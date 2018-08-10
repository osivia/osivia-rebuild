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
import org.jboss.mx.interceptor.ModelMBeanAttributeInterceptor;
import org.jboss.mx.interceptor.ModelMBeanInfoInterceptor;
import org.jboss.mx.interceptor.ModelMBeanInterceptor;
import org.jboss.mx.interceptor.ModelMBeanOperationInterceptor;
import org.jboss.mx.interceptor.NullInterceptor;
import org.jboss.mx.interceptor.ObjectReferenceInterceptor;
import org.jboss.mx.interceptor.PersistenceInterceptor;
import org.jboss.mx.interceptor.PersistenceInterceptor2;
import org.jboss.mx.modelmbean.ModelMBeanInvoker.1;
import org.jboss.mx.modelmbean.ModelMBeanInvoker.2;
import org.jboss.mx.persistence.NullPersistence;
import org.jboss.mx.persistence.PersistenceManager;
import org.jboss.mx.server.AbstractMBeanInvoker;
import org.jboss.mx.server.Invocation;
import org.jboss.mx.server.InvocationContext;
import org.jboss.mx.server.MBeanInvoker;
import org.jboss.mx.server.AbstractMBeanInvoker.OperationKey;
import org.jboss.mx.util.JBossNotificationBroadcasterSupport;

