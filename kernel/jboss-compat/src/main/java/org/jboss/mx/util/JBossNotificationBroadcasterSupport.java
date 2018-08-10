package org.jboss.mx.util;

import EDU.oswego.cs.dl.util.concurrent.SynchronizedLong;
import javax.management.JMException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import org.jboss.logging.Logger;

public class JBossNotificationBroadcasterSupport implements NotificationEmitter {
	private static final Logger log = Logger.getLogger(JBossNotificationBroadcasterSupport.class);
	private static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];

	private SynchronizedLong sequenceNumber = new SynchronizedLong(0L);

	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {

	}

	public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {

	}

	public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {

	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return NO_NOTIFICATIONS;
	}

	public void sendNotification(Notification notification) {
	

	}

	public void handleNotification(NotificationListener listener, Notification notification, Object handback) {
		try {
			listener.handleNotification(notification, handback);
		} catch (Throwable var5) {
			log.debug("Ignored unhandled throwable from listener", var5);
		}

	}

	public long nextNotificationSequenceNumber() {
		return this.sequenceNumber.increment();
	}
}