package org.jboss.mx.modelmbean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.jboss.logging.Logger;
import org.jboss.mx.interceptor.Interceptor;




import org.jboss.mx.server.AbstractMBeanInvoker;
import org.jboss.mx.server.Invocation;
import org.jboss.mx.server.InvocationContext;
import org.jboss.mx.server.MBeanInvoker;

import org.jboss.mx.util.JBossNotificationBroadcasterSupport;

public abstract class ModelMBeanInvoker extends AbstractMBeanInvoker implements ModelMBean {
	Logger log = Logger.getLogger(ModelMBeanInvoker.class.getName());
	protected String resourceType = null;
	protected JBossNotificationBroadcasterSupport notifier = new JBossNotificationBroadcasterSupport();
	protected long notifierSequence = 1L;
	protected long attrNotifierSequence = 1L;

	public ModelMBeanInvoker() {
	}

	public ModelMBeanInvoker(ModelMBeanInfo info) throws MBeanException {
		this.setModelMBeanInfo(info);
	}

	public void setModelMBeanInfo(ModelMBeanInfo info) throws MBeanException, RuntimeOperationsException {
		if (info == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("MBeanInfo cannot be null"));
		} else {
			this.info = new ModelMBeanInfoSupport(info);
			ModelMBeanInfo minfo = info;
			Descriptor mbeanDescriptor = null;

			try {
				mbeanDescriptor = minfo.getDescriptor("", "mbean");
			} catch (MBeanException var5) {
				this.log.warn("Failed to obtain descriptor: mbean", var5);
				return;
			}

			String type = (String) mbeanDescriptor.getFieldValue("MBeanInfoType");
			if (type != null) {
				this.inject("MBeanInfoType", type, MBeanInfo.class, info);
			}

		}
	}

	public void setManagedResource(Object ref, String resourceType)
			throws MBeanException, InstanceNotFoundException, InvalidTargetObjectTypeException {
		if (!this.isSupportedResourceType(ref, resourceType)) {
			throw new InvalidTargetObjectTypeException("Unsupported resource type: " + resourceType);
		} else {
			this.setResource(ref);
			this.resourceType = resourceType;
			if (this.getServer() != null) {
				try {
					this.init(this.getServer(), this.resourceEntry.getObjectName());
				} catch (Exception var4) {
					throw new MBeanException(var4, "Failed to init from resource");
				}
			}

		}
	}

	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
		this.notifier.addNotificationListener(listener, filter, handback);
	}

	public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
		this.notifier.removeNotificationListener(listener);
	}

	public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		this.notifier.removeNotificationListener(listener, filter, handback);
	}

	public void sendNotification(String ntfyText) throws MBeanException, RuntimeOperationsException {
		if (ntfyText == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("ntfyText cannot be null"));
		} else {
			Notification notif = new Notification("jmx.modelmbean.generic", this, 1L, ntfyText);
			this.sendNotification(notif);
		}
	}

	public void sendNotification(Notification ntfyObj) throws MBeanException, RuntimeOperationsException {
		if (ntfyObj == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("ntfyText cannot be null"));
		} else {
			this.notifier.sendNotification(ntfyObj);
		}
	}

	public void sendAttributeChangeNotification(AttributeChangeNotification notification) throws MBeanException {
		if (notification == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("notification cannot be null"));
		} else {
			this.notifier.sendNotification(notification);
		}
	}

	public void sendAttributeChangeNotification(Attribute oldValue, Attribute newValue)
			throws MBeanException, RuntimeOperationsException {
		if (oldValue != null && newValue != null) {
			if (!oldValue.getName().equals(newValue.getName())) {
				throw new RuntimeOperationsException(
						new IllegalArgumentException("Attribute name mismatch between oldvalue and newvalue"));
			} else {
				String attr = oldValue.getName();
				String type = ((ModelMBeanInfo) this.info).getAttribute(attr).getType();
				AttributeChangeNotification notif = new AttributeChangeNotification(this, 1L,
						System.currentTimeMillis(), "" + attr + " changed from " + oldValue + " to " + newValue, attr,
						type, oldValue.getValue(), newValue.getValue());
				this.notifier.sendNotification(notif);
			}
		} else {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"));
		}
	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return this.info.getNotifications();
	}

	public void addAttributeChangeNotificationListener(NotificationListener listener, String attributeName,
			Object handback) throws MBeanException {
		ModelMBeanInfo minfo = (ModelMBeanInfo) this.info;
		AttributeChangeNotificationFilter filter = null;
		if (attributeName != null) {
			ModelMBeanAttributeInfo ainfo = minfo.getAttribute(attributeName);
			if (ainfo == null) {
				throw new RuntimeOperationsException(
						new IllegalArgumentException("Attribute does not exist: " + attributeName));
			}

			filter = new AttributeChangeNotificationFilter();
			filter.enableAttribute(attributeName);
		} else {
			filter = new AttributeChangeNotificationFilter();
			MBeanAttributeInfo[] allAttributes = minfo.getAttributes();

			for (int i = 0; i < allAttributes.length; ++i) {
				filter.enableAttribute(allAttributes[i].getName());
			}
		}

		this.notifier.addNotificationListener(listener, filter, handback);
	}

	public void removeAttributeChangeNotificationListener(NotificationListener listener, String attributeName)
			throws MBeanException, ListenerNotFoundException {
		if (attributeName != null) {
			ModelMBeanInfo minfo = (ModelMBeanInfo) this.info;
			ModelMBeanAttributeInfo ainfo = minfo.getAttribute(attributeName);
			if (ainfo == null) {
				throw new RuntimeOperationsException(
						new IllegalArgumentException("Attribute does not exist: " + attributeName));
			}
		}

		this.notifier.removeNotificationListener(listener);
	}

	public void load() throws MBeanException, InstanceNotFoundException {

	}

	public void store() throws MBeanException, InstanceNotFoundException {

	}

	public ObjectName invokePreRegister(MBeanServer server, ObjectName name) throws Exception {
     return null;
   }

	protected void init(MBeanServer server, ObjectName name) throws Exception {

	}

	protected void initPersistence(MBeanServer server, ObjectName name)
			throws MBeanException, InstanceNotFoundException {

	}

	protected void initOperationContexts(MBeanOperationInfo[] operations) {

	}

	protected void initAttributeContexts(MBeanAttributeInfo[] attributes) {
		
	}

	protected void configureInterceptorStack(ModelMBeanInfo info, MBeanServer server, ObjectName name)
			throws Exception {
	

	}

	protected List getInterceptors(Descriptor d) throws Exception {
		return null;
	}

	protected boolean isSupportedResourceType(Object resource, String resourceType) {
		return resourceType.equalsIgnoreCase("ObjectReference");
	}

	protected void override(Invocation invocation) throws MBeanException {
		

	}
}