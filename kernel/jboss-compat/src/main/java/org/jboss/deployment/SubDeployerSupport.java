package org.jboss.deployment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.management.Notification;
import org.jboss.deployment.SubDeployerSupport.ClassConfiguration;
import org.jboss.deployment.SuffixOrderHelper.EnhancedSuffix;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.system.ServiceMBeanSupport;
import org.jboss.system.server.ServerConfig;
import org.jboss.system.server.ServerConfigLocator;
import org.jboss.system.server.ServerConfigUtil;
import org.jboss.util.file.JarUtils;
import org.jboss.util.stream.Streams;

public abstract class SubDeployerSupport extends ServiceMBeanSupport implements SubDeployerExt, SubDeployerExtMBean {
	protected static final String nativeSuffix;
	protected static final String nativePrefix;
	protected MainDeployerMBean mainDeployer;
	protected File tempDeployDir;
	protected String[] enhancedSuffixes;
	protected String[] suffixes;
	protected int relativeOrder = -1;
	private File tempNativeDir;
	private boolean loadNative = false;
	protected static final ClassConfiguration CONFIGURATION = new ClassConfiguration();

	protected void createService() throws Exception {
		ServerConfig config = ServerConfigLocator.locate();
		this.tempNativeDir = config.getServerNativeDir();
		this.tempDeployDir = config.getServerTempDeployDir();
		this.loadNative = ServerConfigUtil.isLoadNative();
		this.mainDeployer = (MainDeployerMBean) MBeanProxyExt.create(MainDeployerMBean.class,
				MainDeployerMBean.OBJECT_NAME, this.server);
	}

	protected void startService() throws Exception {
		this.mainDeployer.addDeployer(this);
	}

	protected void stopService() throws Exception {
		this.mainDeployer.removeDeployer(this);
	}

	protected void destroyService() throws Exception {
		this.mainDeployer = null;
		this.tempNativeDir = null;
	}

	protected void setSuffixes(String[] suffixes) {
		this.suffixes = suffixes;
	}

	protected void setRelativeOrder(int relativeOrder) {
		this.relativeOrder = relativeOrder;
	}

	public void setEnhancedSuffixes(String[] enhancedSuffixes) {
		if (enhancedSuffixes != null) {
			int len = enhancedSuffixes.length;
			this.suffixes = new String[len];

			for (int i = 0; i < len; ++i) {
				EnhancedSuffix e = new EnhancedSuffix(enhancedSuffixes[i]);
				this.suffixes[i] = e.suffix;
			}
		}

		this.enhancedSuffixes = enhancedSuffixes;
	}

	public String[] getEnhancedSuffixes() {
		return this.enhancedSuffixes;
	}

	public String[] getSuffixes() {
		return this.suffixes;
	}

	public int getRelativeOrder() {
		return this.relativeOrder;
	}

	public boolean accepts(DeploymentInfo sdi) {
		String[] acceptedSuffixes = this.getSuffixes();
		if (acceptedSuffixes == null) {
			return false;
		} else {
			String urlPath = sdi.url.getPath();
			String shortName = sdi.shortName;
			boolean checkDir = sdi.isDirectory && !sdi.isXML && !sdi.isScript;

			for (int i = 0; i < acceptedSuffixes.length; ++i) {
				if (urlPath.endsWith(acceptedSuffixes[i]) || checkDir && shortName.endsWith(acceptedSuffixes[i])) {
					return true;
				}
			}

			return false;
		}
	}

	public void init(DeploymentInfo di) throws DeploymentException {
		this.processNestedDeployments(di);
		this.emitNotification("org.jboss.deployment.SubDeployer.init", di);
	}

	public void create(DeploymentInfo di) throws DeploymentException {
		this.emitNotification("org.jboss.deployment.SubDeployer.create", di);
	}

	public void start(DeploymentInfo di) throws DeploymentException {
		this.emitNotification("org.jboss.deployment.SubDeployer.start", di);
	}

	public void stop(DeploymentInfo di) throws DeploymentException {
		this.emitNotification("org.jboss.deployment.SubDeployer.stop", di);
	}

