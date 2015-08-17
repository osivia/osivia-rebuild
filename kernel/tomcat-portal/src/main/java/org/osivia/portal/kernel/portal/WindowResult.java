package org.osivia.portal.kernel.portal;

import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * Window result.
 *
 * @author Cédric Krommenhoek
 */
public class WindowResult {

    /** Window definition. */
    private final WindowDef windowDef;
    /** Portlet invocation response. */
    private final PortletInvocationResponse response;


    /**
     * Constructor.
     *
     * @param windowDef window definition
     * @param response portlet invocation response
     */
    public WindowResult(WindowDef windowDef, PortletInvocationResponse response) {
        super();
        this.windowDef = windowDef;
        this.response = response;
    }


    /**
     * Getter for windowDef.
     * 
     * @return the windowDef
     */
    public WindowDef getWindowDef() {
        return this.windowDef;
    }

    /**
     * Getter for response.
     * 
     * @return the response
     */
    public PortletInvocationResponse getResponse() {
        return this.response;
    }

}
