package org.osivia.portal.kernel.common.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.common.io.Serialization;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.event.EventControllerContext;
import org.jboss.portal.portlet.controller.impl.AbstractPortletControllerContext;
import org.jboss.portal.portlet.controller.impl.state.StateControllerContextImpl;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalState;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalStateSerialization;
import org.jboss.portal.portlet.controller.state.StateControllerContext;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.web.IllegalRequestException;
import org.osivia.portal.api.common.model.Window;

/**
 * Portal portlet controller context.
 *
 * @author Cédric Krommenhoek
 * @see AbstractPortletControllerContext
 */
public class PortalPortletControllerContext extends AbstractPortletControllerContext {

    /** Portlet invoker. */
    private final PortletInvoker invoker;
    /** Windows. */
    private final Map<String, Window> windows;
    /** Portlets. */
    private final Map<PortletKey, Portlet> portlets;
    /** Event controller context. */
    private final EventControllerContext eventControllerContext;
    /** State controller context. */
    private final StateControllerContext stateControllerContext;
    /** Serialization. */
    private final Serialization<PortletPageNavigationalState> serialization;


    /**
     * Constructor.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param invoker portlet invoker
     * @param windows windows
     * @throws IllegalRequestException
     * @throws IOException
     * @throws PortletInvokerException
     */
    public PortalPortletControllerContext(HttpServletRequest request, HttpServletResponse response, PortletInvoker invoker, List<Window> windows)
            throws IllegalRequestException, IOException, PortletInvokerException {
        super(request, response);
        this.invoker = invoker;

        // Windows
        this.windows = new HashMap<String, Window>(windows.size());
        for (Window window : windows) {
            this.windows.put(window.getId(), window);
        }

        // Portlets
        this.portlets = new HashMap<PortletKey, Portlet>(invoker.getPortlets().size());
        for (Portlet portlet : invoker.getPortlets()) {
            PortletKey key = new PortletKey(portlet);
            this.portlets.put(key, portlet);
        }

        // Event controller context
        this.eventControllerContext = new PortalEventControllerContext(this, windows);
        // State controller context
        this.stateControllerContext = new StateControllerContextImpl(this);
        // Serialization
        this.serialization = new PortletPageNavigationalStateSerialization(this.stateControllerContext);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Portlet getPortlet(String windowId) throws PortletInvokerException {
        Portlet portlet = null;
        Window window = this.windows.get(windowId);
        if (window != null) {
            PortletKey key = new PortletKey(window);
            portlet = this.portlets.get(key);
        }
        return portlet;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException {
        return this.invoker.invoke(invocation);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public EventControllerContext getEventControllerContext() {
        return this.eventControllerContext;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public StateControllerContext getStateControllerContext() {
        return this.stateControllerContext;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Serialization<PortletPageNavigationalState> getPageNavigationalStateSerialization() {
        return this.serialization;
    }

}
