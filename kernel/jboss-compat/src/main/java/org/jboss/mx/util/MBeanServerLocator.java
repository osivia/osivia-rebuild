package org.jboss.mx.util;

import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

public class MBeanServerLocator {
	private static MBeanServer instance = null;

	public static void setJBoss(MBeanServer server) {
		Class var1 = MBeanServerLocator.class;
		synchronized (MBeanServerLocator.class) {
			instance = server;
		}
	}

	public static MBeanServer locate(String agentID) {
		MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(agentID).iterator().next();
		return server;
	}

	public static MBeanServer locate() {
		return locate((String) null);
	}

	public static MBeanServer locateJBoss() {
		Class var0 = MBeanServerLocator.class;
		synchronized (MBeanServerLocator.class) {
			if (instance != null) {
				return instance;
			}
		}

		Iterator i = MBeanServerFactory.findMBeanServer((String) null).iterator();

		MBeanServer server;
		do {
			if (!i.hasNext()) {
				throw new IllegalStateException("No 'jboss' MBeanServer found!");
			}

			server = (MBeanServer) i.next();
		} while (!server.getDefaultDomain().equals("jboss"));

		return server;
	}
}