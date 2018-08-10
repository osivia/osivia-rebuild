package org.jboss.mx.util;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ObjectNameFactory {
	public static ObjectName create(String name) {
		try {
			return new ObjectName(name);
		} catch (MalformedObjectNameException var2) {
			throw new Error("Invalid ObjectName: " + name + "; " + var2);
		}
	}

	public static ObjectName create(String domain, String key, String value) {
		try {
			return new ObjectName(domain, key, value);
		} catch (MalformedObjectNameException var4) {
			throw new Error("Invalid ObjectName: " + domain + "," + key + "," + value + "; " + var4);
		}
	}

	public static ObjectName create(String domain, Hashtable table) {
		try {
			return new ObjectName(domain, table);
		} catch (MalformedObjectNameException var3) {
			throw new Error("Invalid ObjectName: " + domain + "," + table + "; " + var3);
		}
	}
}