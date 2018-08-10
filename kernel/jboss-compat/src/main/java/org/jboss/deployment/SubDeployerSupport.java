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
import org.jboss.system.ServiceMBeanSupport;
import org.jboss.util.file.JarUtils;
import org.jboss.util.stream.Streams;

public abstract class SubDeployerSupport extends ServiceMBeanSupport implements SubDeployerExt, SubDeployerExtMBean {

	protected MainDeployerMBean mainDeployer;
	protected File tempDeployDir;
	protected String[] enhancedSuffixes;
	protected String[] suffixes;
	protected int relativeOrder = -1;
	private File tempNativeDir;
	private boolean loadNative = false;

	protected void createService() throws Exception {
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
		return true;
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
	
	}


}