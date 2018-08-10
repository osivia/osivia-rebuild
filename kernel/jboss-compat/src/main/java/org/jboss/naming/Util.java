package org.jboss.naming;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.jboss.logging.Logger;

public class Util {
	private static final Logger log;

	public static Context createSubcontext(Context ctx, String name) throws NamingException {
		Name n = ctx.getNameParser("").parse(name);
		return createSubcontext(ctx, n);
	}

	public static Context createSubcontext(Context ctx, Name name) throws NamingException {
		Context subctx = ctx;

		for (int pos = 0; pos < name.size(); ++pos) {
			String ctxName = name.get(pos);

			try {
				subctx = (Context) ctx.lookup(ctxName);
			} catch (NameNotFoundException var6) {
				subctx = ctx.createSubcontext(ctxName);
			}

			ctx = subctx;
		}

		return subctx;
	}

	public static void bind(Context ctx, String name, Object value) throws NamingException {
		Name n = ctx.getNameParser("").parse(name);
		bind(ctx, n, value);
	}

	public static void bind(Context ctx, Name name, Object value) throws NamingException {
		int size = name.size();
		String atom = name.get(size - 1);
		Context parentCtx = createSubcontext(ctx, name.getPrefix(size - 1));
		parentCtx.bind(atom, value);
	}

	public static void rebind(Context ctx, String name, Object value) throws NamingException {
		Name n = ctx.getNameParser("").parse(name);
		rebind(ctx, n, value);
	}

	public static void rebind(Context ctx, Name name, Object value) throws NamingException {
		int size = name.size();
		String atom = name.get(size - 1);
		Context parentCtx = createSubcontext(ctx, name.getPrefix(size - 1));
		parentCtx.rebind(atom, value);
	}

	public static void unbind(Context ctx, String name) throws NamingException {
		unbind(ctx, ctx.getNameParser("").parse(name));
	}

	public static void unbind(Context ctx, Name name) throws NamingException {
		ctx.unbind(name);
		int sz = name.size();

		while (true) {
			--sz;
			if (sz <= 0) {
				break;
			}

			Name pname = name.getPrefix(sz);

			try {
				ctx.destroySubcontext(pname);
			} catch (NamingException var5) {
				log.trace("Unable to remove context " + pname, var5);
				break;
			}
		}

	}

	public static Object lookup(String name, Class clazz) throws Exception {
		InitialContext ctx = new InitialContext();

		Object var3;
		try {
			var3 = lookup(ctx, (String) name, clazz);
		} finally {
			ctx.close();
		}

		return var3;
	}

	public static Object lookup(Name name, Class clazz) throws Exception {
		InitialContext ctx = new InitialContext();

		Object var3;
		try {
			var3 = lookup(ctx, (Name) name, clazz);
		} finally {
			ctx.close();
		}

		return var3;
	}

	public static Object lookup(Context context, String name, Class clazz) throws Exception {
		Object result = context.lookup(name);
		checkObject(context, name, result, clazz);
		return result;
	}

	public static Object lookup(Context context, Name name, Class clazz) throws Exception {
		Object result = context.lookup(name);
		checkObject(context, name.toString(), result, clazz);
		return result;
	}

	protected static void checkObject(Context context, String name, Object object, Class clazz) throws Exception {
		Class objectClass = object.getClass();
		if (!clazz.isAssignableFrom(objectClass)) {
			StringBuffer buffer = new StringBuffer(100);
			buffer.append("Object at '").append(name);
			buffer.append("' in context ").append(context.getEnvironment());
			buffer.append(" is not an instance of ");
			appendClassInfo(buffer, clazz);
			buffer.append(" object class is ");
			appendClassInfo(buffer, object.getClass());
			throw new ClassCastException(buffer.toString());
		}
	}

	protected static void appendClassInfo(StringBuffer buffer, Class clazz) {
		buffer.append("[class=").append(clazz.getName());
		buffer.append(" classloader=").append(clazz.getClassLoader());
		buffer.append(" interfaces={");
		Class[] interfaces = clazz.getInterfaces();

		for (int i = 0; i < interfaces.length; ++i) {
			if (i > 0) {
				buffer.append(", ");
			}

			buffer.append("interface=").append(interfaces[i].getName());
			buffer.append(" classloader=").append(interfaces[i].getClassLoader());
		}

		buffer.append("}]");
	}

	static {
		log = Logger.getLogger(Util.class);
	}
}