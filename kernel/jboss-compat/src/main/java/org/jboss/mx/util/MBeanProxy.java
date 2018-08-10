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
		return null;
	}

	public static Object get(Class intrface, ObjectName name, MBeanServer server) throws MBeanProxyCreationException {
		return null;	}

	public static Object get(ObjectName name, MBeanServer server) throws MBeanProxyCreationException {
		return null;	}

	private static Object get(Class[] interfaces, ObjectName name, MBeanServer server)
			throws MBeanProxyCreationException {
		return null;	}

	public static Object create(Class instance, Class intrface, ObjectName name, String agentID)
			throws MBeanProxyCreationException {
		return null;	}

	public static Object create(Class instance, Class intrface, ObjectName name, MBeanServer server)
			throws MBeanProxyCreationException {
		return null;	}
}