package org.jboss.mx.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import org.jboss.logging.Logger;
import org.jboss.mx.interceptor.Interceptor;
import org.jboss.mx.server.registry.MBeanEntry;
import org.jboss.util.Strings;

public abstract class AbstractMBeanInvoker  {
	static ThreadLocal preRegisterInfo = new ThreadLocal();
	private Object resource = null;
	protected MBeanEntry resourceEntry = null;
	protected boolean dynamicResource = true;
	protected MBeanInfo info = null;
	protected Map attributeContextMap = new HashMap();
	protected Map operationContextMap = new HashMap();
	protected Map constructorContextMap = new HashMap();
	protected InvocationContext getMBeanInfoCtx = null;
	protected InvocationContext preRegisterCtx = null;
	protected InvocationContext postRegisterCtx = null;
	protected InvocationContext preDeregisterCtx = null;
	protected InvocationContext postDeregisterCtx = null;
	protected Logger log = Logger.getLogger(AbstractMBeanInvoker.class);
	private MBeanServer server;

	public static void setMBeanEntry(MBeanEntry entry) {
		preRegisterInfo.set(entry);
	}

	public static MBeanEntry getMBeanEntry() {
		return (MBeanEntry) preRegisterInfo.get();
	}

	public AbstractMBeanInvoker() {
	}

	public AbstractMBeanInvoker(Object resource) {
		this.resource = resource;
	}

	public AbstractMBeanInvoker(MBeanEntry resourceEntry) {
		this.resourceEntry = resourceEntry;
		this.resource = resourceEntry.getResourceInstance();
	}

