package org.jboss.system;

import javax.management.AttributeChangeNotification;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.jboss.deployment.DeploymentInfo;

import org.jboss.logging.Logger;
import org.jboss.mx.util.JBossNotificationBroadcasterSupport;
import org.jboss.util.Classes;

public class ServiceMBeanSupport extends JBossNotificationBroadcasterSupport
		implements
			ServiceMBean,
			MBeanRegistration {
	public static final String[] SERVICE_CONTROLLER_SIG;
	protected Logger log;
	protected MBeanServer server;
	protected ObjectName serviceName;
	private int state;
	private boolean isJBossInternalLifecycleExposed;

	public ServiceMBeanSupport() {
		this.state = 7;
		this.isJBossInternalLifecycleExposed = false;
		this.log = Logger.getLogger(this.getClass().getName());
		this.log.trace("Constructing");
	}

	public ServiceMBeanSupport(Class type) {
		this(type.getName());
	}

	public ServiceMBeanSupport(String category) {
		this(Logger.getLogger(category));
	}

	public ServiceMBeanSupport(Logger log) {
		this.state = 7;
		this.isJBossInternalLifecycleExposed = false;
		this.log = log;
		log.trace("Constructing");
	}

	public String getName() {
		return Classes.stripPackageName(this.log.getName());
	}

	public ObjectName getServiceName() {
		return this.serviceName;
	}

	public DeploymentInfo getDeploymentInfo() throws JMException {
		return null;
	}

	public MBeanServer getServer() {
		return this.server;
	}

	public int getState() {
		return this.state;
	}

	public String getStateString() {
		return states[this.state];
	}

	public Logger getLog() {
		return this.log;
	}

	public void create() throws Exception {


	}

	public void start() throws Exception {
	}

	public void stop() {
	
	}

	public void destroy() {
	

	}

	protected String jbossInternalDescription() {
		return this.serviceName != null ? this.serviceName.toString() : this.getName();
	}

	public void jbossInternalLifecycle(String method) throws Exception {
		if (method == null) {
			throw new IllegalArgumentException("Null method name");
		} else {
			if (method.equals("create")) {
				this.jbossInternalCreate();
			} else if (method.equals("start")) {
				this.jbossInternalStart();
			} else if (method.equals("stop")) {
				this.jbossInternalStop();
			} else {
				if (!method.equals("destroy")) {
					throw new IllegalArgumentException("Unknown lifecyle method " + method);
				}

				this.jbossInternalDestroy();
			}

		}
	}

	protected void jbossInternalCreate() throws Exception {
		this.log.debug("Creating " + this.jbossInternalDescription());

		try {
			this.createService();
			this.state = 6;
		} catch (Exception var2) {
			this.log.debug("Initialization failed " + this.jbossInternalDescription(), var2);
			throw var2;
		}

		this.log.debug("Created " + this.jbossInternalDescription());
	}

	protected void jbossInternalStart() throws Exception {
		if (this.state != 2 && this.state != 3 && this.state != 1) {
			if (this.state != 6 && this.state != 0 && this.state != 4) {
				this.log.debug("Start requested before create, calling create now");
				this.create();
			}

			this.state = 2;
			this.sendStateChangeNotification(0, 2, this.getName() + " starting", (Throwable) null);
			this.log.debug("Starting " + this.jbossInternalDescription());

			try {
				this.startService();
			} catch (Exception var2) {
				this.state = 4;
				this.sendStateChangeNotification(2, 4, this.getName() + " failed", var2);
				this.log.debug("Starting failed " + this.jbossInternalDescription(), var2);
				throw var2;
			}

			this.state = 3;
			this.sendStateChangeNotification(2, 3, this.getName() + " started", (Throwable) null);
			this.log.debug("Started " + this.jbossInternalDescription());
		}
	}

	protected void jbossInternalStop() {
		if (this.state == 3) {
			this.state = 1;
			this.sendStateChangeNotification(3, 1, this.getName() + " stopping", (Throwable) null);
			this.log.debug("Stopping " + this.jbossInternalDescription());

			try {
				this.stopService();
			} catch (Throwable var2) {
				this.state = 4;
				this.sendStateChangeNotification(1, 4, this.getName() + " failed", var2);
				this.log.warn("Stopping failed " + this.jbossInternalDescription(), var2);
				return;
			}

			this.state = 0;
			this.sendStateChangeNotification(1, 0, this.getName() + " stopped", (Throwable) null);
			this.log.debug("Stopped " + this.jbossInternalDescription());
		}
	}

	protected void jbossInternalDestroy() {
		if (this.state != 5) {
			if (this.state == 3) {
				this.log.debug("Destroy requested before stop, calling stop now");
				this.stop();
			}

			this.log.debug("Destroying " + this.jbossInternalDescription());

			try {
				this.destroyService();
			} catch (Throwable var2) {
				this.log.warn("Destroying failed " + this.jbossInternalDescription(), var2);
			}

			this.state = 5;
			this.log.debug("Destroyed " + this.jbossInternalDescription());
		}
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
		this.server = server;
		this.serviceName = this.getObjectName(server, name);
		return this.serviceName;
	}

	public void postRegister(Boolean registrationDone) {
	}
	
		

	public void preDeregister() throws Exception {
	}

	public void postDeregister() {
		
	}

	protected long getNextNotificationSequenceNumber() {
		return this.nextNotificationSequenceNumber();
	}

	protected ObjectName getObjectName(MBeanServer server, ObjectName name) throws MalformedObjectNameException {
		return name;
	}

	protected void createService() throws Exception {
	}

	protected void startService() throws Exception {
	}

	protected void stopService() throws Exception {
	}

	protected void destroyService() throws Exception {
	}

	private void sendStateChangeNotification(int oldState, int newState, String msg, Throwable t) {
		long now = System.currentTimeMillis();
		AttributeChangeNotification stateChangeNotification = new AttributeChangeNotification(this,
				this.getNextNotificationSequenceNumber(), now, msg, "State", "java.lang.Integer", new Integer(oldState),
				new Integer(newState));
		stateChangeNotification.setUserData(t);
		this.sendNotification(stateChangeNotification);
	}

	static {
		SERVICE_CONTROLLER_SIG = new String[]{ObjectName.class.getName()};
	}
}