public abstract class ModelMBeanInvoker extends AbstractMBeanInvoker implements ModelMBean, ModelMBeanConstants {
	Logger log = Logger.getLogger(ModelMBeanInvoker.class.getName());
	protected String resourceType = null;
	protected PersistenceManager persistence = new NullPersistence();
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
		if (this.info != null) {
			this.persistence.load(this, this.info);
		}
	}

	public void store() throws MBeanException, InstanceNotFoundException {
		this.persistence.store(this.info);
	}

	public ObjectName invokePreRegister(MBeanServer server, ObjectName name) throws Exception {
      if (this.info == null) {
         throw new RuntimeErrorException(new Error("MBeanInfo has not been set."));
      } else {
         ModelMBeanInfo minfo = (ModelMBeanInfo)this.info;
         Descriptor mbeanDescriptor = minfo.getMBeanDescriptor();
         this.getMBeanInfoCtx = new InvocationContext();
         this.getMBeanInfoCtx.setInvoker(this);
         this.getMBeanInfoCtx.setDescriptor(mbeanDescriptor);
         this.getMBeanInfoCtx.setDispatcher(new 1(this, "MBeanInfo Dispatcher", minfo));
         String[] signature = new String[]{"java.lang.Object", "java.lang.String"};
         OperationKey opKey = new OperationKey(this, "setManagedResource", signature);
         InvocationContext ctx = new InvocationContext();
         ctx.setInvoker(this);
         ctx.setDispatcher(new 2(this, "SetMangedResource Dispatcher"));
         this.operationContextMap.put(opKey, ctx);
         if (this.getResource() == null) {
            return name;
         } else {
            this.init(server, name);
            return super.invokePreRegister(server, name);
         }
      }
   }

	protected void init(MBeanServer server, ObjectName name) throws Exception {
		ModelMBeanInfo minfo = (ModelMBeanInfo) this.info;
		this.configureInterceptorStack(minfo, server, name);
		this.initDispatchers();
		Object resource = this.getResource();
		if (resource != null) {
			Descriptor mbeanDescriptor = minfo.getMBeanDescriptor();
			String resClassName = this.getResource().getClass().getName();
			mbeanDescriptor.setField("resourceClass", resClassName);
			minfo.setMBeanDescriptor(mbeanDescriptor);
		}

		this.setValuesFromMBeanInfo();
		this.initPersistence(server, name);
		this.load();
	}

	protected void initPersistence(MBeanServer server, ObjectName name)
			throws MBeanException, InstanceNotFoundException {
		ModelMBeanInfo minfo = (ModelMBeanInfo) this.getMetaData();

		Descriptor[] descriptors;
		try {
			descriptors = minfo.getDescriptors("mbean");
		} catch (MBeanException var8) {
			this.log.error("Failed to obtain MBEAN_DESCRIPTORs", var8);
			return;
		}

		if (descriptors != null) {
			String persistMgrName = null;

			for (int i = 0; i < descriptors.length && persistMgrName == null; ++i) {
				persistMgrName = (String) descriptors[i].getFieldValue("persistence-manager");
			}

			if (persistMgrName == null) {
				this.log.trace("No persistence-manager descriptor found, null persistence will be used");
			} else {
				try {
					this.persistence = (PersistenceManager) server.instantiate(persistMgrName);
					this.log.debug("Loaded persistence mgr: " + persistMgrName);
					Descriptor descriptor = minfo.getMBeanDescriptor();
					descriptor.setField("objectname", name);
					minfo.setMBeanDescriptor(descriptor);
				} catch (Exception var7) {
					this.log.error("Unable to instantiate the persistence manager:" + persistMgrName, var7);
				}

			}
		}
	}

	protected void initOperationContexts(MBeanOperationInfo[] operations) {
		super.initOperationContexts(operations);

		for (int i = 0; i < operations.length; ++i) {
			OperationKey key = new OperationKey(this, operations[i]);
			InvocationContext ctx = (InvocationContext) this.operationContextMap.get(key);
			ModelMBeanOperationInfo info = (ModelMBeanOperationInfo) operations[i];
			ctx.setDescriptor(info.getDescriptor());
		}

	}

	protected void initAttributeContexts(MBeanAttributeInfo[] attributes) {
		super.initAttributeContexts(attributes);

		for (int i = 0; i < attributes.length; ++i) {
			ModelMBeanAttributeInfo info = (ModelMBeanAttributeInfo) attributes[i];
			String name = info.getName();
			InvocationContext ctx = (InvocationContext) this.attributeContextMap.get(name);
			ctx.setDescriptor(info.getDescriptor());
			ctx.setReadable(info.isReadable());
			ctx.setWritable(info.isWritable());
		}

	}

	protected void configureInterceptorStack(ModelMBeanInfo info, MBeanServer server, ObjectName name)
			throws Exception {
		List defaultInterceptors = this.getInterceptors(this.getMBeanInfoCtx.getDescriptor());
		List interceptors = null;
		if (defaultInterceptors != null) {
			interceptors = new ArrayList(defaultInterceptors);
		}

		if (interceptors == null) {
			interceptors = this.getMBeanInfoCtx.getInterceptors();
		}

		String mbeanName = name != null ? name.toString() : info.getClassName();
		((List) interceptors).add(new ModelMBeanInfoInterceptor(mbeanName));
		this.getMBeanInfoCtx.setInterceptors((List) interceptors);
		Iterator it = this.attributeContextMap.entrySet().iterator();

		Entry entry;
		InvocationContext ctx;
		Object list;
		while (it.hasNext()) {
			entry = (Entry) it.next();
			ctx = (InvocationContext) entry.getValue();
			list = this.getInterceptors(ctx.getDescriptor());
			if (list == null) {
				if (defaultInterceptors != null) {
					list = new ArrayList(defaultInterceptors);
				} else {
					list = new ArrayList();
				}
			}

			((List) list).add(new PersistenceInterceptor());
			((List) list).add(new ModelMBeanAttributeInterceptor());
			ctx.setInterceptors((List) list);
		}

		it = this.operationContextMap.entrySet().iterator();

		while (it.hasNext()) {
			entry = (Entry) it.next();
			ctx = (InvocationContext) entry.getValue();
			list = this.getInterceptors(ctx.getDescriptor());
			if (list == null && defaultInterceptors != null) {
				list = new ArrayList(defaultInterceptors);
			}

			if (this.dynamicResource) {
				if (list == null) {
					list = new ArrayList();
				}

				((List) list).add(new ModelMBeanOperationInterceptor());
			}

			if (list != null) {
				((List) list).add(new NullInterceptor());
				ctx.setInterceptors((List) list);
			}
		}

	}

	protected List getInterceptors(Descriptor d) throws Exception {
		if (d == null) {
			return null;
		} else {
			Descriptor[] interceptorDescriptors = (Descriptor[]) ((Descriptor[]) d.getFieldValue("interceptors"));
			if (interceptorDescriptors == null) {
				return null;
			} else {
				ArrayList interceptors = new ArrayList();
				ClassLoader loader = Thread.currentThread().getContextClassLoader();

				for (int i = 0; i < interceptorDescriptors.length; ++i) {
					Descriptor desc = interceptorDescriptors[i];
					String code = (String) desc.getFieldValue("code");
					if (!code.equals(ModelMBeanInterceptor.class.getName())
							&& !code.equals(ObjectReferenceInterceptor.class.getName())
							&& !code.equals(PersistenceInterceptor2.class.getName())) {
						Class interceptorClass = loader.loadClass(code);
						Interceptor interceptor = null;
						Class[] ctorSig = new Class[]{MBeanInvoker.class};

						try {
							Constructor ctor = interceptorClass.getConstructor(ctorSig);
							Object[] ctorArgs = new Object[]{this};
							interceptor = (Interceptor) ctor.newInstance(ctorArgs);
						} catch (Throwable var23) {
							this.log.debug("Could not invoke CTOR(MBeanInvoker) for '" + interceptorClass
									+ "', trying default CTOR: " + var23.getMessage());
							interceptor = (Interceptor) interceptorClass.newInstance();
						}

						interceptors.add(interceptor);
						String[] names = desc.getFieldNames();
						HashMap propertyMap = new HashMap();
						if (names.length > 1) {
							BeanInfo beanInfo = Introspector.getBeanInfo(interceptorClass);
							PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();

							int n;
							String name;
							for (n = 0; n < props.length; ++n) {
								name = props[n].getName();
								propertyMap.put(name, props[n]);
							}

							for (n = 0; n < names.length; ++n) {
								name = names[n];
								if (!name.equals("code")) {
									String text = (String) desc.getFieldValue(name);
									PropertyDescriptor pd = (PropertyDescriptor) propertyMap.get(name);
									if (pd == null) {
										throw new IntrospectionException("No PropertyDescriptor for attribute:" + name);
									}

									Method setter = pd.getWriteMethod();
									if (setter != null) {
										Class ptype = pd.getPropertyType();
										PropertyEditor editor = PropertyEditorManager.findEditor(ptype);
										if (editor == null) {
											throw new IntrospectionException(
													"Cannot convert string to interceptor attribute:" + name);
										}

										editor.setAsText(text);
										Object[] args = new Object[]{editor.getValue()};
										setter.invoke(interceptor, args);
									}
								}
							}
						}
					} else {
						this.log.debug("Ignoring obsolete legacy interceptor: " + code);
					}
				}

				if (interceptors.size() == 0) {
					interceptors = null;
				}

				return interceptors;
			}
		}
	}

	protected void setValuesFromMBeanInfo() throws JMException {
		Iterator it = this.attributeContextMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			InvocationContext ctx = (InvocationContext) entry.getValue();
			Object value = ctx.getDescriptor().getFieldValue("value");
			if (value != null) {
				this.setAttribute(new Attribute(key, value));
			}
		}

	}

	protected boolean isSupportedResourceType(Object resource, String resourceType) {
		return resourceType.equalsIgnoreCase("ObjectReference");
	}

	protected void override(Invocation invocation) throws MBeanException {
		if (this.dynamicResource && this.info != null) {
			Descriptor current = invocation.getDescriptor();
			if (current != null) {
				ModelMBeanInfo mminfo = (ModelMBeanInfo) this.info;
				Descriptor descriptor = mminfo.getDescriptor((String) current.getFieldValue("name"),
						(String) current.getFieldValue("descriptorType"));
				if (descriptor != null) {
					invocation.setDescriptor(descriptor);
				}
			}
		}

	}
}