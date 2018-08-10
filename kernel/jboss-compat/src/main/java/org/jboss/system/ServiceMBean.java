package org.jboss.system;

public interface ServiceMBean extends Service {
	String CREATE_EVENT = "org.jboss.system.ServiceMBean.create";
	String START_EVENT = "org.jboss.system.ServiceMBean.start";
	String STOP_EVENT = "org.jboss.system.ServiceMBean.stop";
	String DESTROY_EVENT = "org.jboss.system.ServiceMBean.destroy";
	String[] states = new String[]{"Stopped", "Stopping", "Starting", "Started", "Failed", "Destroyed", "Created",
			"Unregistered", "Registered"};
	int STOPPED = 0;
	int STOPPING = 1;
	int STARTING = 2;
	int STARTED = 3;
	int FAILED = 4;
	int DESTROYED = 5;
	int CREATED = 6;
	int UNREGISTERED = 7;
	int REGISTERED = 8;

	String getName();

	int getState();

	String getStateString();

	void jbossInternalLifecycle(String var1) throws Exception;
}