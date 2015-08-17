package org.osivia.portal.kernel.common.portal;

import java.io.IOException;
import java.util.HashMap;
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
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.web.IllegalRequestException;


public class PagePortletControllerContext extends AbstractPortletControllerContext {

    /** Portlet invoker. */
    private final PortletInvoker invoker;
    /** Portal prepare response. */
    private final PortalPrepareResponse prepareResponse;
    /** State controller context. */
    private final StateControllerContext stateControllerContext;
    /** Serialization. */
    private final Serialization<PortletPageNavigationalState> serialization;
    /** Event controller context. */
    private final EventControllerContext eventControllerContext;
    /** Portlets. */
    private final Map<PortletKey, Portlet> portlets;


    /**
     * Constructor.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param invoker portlet invoker
     * @param prepareResponse portal prepare response
     * @throws IllegalRequestException
     * @throws IOException
     * @throws PortletInvokerException
     */
    public PagePortletControllerContext(HttpServletRequest request, HttpServletResponse response, PortletInvoker invoker, PortalPrepareResponse prepareResponse)
            throws IllegalRequestException, IOException, PortletInvokerException {
        super(request, response);
        this.invoker = invoker;
        this.prepareResponse = prepareResponse;

        this.stateControllerContext = new StateControllerContextImpl(this);
        this.serialization = new PortletPageNavigationalStateSerialization(this.stateControllerContext);
        this.eventControllerContext = new PageEventControllerContext();

        // Portlets
        Map<PortletKey, Portlet> portlets = new HashMap<PortletKey, Portlet>();
        for (Portlet portlet : invoker.getPortlets()) {
            PortletInfo portletInfo = portlet.getInfo();
            String portletName = portletInfo.getName();
            String applicationName = portletInfo.getApplicationName();
            PortletKey key = new PortletKey(applicationName, portletName);
            portlets.put(key, portlet);
        }
        this.portlets = portlets;
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
    protected Portlet getPortlet(String windowId) throws PortletInvokerException {
        WindowDefinition windowDefinition = this.prepareResponse.getWindowDefinition(windowId);
        return this.findPortlet(windowDefinition.getApplicationName(), windowDefinition.getPortletName());
    }


    /**
     * Find portlet from application and portlet names.
     *
     * @param applicationName application name
     * @param portletName portlet name
     * @return portlet
     */
    private Portlet findPortlet(String applicationName, String portletName) {
        PortletKey key = new PortletKey(applicationName, portletName);
        return this.portlets.get(key);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Serialization<PortletPageNavigationalState> getPageNavigationalStateSerialization() {
        return this.serialization;
    }

}
