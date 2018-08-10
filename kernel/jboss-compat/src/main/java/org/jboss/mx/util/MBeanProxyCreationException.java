package org.jboss.mx.util;

import javax.management.JMException;

public class MBeanProxyCreationException extends JMException {
	private static final long serialVersionUID = 1008637966352433381L;

	public MBeanProxyCreationException() {
	}

	public MBeanProxyCreationException(String msg) {
		super(msg);
	}
}