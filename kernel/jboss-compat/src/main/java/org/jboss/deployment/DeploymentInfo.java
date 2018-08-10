package org.jboss.deployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.jboss.logging.Logger;
import org.jboss.mx.loading.LoaderRepositoryFactory;
import org.jboss.mx.loading.RepositoryClassLoader;
import org.jboss.mx.loading.LoaderRepositoryFactory.LoaderRepositoryConfig;
import org.jboss.util.collection.ListSet;
import org.jboss.util.file.Files;
import org.w3c.dom.Document;

public class DeploymentInfo implements Serializable {
	private static final long serialVersionUID = 1131841473723490707L;
	private static final Logger log;
	public Date date = new Date();
	public URL url;
	public URL localUrl;
	public URL watch;
	public String shortName;
	public long lastDeployed = 0L;
	public long lastModified = 0L;
	public String status;
	public DeploymentState state;
	public transient SubDeployer deployer;
	public transient RepositoryClassLoader ucl;
	public transient URLClassLoader localCl;
	public transient URLClassLoader annotationsCl;
	public final Collection classpath;
	public final List mbeans;
	public final Set subDeployments;
	public DeploymentInfo parent;
	public String webContext;
	public transient Manifest manifest;
	public Document document;
	public URL documentUrl;
	public transient Object metaData;
	public String alternativeDD;
	public transient HashMap context;
	public boolean isXML;
	public boolean isScript;
	public boolean isDirectory;
	public boolean sortedSubDeployments;
	public ObjectName deployedObject;
	public LoaderRepositoryConfig repositoryConfig;
	private transient MBeanServer server;

	public DeploymentInfo(URL url, DeploymentInfo parent, MBeanServer server) throws DeploymentException {
		this.state = DeploymentState.CONSTRUCTED;
		this.classpath = new ArrayList();
		this.mbeans = new ArrayList();
		this.subDeployments = new ListSet();
		this.parent = null;
		this.context = new HashMap();
		this.server = server;
		this.url = url;
		this.watch = url;
		this.parent = parent;
		if (url.getProtocol().startsWith("file") && (new File(url.getFile())).isDirectory()) {
			this.isDirectory = true;
		}

		if (!this.isDirectory) {
			try {
				url.openStream().close();
			} catch (Exception var5) {
				throw new DeploymentException("url " + url + " could not be opened, does it exist?");
			}
		}

		if (parent != null) {
			parent.subDeployments.add(this);
			this.repositoryConfig = this.getTopRepositoryConfig();
		}

		this.shortName = this.getShortName(url.getPath());
		this.isXML = this.shortName.toLowerCase().endsWith(".xml");
		this.isScript = this.shortName.toLowerCase().endsWith(".bsh");
	}

	public MBeanServer getServer() {
		return this.server;
	}

	public void setServer(MBeanServer server) {
		this.server = server;
	}

	public void createClassLoaders() throws Exception {
		if (this.localCl == null) {
			this.localCl = new URLClassLoader(new URL[]{this.localUrl});
		}

		URL origUrl = this.url;

		DeploymentInfo current;
		for (current = this; current.parent != null; current = current.parent) {
			;
		}

		origUrl = current.url;
		this.repositoryConfig = current.repositoryConfig;
		if (this.parent == null) {
			if (this.repositoryConfig == null) {
				this.repositoryConfig = new LoaderRepositoryConfig();
			}

			LoaderRepositoryFactory.createLoaderRepository(this.server, this.repositoryConfig);
			log.debug("createLoaderRepository from config: " + this.repositoryConfig);
			Object[] args = new Object[]{this.isXML ? null : this.localUrl, origUrl, Boolean.TRUE};
			String[] sig = new String[]{"java.net.URL", "java.net.URL", "boolean"};
			this.ucl = (RepositoryClassLoader) this.server.invoke(this.repositoryConfig.repositoryName,
					"newClassLoader", args, sig);
		} else {
			LoaderRepositoryFactory.createLoaderRepository(this.server, this.repositoryConfig);
			this.ucl = this.parent.ucl;
			this.ucl.addURL(this.localUrl);
		}

		if (this.classpath.size() > 0) {
			Iterator jars = this.classpath.iterator();

			while (jars.hasNext()) {
				URL jar = (URL) jars.next();
				this.ucl.addURL(jar);
			}
		}

	}

