package org.jboss.mx.interceptor;

import org.jboss.logging.Logger;
import org.jboss.mx.server.Invocation;

public interface Interceptor {
	String getName();

	boolean isShared();

	Object invoke(Invocation var1) throws Throwable;

	void setLogger(Logger var1);

	void init() throws Exception;

	void start();

	void stop() throws Exception;

	void destroy();
}