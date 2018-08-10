package org.jboss.deployment;

//FIXME make compatible

public class DeploymentException extends Exception {
	private static final long serialVersionUID = 1416258464473965574L;

	public static void rethrowAsDeploymentException(String message, Throwable t) throws DeploymentException {
		if (t instanceof DeploymentException) {
			throw (DeploymentException) t;
		} else {
			throw new DeploymentException(message, t);
		}
	}

	public DeploymentException(String msg) {
		super(msg);
	}

	public DeploymentException(String msg, Throwable nested) {
		super(msg, nested);
	}

	public DeploymentException(Throwable nested) {
		super(nested);
	}

	public DeploymentException() {
	}
}