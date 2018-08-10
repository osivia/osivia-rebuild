package org.jboss.mx.interceptor;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.jboss.logging.Logger;
import org.jboss.mx.server.Invocation;

public abstract class AbstractInterceptor implements Interceptor {
	protected String name = "<no name>";
	protected boolean isShared = false;
	protected Logger log;

	public AbstractInterceptor() {
		this.log = Logger.getLogger(this.getClass());
	}

	public AbstractInterceptor(String name) {
		if (name != null && !name.equals("")) {
			this.name = name;
		} else {
			throw new IllegalArgumentException("null name");
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object invoke(Invocation invocation) throws Throwable {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public boolean isShared() {
		return this.isShared;
	}

	public void setLogger(Logger log) {
		this.log = log;
	}

	public void init() throws Exception {
	}

	public void start() {
	}

	public void stop() throws Exception {
	}

	public void destroy() {
	}

	public String toString() {
		String className = this.getClass().getName();
		int index = className.lastIndexOf(46);
		return className.substring(index < 0 ? 0 : index) + "[name=" + this.name + "]";
	}
}