	public void setRepositoryInfo(LoaderRepositoryConfig config) throws Exception {
		if (this.parent != null) {
			log.warn("Only the root deployment can set the loader repository, ignoring config=" + config);
		} else {
			this.repositoryConfig = config;
			if (this.ucl != null) {
				this.ucl.unregister();
				LoaderRepositoryFactory.createLoaderRepository(this.server, this.repositoryConfig);
				log.debug("createLoaderRepository from config: " + this.repositoryConfig);
				Object[] args = new Object[]{this.isXML ? null : this.localUrl, this.url, Boolean.TRUE};
				String[] sig = new String[]{"java.net.URL", "java.net.URL", "boolean"};
				this.ucl = (RepositoryClassLoader) this.server.invoke(this.repositoryConfig.repositoryName,
						"newClassLoader", args, sig);
			}

		}
	}

	public void addLibraryJar(URL libJar) {
		DeploymentInfo current;
		for (current = this; current.parent != null; current = current.parent) {
			;
		}

		if (current.ucl != null) {
			current.ucl.addURL(libJar);
		} else {
			this.classpath.add(libJar);
		}

	}

	public LoaderRepositoryConfig getTopRepositoryConfig() {
		LoaderRepositoryConfig topConfig = this.repositoryConfig;

		for (DeploymentInfo info = this; info.parent != null; topConfig = info.repositoryConfig) {
			info = info.parent;
		}

		return topConfig;
	}

	public Manifest getManifest() {
		try {
			if (this.manifest == null) {
				File file = new File(this.localUrl.getFile());
				if (file.isDirectory()) {
					FileInputStream fis = new FileInputStream(new File(file, "META-INF/MANIFEST.MF"));
					this.manifest = new Manifest(fis);
					fis.close();
				} else if (!this.isXML) {
					this.manifest = (new JarFile(file)).getManifest();
				}
			}

			return this.manifest;
		} catch (Exception var3) {
			return null;
		}
	}

	public void cleanup() {
		if (this.parent == null && this.ucl != null) {
			this.ucl.unregister();
		}

		this.ucl = null;
		if (this.repositoryConfig != null) {
			LoaderRepositoryFactory.destroyLoaderRepository(this.server, this.repositoryConfig.repositoryName);
		}

		this.subDeployments.clear();
		this.mbeans.clear();
		this.context.clear();
		if (this.localUrl != null && !this.localUrl.equals(this.url)) {
			if (Files.delete(this.localUrl.getFile())) {
				log.debug("Cleaned Deployment: " + this.localUrl);
			} else {
				log.debug("Could not delete " + this.localUrl + " restart will delete it");
			}
		} else {
			log.debug("Not deleting localUrl, it is null or not a copy: " + this.localUrl);
		}

		this.localCl = null;
		this.annotationsCl = null;
		this.localUrl = null;
		this.repositoryConfig = null;
		this.watch = null;
		this.parent = null;
		this.manifest = null;
		this.document = null;
		this.metaData = null;
		this.server = null;
		this.classpath.clear();
		this.state = DeploymentState.DESTROYED;
	}

	public String getCanonicalName() {
		String name = this.shortName;
		if (this.parent != null) {
			name = this.parent.getCanonicalName() + "/" + name;
		}

		return name;
	}

	private String getShortName(String name) {
		if (name.endsWith("/")) {
			name = name.substring(0, name.length() - 1);
		}

		name = name.substring(name.lastIndexOf("/") + 1);
		return name;
	}

	public int hashCode() {
		return this.url.hashCode();
	}

	public boolean equals(Object other) {
		return other instanceof DeploymentInfo ? ((DeploymentInfo) other).url.equals(this.url) : false;
	}

	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" { url=" + this.url + " }\n");
		s.append("  deployer: " + this.deployer + "\n");
		s.append("  status: " + this.status + "\n");
		s.append("  state: " + this.state + "\n");
		s.append("  watch: " + this.watch + "\n");
		s.append("  altDD: " + this.alternativeDD + "\n");
		s.append("  lastDeployed: " + this.lastDeployed + "\n");
		s.append("  lastModified: " + this.lastModified + "\n");
		s.append("  mbeans:\n");
		Iterator i = this.mbeans.iterator();

		while (i.hasNext()) {
			ObjectName o = (ObjectName) i.next();

			try {
				String state = (String) this.server.getAttribute(o, "StateString");
				s.append("    " + o + " state: " + state + "\n");
			} catch (Exception var5) {
				s.append("    " + o + " (state not available)\n");
			}
		}

		return s.toString();
	}

	static {
		log = Logger.getLogger(DeploymentInfo.class);
	}
}