package org.osivia.portal.kernel.portal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.event.EventControllerContext;
import org.jboss.portal.portlet.controller.event.EventPhaseContext;
import org.jboss.portal.portlet.controller.event.PortletWindowEvent;
import org.jboss.portal.portlet.info.EventInfo;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * Page event controller context.
 *
 * @author Cédric Krommenhoek
 * @see EventControllerContext
 */
public class PageEventControllerContext implements EventControllerContext {

    /** Page portlet controller context. */
    private final PagePortletControllerContext context;
    /** Prepare response. */
    private final PortalPrepareResponse prepareResponse;
    /** Routings. */
    private final Map<PortletWindowEvent, EventRoute> routings;
    /** Roots. */
    private final List<EventRoute> roots;


    /**
     * Constructor.
     *
     * @param context page portlet controller context
     * @param prepareResponse prepare response
     */
    public PageEventControllerContext(PagePortletControllerContext context, PortalPrepareResponse prepareResponse) {
        this.context = context;
        this.prepareResponse = prepareResponse;
        this.routings = new LinkedHashMap<PortletWindowEvent, EventRoute>();
        this.roots = new ArrayList<EventRoute>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent) {
        EventRoute relatedRoute = this.routings.get(sourceEvent);

        for (String windowId : this.prepareResponse.getWindowIds()) {
            try {
                Portlet portlet = this.context.getPortlet(windowId);
                if (portlet != null) {
                    PortletInfo portletInfo = portlet.getInfo();
                    Map<QName, ? extends EventInfo> consumedEvents = portletInfo.getEventing().getConsumedEvents();
                    if (consumedEvents.containsKey(producedEvent.getName())) {
                        PortletWindowEvent destinationEvent = new PortletWindowEvent(producedEvent.getName(), producedEvent.getPayload(), windowId);

                        EventRoute eventRoute = new EventRoute(relatedRoute, producedEvent.getName(), producedEvent.getPayload(), producedEvent.getWindowId(), destinationEvent.getWindowId());

                        if (relatedRoute != null) {
                            relatedRoute.getChildren().add(eventRoute);
                        } else {
                            this.roots.add(eventRoute);
                        }

                        this.routings.put(destinationEvent, eventRoute);

                        context.queueEvent(destinationEvent);
                    }
                }
            } catch (PortletInvokerException e) {
                e.printStackTrace();
                context.interrupt();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse) {
        EventRoute route = this.routings.get(consumedEvent);
        route.setAcknowledgement(new EventAcknowledgement.Consumed(consumerResponse));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable) {
        EventRoute route = this.routings.get(failedEvent);
        route.setAcknowledgement(new EventAcknowledgement.Failed(throwable));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause) {
        EventRoute route = this.routings.get(discardedEvent);
        route.setAcknowledgement(new EventAcknowledgement.Discarded(cause));
    }


    /**
     * Getter for roots.
     * 
     * @return the roots
     */
    public List<EventRoute> getRoots() {
        return this.roots;
    }

}
