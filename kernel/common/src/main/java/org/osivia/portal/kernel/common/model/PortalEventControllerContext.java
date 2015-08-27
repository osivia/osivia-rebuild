package org.osivia.portal.kernel.common.model;

import java.util.List;

import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.event.EventControllerContext;
import org.jboss.portal.portlet.controller.event.EventPhaseContext;
import org.jboss.portal.portlet.controller.event.PortletWindowEvent;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.common.model.Window;

/**
 * Portal event controller context.
 *
 * @author Cédric Krommenhoek
 * @see EventControllerContext
 */
public class PortalEventControllerContext implements EventControllerContext {

    /** Portlet controller context. */
    private final PortalPortletControllerContext portletControllerContext;
    /** Windows. */
    private final List<Window> windows;


    /**
     * Constructor.
     *
     * @param portletControllerContext portlet controller context
     * @param windows windows
     */
    public PortalEventControllerContext(PortalPortletControllerContext portletControllerContext, List<Window> windows) {
        super();
        this.portletControllerContext = portletControllerContext;
        this.windows = windows;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent) {
        for (Window window : this.windows) {
            try {
                Portlet portlet = this.portletControllerContext.getPortlet(window.getId());
                if (portlet != null) {
                    PortletInfo portletInfo = portlet.getInfo();
                    if (portletInfo.getEventing().getConsumedEvents().containsKey(producedEvent.getName())) {
                        // TODO
                    }
                }
            } catch (PortletInvokerException e) {
                context.interrupt();
            }
        }
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
