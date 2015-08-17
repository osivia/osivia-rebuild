package org.osivia.portal.kernel.portal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.jboss.portal.WindowState;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.PortletController;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalState;
import org.jboss.portal.portlet.controller.state.PortletWindowNavigationalState;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * Portal render response.
 *
 * @author Cédric Krommenhoek
 * @see PortalResponse
 */
public class PortalRenderResponse extends PortalResponse {

    /** Portlet page navigational state. */
    private PortletPageNavigationalState pageNavigationalState;
    /** Page portlet controller context. */
    private PagePortletControllerContext portletControllerContext;
    /** Window results. */
    private Map<String, WindowResult> windowResults;


    public PortalRenderResponse(HttpServletRequest request, HttpServletResponse response, PagePortletControllerContext portletControllerContext, PortletPageNavigationalState pageNavigationalState, PortalPrepareResponse prepareResponse) {
        super(request, response);

        Set<QName> pageParameterNames = prepareResponse.getPageParameterNames();
        if (pageParameterNames.size() > 0) {
            if (pageNavigationalState == null) {
                pageNavigationalState = portletControllerContext.getStateControllerContext().createPortletPageNavigationalState(true);
            }
        }
        for (QName parameterName : pageParameterNames) {
            PageParameterDef parameterDef = prepareResponse.getPageParameterDef(parameterName);
            boolean update = parameterDef.isFrozen() || (pageNavigationalState.getPublicNavigationalState(parameterName) == null);
            if (update) {
                String[] value = new String[]{parameterDef.getValue()};
                pageNavigationalState.setPublicNavigationalState(parameterName, value);
            }
        }

        this.portletControllerContext = portletControllerContext;
        this.windowResults = new HashMap<String, WindowResult>();
        this.pageNavigationalState = pageNavigationalState;

        this.render(prepareResponse);
    }


    /**
     * Get page navigational state.
     *
     * @return page navigation state
     */
    public PortletPageNavigationalState getPageNavigationalState() {
        return this.pageNavigationalState;
    }


    /**
     * Get window identifiers.
     *
     * @return window identifiers
     */
    public Set<String> getWindowIds() {
        return this.windowResults.keySet();
    }


    /**
     * Get window result.
     *
     * @param windowId window identifier
     * @return window result
     */
    public WindowResult getWindowResult(String windowId) {
        return this.windowResults.get(windowId);
    }


    /**
     * Get portlet controller context.
     *
     * @return portlet controller context
     */
    public PagePortletControllerContext getPortletControllerContext() {
        return this.portletControllerContext;
    }


    /**
     * Check if window is maximized.
     * 
     * @param windowId window identifier
     * @return true if window is maximized
     */
    public boolean isMaximizedWindow(String windowId) {
        if (windowId == null) {
            throw new IllegalArgumentException();
        }
        return windowId.equals(this.getMaximizedWindowId());
    }


    /**
     * Get maximized window identifier.
     * 
     * @return maximized window identifier
     */
    public String getMaximizedWindowId() {
        if (this.pageNavigationalState != null) {
            for (String windowId : this.pageNavigationalState.getPortletWindowIds()) {
                PortletWindowNavigationalState windowNS = this.pageNavigationalState.getPortletWindowNavigationalState(windowId);
                if (WindowState.MAXIMIZED.equals(windowNS.getWindowState())) {
                    return windowId;
                }
            }
        }

        return null;
    }


    /**
     * Render.
     *
     * @param cookies cookies
     * @param windowId window identifier
     * @return portlet invocation response
     * @throws PortletInvokerException
     */
    private PortletInvocationResponse render(List<Cookie> cookies, String windowId) throws PortletInvokerException {
        PortletController portletController = new PortletController();
        return portletController.render(this.portletControllerContext, cookies, this.pageNavigationalState, windowId);
    }


    /**
     * Render.
     *
     * @param prepareResponse prepare response
     */
    private void render(PortalPrepareResponse prepareResponse) {
        for (String windowId : prepareResponse.getWindowIds()) {
            WindowDef windowDef = prepareResponse.getWindowDef(windowId);

            try {
                Portlet portlet = this.portletControllerContext.getPortlet(windowId);
                if (portlet != null) {
                    PortletInvocationResponse portletResponse = this.render(null, windowId);

                    WindowResult result = new WindowResult(windowDef, portletResponse);

                    this.windowResults.put(windowId, result);
                }
            } catch (PortletInvokerException e) {
                e.printStackTrace();
            }
        }
    }


}
