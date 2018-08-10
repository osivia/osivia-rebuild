package org.jboss.deployment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import javax.management.ObjectName;
import org.jboss.mx.util.ObjectNameFactory;
import org.jboss.system.ServiceMBean;

public interface MainDeployerMBean extends ServiceMBean, DeployerMBean, MainDeployerConstants {
	ObjectName OBJECT_NAME = ObjectNameFactory.create("jboss.system:service=MainDeployer");

	boolean getCopyFiles();

	void setCopyFiles(boolean var1);

	File getTempDir();

	void setTempDir(File var1);

	String[] getEnhancedSuffixOrder();

	void setEnhancedSuffixOrder(String[] var1);

	void setServiceController(ObjectName var1);

	String getTempDirString();

	String[] getSuffixOrder();

	Collection listDeployed();

	Collection listDeployedModules();

	String listDeployedAsString();

	Collection listIncompletelyDeployed();

	Collection listWaitingForDeployer();

	void addDeployer(SubDeployer var1);

	void removeDeployer(SubDeployer var1);

	Collection listDeployers();

	void shutdown();

	void redeploy(String var1) throws DeploymentException, MalformedURLException;

	void redeploy(URL var1) throws DeploymentException;

	void redeploy(DeploymentInfo var1) throws DeploymentException;

	void undeploy(URL var1) throws DeploymentException;

	void undeploy(String var1) throws DeploymentException, MalformedURLException;

	void undeploy(DeploymentInfo var1);

	void deploy(String var1) throws DeploymentException, MalformedURLException;

	void deploy(URL var1) throws DeploymentException;

	void deploy(DeploymentInfo var1) throws DeploymentException;

	void start(String var1) throws DeploymentException, MalformedURLException;

	void stop(String var1) throws DeploymentException, MalformedURLException;

	boolean isDeployed(String var1) throws MalformedURLException;

	boolean isDeployed(URL var1);

	DeploymentInfo getDeployment(URL var1);

	URL getWatchUrl(URL var1);

	void checkIncompleteDeployments() throws DeploymentException;
}