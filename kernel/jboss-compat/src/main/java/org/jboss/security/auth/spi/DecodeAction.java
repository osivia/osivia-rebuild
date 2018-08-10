package org.jboss.security.auth.spi;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import org.jboss.mx.util.MBeanServerLocator;


class DecodeAction implements PrivilegedExceptionAction {
	String password;
	ObjectName serviceName;

	DecodeAction(String password, ObjectName serviceName) {
		this.password = password;
		this.serviceName = serviceName;
	}

	public Object run() throws Exception {
		return null;
		//FIXME OSIVIA/MIG interaction JAAS
		
//		MBeanServer server = MBeanServerLocator.locateJBoss();
//		JaasSecurityDomainMBean securityDomain = (JaasSecurityDomainMBean) MBeanServerInvocationHandler
//				.newProxyInstance(server, this.serviceName, JaasSecurityDomainMBean.class, false);
//		byte[] secret = securityDomain.decode64(this.password);
//		String secretPassword = new String(secret, "UTF-8");
//		return secretPassword.toCharArray();
	}

	static char[] decode(String password, ObjectName serviceName) throws Exception {
		DecodeAction action = new DecodeAction(password, serviceName);

		try {
			char[] decode = (char[]) ((char[]) AccessController.doPrivileged(action));
			return decode;
		} catch (PrivilegedActionException var4) {
			throw var4.getException();
		}
	}
}