	public void destroy(DeploymentInfo di) throws DeploymentException {
		this.emitNotification("org.jboss.deployment.SubDeployer.destroy", di);
	}

	protected void emitNotification(String type, DeploymentInfo di) {
		Notification notification = new Notification(type, this, this.getNextNotificationSequenceNumber());
		notification.setUserData(di);
		this.sendNotification(notification);
	}

	protected void processNestedDeployments(DeploymentInfo di) throws DeploymentException {
		this.log.debug("looking for nested deployments in : " + di.url);
		if (!di.isXML) {
			if (di.isDirectory) {
				File f = new File(di.url.getFile());
				if (!f.isDirectory()) {
					throw new DeploymentException("Deploy file incorrectly reported as a directory: " + di.url);
				}

				this.addDeployableFiles(di, f);
			} else {
				try {
					URL nestedURL = JarUtils.extractNestedJar(di.localUrl, this.tempDeployDir);
					JarFile jarFile = new JarFile(nestedURL.getFile());
					this.addDeployableJar(di, jarFile);
				} catch (Exception var4) {
					this.log.warn("Failed to add deployable jar: " + di.localUrl, var4);
					return;
				}
			}

		}
	}

	protected boolean isDeployable(String name, URL url) {
		if (url.getPath().indexOf("META-INF") != -1) {
			return false;
		} else {
			String[] acceptedSuffixes = this.mainDeployer.getSuffixOrder();

			for (int i = 0; i < acceptedSuffixes.length; ++i) {
				if (name.endsWith(acceptedSuffixes[i])) {
					return true;
				}
			}

			return name.endsWith(nativeSuffix) && name.startsWith(nativePrefix);
		}
	}

	protected void addDeployableFiles(DeploymentInfo di, File dir) throws DeploymentException {
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; ++i) {
			File file = files[i];
			String name = file.getName();

			try {
				URL url = file.toURL();
				if (this.isDeployable(name, url)) {
					this.deployUrl(di, url, name);
					continue;
				}
			} catch (MalformedURLException var8) {
				this.log.warn("File name invalid; ignoring: " + file, var8);
			}

			if (file.isDirectory()) {
				this.addDeployableFiles(di, file);
			}
		}

	}

	protected void addDeployableJar(DeploymentInfo di, JarFile jarFile) throws DeploymentException {
		String urlPrefix = "jar:" + di.localUrl.toString() + "!/";
		Enumeration e = jarFile.entries();

		while (e.hasMoreElements()) {
			JarEntry entry = (JarEntry) e.nextElement();
			String name = entry.getName();

			try {
				URL url = new URL(urlPrefix + name);
				if (this.isDeployable(name, url)) {
					URL nestedURL = JarUtils.extractNestedJar(url, this.tempDeployDir);
					this.deployUrl(di, nestedURL, name);
				}
			} catch (MalformedURLException var9) {
				this.log.warn("Jar entry invalid; ignoring: " + name, var9);
			} catch (IOException var10) {
				this.log.warn("Failed to extract nested jar; ignoring: " + name, var10);
			}
		}

	}

	protected void deployUrl(DeploymentInfo di, URL url, String name) throws DeploymentException {
		this.log.debug("nested deployment: " + url);

		try {
			if (name.endsWith(nativeSuffix) && name.startsWith(nativePrefix)) {
				File destFile = new File(this.tempNativeDir, name);
				this.log.info("Loading native library: " + destFile.toString());
				File parent = destFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}

				InputStream in = url.openStream();
				OutputStream out = new FileOutputStream(destFile);
				Streams.copyb(in, out);
				out.flush();
				out.close();
				in.close();
				if (this.loadNative) {
					System.load(destFile.toString());
				}
			} else {
				new DeploymentInfo(url, di, this.getServer());
			}

		} catch (Exception var8) {
			throw new DeploymentException("Could not deploy sub deployment " + name + " of deployment " + di.url, var8);
		}
	}

	static {
		String token = CONFIGURATION.getNativeLibToken();
		String nativex = System.mapLibraryName(token);
		int xPos = nativex.indexOf(token);
		nativePrefix = nativex.substring(0, xPos);
		nativeSuffix = nativex.substring(xPos + 3);
	}
}