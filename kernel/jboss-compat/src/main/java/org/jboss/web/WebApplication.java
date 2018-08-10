package org.jboss.web;

import java.net.URL;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.metadata.WebMetaData;

public class WebApplication {
	ClassLoader classLoader = null;
	String name = "";
	URL url;
	WebMetaData metaData;
	Object data;
	DeploymentInfo warInfo;

	public WebApplication() {
	}

	public WebApplication(WebMetaData metaData) {
		this.metaData = metaData;
	}

	public WebApplication(String name, URL url, ClassLoader classLoader) {
		this.name = name;
		this.url = url;
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public String getName() {
		String n = this.name;
		if (n == null) {
			n = this.url.getFile();
		}

		return n;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URL getURL() {
		return this.url;
	}

	public void setURL(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("Null URL");
		} else {
			this.url = url;
		}
	}

	public WebMetaData getMetaData() {
		return this.metaData;
	}

	public void setMetaData(WebMetaData metaData) {
		this.metaData = metaData;
	}

	public Object getAppData() {
		return this.data;
	}

	public void setAppData(Object data) {
		this.data = data;
	}

	public DeploymentInfo getDeploymentInfo() {
		return this.warInfo;
	}

	public void setDeploymentInfo(DeploymentInfo warInfo) {
		this.warInfo = warInfo;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("{WebApplication: ");
		buffer.append(this.getName());
		buffer.append(", URL: ");
		buffer.append(this.url);
		buffer.append(", classLoader: ");
		buffer.append(this.classLoader);
		buffer.append(':');
		buffer.append(this.classLoader.hashCode());
		buffer.append('}');
		return buffer.toString();
	}
}