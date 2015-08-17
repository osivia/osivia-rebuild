package org.osivia.portal.kernel.common.portal;

import org.jboss.portal.portlet.controller.event.EventControllerContext;
import org.jboss.portal.portlet.controller.event.EventPhaseContext;
import org.jboss.portal.portlet.controller.event.PortletWindowEvent;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * Page event controller context.
 *
 * @author Cédric Krommenhoek
 * @see EventControllerContext
 */
public class PageEventControllerContext implements EventControllerContext {

    /**
     * Constructor.
     */
    public PageEventControllerContext() {
        super();
    }


    @Override
    public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse) {
        // TODO Auto-generated method stub

    }

    @Override
    public void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause) {
        // TODO Auto-generated method stub

    }

}
