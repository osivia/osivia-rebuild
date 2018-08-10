package org.jboss.mx.server.registry;

import java.util.Map;
import javax.management.ObjectName;
import org.jboss.mx.server.MBeanInvoker;
import org.jboss.mx.server.ServerConstants;

public class MBeanEntry implements ServerConstants {
	private ObjectName objectName = null;
	private String resourceClassName = null;
	private MBeanInvoker invoker = null;
	private Object resource = null;
	private ClassLoader cl = null;
	private Map valueMap = null;

	public MBeanEntry(ObjectName objectName, MBeanInvoker invoker, Object resource, Map valueMap) {
		this.objectName = objectName;
		this.invoker = invoker;
		this.resourceClassName = resource.getClass().getName();
		this.resource = resource;
		this.valueMap = valueMap;
		if (valueMap != null) {
			this.cl = (ClassLoader) valueMap.get("org.jboss.mx.classloader");
		}

	}

	public ObjectName getObjectName() {
		return this.objectName;
	}

	protected void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}

	public MBeanInvoker getInvoker() {
		return this.invoker;
	}

	public String getResourceClassName() {
		return this.resourceClassName;
	}

	public void setResourceClassName(String resourceClassName) {
		this.resourceClassName = resourceClassName;
	}

	public Object getResourceInstance() {
		return this.resource;
	}

	public ClassLoader getClassLoader() {
		return this.cl;
	}

	public Object getValue(String key) {
		return this.valueMap != null ? this.valueMap.get(key) : null;
	}
}