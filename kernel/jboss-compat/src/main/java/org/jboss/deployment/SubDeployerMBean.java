package org.jboss.deployment;

import javax.management.ObjectName;
import org.jboss.system.ServiceMBean;

public interface SubDeployerMBean extends ServiceMBean {
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