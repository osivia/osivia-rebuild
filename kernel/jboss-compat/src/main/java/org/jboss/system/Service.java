package org.jboss.system;

public interface Service {
	void create() throws Exception;

	void start() throws Exception;

	void stop();

	void destroy();
}