	public Object invoke(String operationName, Object[] args, String[] signature)
			throws MBeanException, ReflectionException {
		return null;
	}

	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Cannot get null attribute"));
		} 

		return null;
	}

	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		
	}

	public MBeanInfo getMBeanInfo() {
		
		return null;
	}

	public AttributeList getAttributes(String[] attributes) {
		if (attributes == null) {
			throw new IllegalArgumentException("null array");
		} else {
			AttributeList list = new AttributeList();

			for (int i = 0; i < attributes.length; ++i) {
				try {
					list.add(new Attribute(attributes[i], this.getAttribute(attributes[i])));
				} catch (Throwable var5) {
					;
				}
			}

			return list;
		}
	}

	public AttributeList setAttributes(AttributeList attributes) {
		if (attributes == null) {
			throw new IllegalArgumentException("null list");
		} else {
			AttributeList results = new AttributeList();
			Iterator it = attributes.iterator();

			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();

				try {
					this.setAttribute(attr);
					results.add(attr);
				} catch (Throwable var6) {
					if (this.log.isTraceEnabled()) {
						this.log.trace("Unhandled setAttribute() for attribute: " + attr.getName(), var6);
					}
				}
			}

			return results;
		}
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
		return null;
	}

	public void postRegister(Boolean registrationSuccessful) {
		this.invokePostRegister(registrationSuccessful);
	}

	public void preDeregister() throws Exception {
		this.invokePreDeregister();
	}

	public void postDeregister() {
		this.invokePostDeregister();
		this.server = null;
	}

	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
		this.addNotificationListenerToResource(listener, filter, handback);
	}

	protected void addNotificationListenerToResource(NotificationListener listener, NotificationFilter filter,
			Object handback) {
		if (this.resource instanceof NotificationBroadcaster) {
			((NotificationBroadcaster) this.resource).addNotificationListener(listener, filter, handback);
		} else {
			throw new RuntimeMBeanException(
					new IllegalArgumentException("Target XXX is not a notification broadcaster"));
		}
	}

	public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
		this.removeNotificationListenerFromResource(listener);
	}

	protected void removeNotificationListenerFromResource(NotificationListener listener)
			throws ListenerNotFoundException {
		if (this.resource instanceof NotificationBroadcaster) {
			((NotificationBroadcaster) this.resource).removeNotificationListener(listener);
		} else {
			throw new RuntimeMBeanException(
					new IllegalArgumentException("Target XXX is not a notification broadcaster"));
		}
	}

	public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		this.removeNotificationListenerFromResource(listener, filter, handback);
	}

	protected void removeNotificationListenerFromResource(NotificationListener listener, NotificationFilter filter,
			Object handback) throws ListenerNotFoundException {
		if (this.resource instanceof NotificationEmitter) {
			((NotificationEmitter) this.resource).removeNotificationListener(listener, filter, handback);
		} else {
			if (!(this.resource instanceof NotificationBroadcaster)) {
				throw new RuntimeMBeanException(
						new IllegalArgumentException("Target XXX is not a notification emitter"));
			}

			this.removeNotificationListener(listener);
		}

	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return this.getNotificationInfoFromResource();
	}

	protected MBeanNotificationInfo[] getNotificationInfoFromResource() {
		return this.resource instanceof NotificationBroadcaster
				? ((NotificationBroadcaster) this.resource).getNotificationInfo()
				: new MBeanNotificationInfo[0];
	}

	public MBeanInfo getMetaData() {
		return this.info;
	}

	public Object getResource() {
		return this.resource;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}

	public ObjectName getObjectName() {
		return this.resourceEntry == null ? null : this.resourceEntry.getObjectName();
	}

	public void updateAttributeInfo(Descriptor attrDesc) throws MBeanException {
		ModelMBeanInfoSupport minfo = (ModelMBeanInfoSupport) this.info;
		minfo.setDescriptor(attrDesc, "attribute");
	}

	public void addOperationInterceptor(Interceptor interceptor) {
		

	}

	public void removeOperationInterceptor(Interceptor interceptor) {
		

	}

	public void suspend() {
	}

	public void suspend(long wait) {
	}

	public void suspend(boolean force) {
	}

	public boolean isSuspended() {
		return false;
	}

	public void setInvocationTimeout(long time) {
	}

	public long getInvocationTimeout() {
		return 0L;
	}

	public void resume() {
	}

	public MBeanServer getServer() {
		return this.server;
	}

	protected void inject(String type, String name, Class argType, Object value) {
		try {
			Class resClass = this.resource.getClass();
			Class[] sig = new Class[]{argType};
			Method setter = resClass.getMethod(name, sig);
			Object[] args = new Object[]{value};
			setter.invoke(this.resource, args);
		} catch (NoSuchMethodException var9) {
			this.log.debug("Setter not found: " + name + "(" + argType + ")", var9);
		} catch (Exception var10) {
			this.log.warn("Failed to inject type: " + type + " using setter: " + name, var10);
		}

	}

	protected ObjectName invokePreRegister(MBeanServer server, ObjectName name) throws Exception {
		return this.resource instanceof MBeanRegistration
				? ((MBeanRegistration) this.resource).preRegister(server, name)
				: name;
	}

	protected void invokePostRegister(Boolean b) {
		if (this.resource instanceof MBeanRegistration) {
			((MBeanRegistration) this.resource).postRegister(b);
		}

	}

	protected void invokePreDeregister() throws Exception {
		if (this.resource instanceof MBeanRegistration) {
			((MBeanRegistration) this.resource).preDeregister();
		}

	}

	protected void invokePostDeregister() {
		if (this.resource instanceof MBeanRegistration) {
			((MBeanRegistration) this.resource).postDeregister();
		}

	}

	protected void initAttributeContexts(MBeanAttributeInfo[] attributes) {
		for (int i = 0; i < attributes.length; ++i) {
			InvocationContext ctx = new InvocationContext();
			ctx.setName(attributes[i].getName());
			ctx.setAttributeType(attributes[i].getType());
			ctx.setInvoker(this);
			this.attributeContextMap.put(attributes[i].getName(), ctx);
		}

		if (this.log.isTraceEnabled()) {
			this.log.trace(this.getObjectName() + " configured attribute contexts: " + this.operationContextMap);
		}

	}

	protected void initOperationContexts(MBeanOperationInfo[] operations) {
		
	}

	protected void initDispatchers() {


	}

	protected void override(Invocation invocation) throws MBeanException {
	}

	protected String getSignatureString(String[] signature) {
		if (signature == null) {
			return "()";
		} else if (signature.length == 0) {
			return "()";
		} else {
			StringBuffer sbuf = new StringBuffer(512);
			sbuf.append("(");

			for (int i = 0; i < signature.length - 1; ++i) {
				sbuf.append(signature[i]);
				sbuf.append(",");
			}

			sbuf.append(signature[signature.length - 1]);
			sbuf.append(")");
			return sbuf.toString();
		}
	}

	private void rethrowAsMBeanException(Throwable t) throws MBeanException {
		if (t instanceof RuntimeException) {
			throw new RuntimeMBeanException((RuntimeException) t);
		} else if (t instanceof Error) {
			throw new RuntimeErrorException((Error) t);
		} else {
			throw new MBeanException((Exception) t);
		}
	}

	private void rethrowAsRuntimeMBeanException(Throwable t) {
		if (t instanceof RuntimeException) {
			throw new RuntimeMBeanException((RuntimeException) t);
		} else if (t instanceof Error) {
			throw new RuntimeErrorException((Error) t);
		} else {
			throw new RuntimeMBeanException(new RuntimeException("Unhandled exception", t));
		}
	}
}