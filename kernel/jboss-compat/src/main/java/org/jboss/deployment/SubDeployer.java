package org.jboss.deployment;

import javax.management.ObjectName;

public interface SubDeployer {
	String INIT_NOTIFICATION = "org.jboss.deployment.SubDeployer.init";
	String CREATE_NOTIFICATION = "org.jboss.deployment.SubDeployer.create";
	String START_NOTIFICATION = "org.jboss.deployment.SubDeployer.start";
	String STOP_NOTIFICATION = "org.jboss.deployment.SubDeployer.stop";
	String DESTROY_NOTIFICATION = "org.jboss.deployment.SubDeployer.destroy";

	int RELATIVE_ORDER_100 = 100;

	int RELATIVE_ORDER_200 = 200;

	int RELATIVE_ORDER_300 = 300;

	int RELATIVE_ORDER_400 = 400;

	int RELATIVE_ORDER_500 = 500;

	int RELATIVE_ORDER_600 = 600;

	int RELATIVE_ORDER_700 = 700;

	int RELATIVE_ORDER_800 = 800;

	int RELATIVE_ORDER_900 = 900;

	ObjectName getServiceName();

	String[] getSuffixes();

	int getRelativeOrder();

	boolean accepts(DeploymentInfo var1);

	void init(DeploymentInfo var1) throws DeploymentException;

	void create(DeploymentInfo var1) throws DeploymentException;

	void start(DeploymentInfo var1) throws DeploymentException;

	void stop(DeploymentInfo var1) throws DeploymentException;

	void destroy(DeploymentInfo var1) throws DeploymentException;
}