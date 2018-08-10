package org.jboss.naming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

public class NonSerializableFactory implements ObjectFactory {
	private static Map wrapperMap = Collections.synchronizedMap(new HashMap());

	public static synchronized void bind(String key, Object target) throws NameAlreadyBoundException {
		if (wrapperMap.containsKey(key)) {
			throw new NameAlreadyBoundException(key + " already exists in the NonSerializableFactory map");
		} else {
			wrapperMap.put(key, target);
		}
	}

	public static void rebind(String key, Object target) {
		wrapperMap.put(key, target);
	}

	public static void unbind(String key) throws NameNotFoundException {
		if (wrapperMap.remove(key) == null) {
			throw new NameNotFoundException(key + " was not found in the NonSerializableFactory map");
		}
	}

	public static void unbind(Name name) throws NameNotFoundException {
		String key = name.toString();
		if (wrapperMap.remove(key) == null) {
			throw new NameNotFoundException(key + " was not found in the NonSerializableFactory map");
		}
	}

	public static Object lookup(String key) {
		Object value = wrapperMap.get(key);
		return value;
	}

	public static Object lookup(Name name) {
		String key = name.toString();
		Object value = wrapperMap.get(key);
		return value;
	}

	public static synchronized void rebind(Context ctx, String key, Object target) throws NamingException {
		rebind(key, target);
		String className = target.getClass().getName();
		String factory = NonSerializableFactory.class.getName();
		StringRefAddr addr = new StringRefAddr("nns", key);
		Reference memoryRef = new Reference(className, addr, factory, (String) null);
		ctx.rebind(key, memoryRef);
	}

	public static synchronized void rebind(Name name, Object target) throws NamingException {
		rebind(name, target, false);
	}

	public static synchronized void rebind(Name name, Object target, boolean createSubcontexts) throws NamingException {
		String key = name.toString();
		InitialContext ctx = new InitialContext();
		if (createSubcontexts && name.size() > 1) {
			int size = name.size() - 1;
			Util.createSubcontext(ctx, name.getPrefix(size));
		}

		rebind(ctx, key, target);
	}

	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable env) throws Exception {
		Reference ref = (Reference) obj;
		RefAddr addr = ref.get("nns");
		String key = (String) addr.getContent();
		Object target = wrapperMap.get(key);
		return target;
	}
}