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
	public Object repositoryConfig;
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
		
	}

	public void setRepositoryInfo(Object config) throws Exception {
		
	}

	public void addLibraryJar(URL libJar) {
	
	}

	public Object getTopRepositoryConfig() {
		return null;
	
	}

	public Manifest getManifest() {
		return null;
	}

	public void cleanup() {
		
	}

	public String getCanonicalName() {
		return null;
	}

	private String getShortName(String name) {
	      if (name.endsWith("/")) name = name.substring(0, name.length() - 1);
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