package org.jboss.mx.server;

import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;

public interface MBeanInvoker extends DynamicMBean, MBeanRegistration, NotificationEmitter {
	MBeanInfo getMetaData();

	Object getResource();

	void setResource(Object var1);

	ObjectName getObjectName();

	void updateAttributeInfo(Descriptor var1) throws MBeanException;
}