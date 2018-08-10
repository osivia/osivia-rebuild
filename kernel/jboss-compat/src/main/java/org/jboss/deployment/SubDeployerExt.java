package org.jboss.deployment;

public interface SubDeployerExt extends SubDeployer {
	void setEnhancedSuffixes(String[] var1);

	String[] getEnhancedSuffixes();
}