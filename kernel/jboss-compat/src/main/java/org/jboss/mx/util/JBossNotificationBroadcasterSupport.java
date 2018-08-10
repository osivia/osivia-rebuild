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
import org.jboss.mx.notification.ListenerRegistration;
import org.jboss.mx.notification.ListenerRegistry;
import org.jboss.mx.notification.ListenerRegistry.ListenerRegistrationIterator;

public class JBossNotificationBroadcasterSupport implements NotificationEmitter {
	private static final Logger log = Logger.getLogger(JBossNotificationBroadcasterSupport.class);
	private static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
	private ListenerRegistry registry = new ListenerRegistry();
	private SynchronizedLong sequenceNumber = new SynchronizedLong(0L);

	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
		try {
			this.registry.add(listener, filter, handback);
		} catch (JMException var5) {
			throw new RuntimeException(var5.toString());
		}
	}

	public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
		this.registry.remove(listener);
	}

	public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		this.registry.remove(listener, filter, handback);
	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return NO_NOTIFICATIONS;
	}

	public void sendNotification(Notification notification) {
		ListenerRegistrationIterator iterator = this.registry.iterator();

		while (iterator.hasNext()) {
			ListenerRegistration registration = iterator.nextRegistration();
			NotificationFilter filter = registration.getFilter();
			if (filter == null) {
				this.handleNotification(registration.getListener(), notification, registration.getHandback());
			} else if (filter.isNotificationEnabled(notification)) {
				this.handleNotification(registration.getListener(), notification, registration.getHandback());
			}
		}

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