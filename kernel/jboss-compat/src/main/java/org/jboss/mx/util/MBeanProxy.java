package org.jboss.mx.util;

import java.lang.reflect.Proxy;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class MBeanProxy {
	public static Object get(Class intrface, ObjectName name, String agentID) throws MBeanProxyCreationException {
		return get(intrface, name, (MBeanServer) MBeanServerFactory.findMBeanServer(agentID).get(0));
	}

	public static Object get(Class intrface, ObjectName name, MBeanServer server) throws MBeanProxyCreationException {
		return get(new Class[]{intrface, ProxyContext.class, DynamicMBean.class}, name, server);
	}

	public static Object get(ObjectName name, MBeanServer server) throws MBeanProxyCreationException {
		return get(new Class[]{ProxyContext.class, DynamicMBean.class}, name, server);
	}

	private static Object get(Class[] interfaces, ObjectName name, MBeanServer server)
			throws MBeanProxyCreationException {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces,
				new JMXInvocationHandler(server, name));
	}

	public static Object create(Class instance, Class intrface, ObjectName name, String agentID)
			throws MBeanProxyCreationException {
		return create(instance, intrface, name, (MBeanServer) MBeanServerFactory.findMBeanServer(agentID).get(0));
	}

	public static Object create(Class instance, Class intrface, ObjectName name, MBeanServer server)
			throws MBeanProxyCreationException {
		try {
			server.createMBean(instance.getName(), name);
			return get(intrface, name, server);
		} catch (ReflectionException var5) {
			throw new MBeanProxyCreationException("Creating the MBean failed: " + var5.toString());
		} catch (InstanceAlreadyExistsException var6) {
			throw new MBeanProxyCreationException("Instance already exists: " + name);
		} catch (MBeanRegistrationException var7) {
			throw new MBeanProxyCreationException("Error registering the MBean to the server: " + var7.toString());
		} catch (MBeanException var8) {
			throw new MBeanProxyCreationException(var8.toString());
		} catch (NotCompliantMBeanException var9) {
			throw new MBeanProxyCreationException(
					"Not a compliant MBean " + instance.getClass().getName() + ": " + var9.toString());
		}
	}
}