package org.jboss.deployment;

public interface SubDeployerExtMBean extends SubDeployerMBean {
	void setEnhancedSuffixes(String[] var1);

	String[] getEnhancedSuffixes();
}