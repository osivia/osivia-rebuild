package org.osivia.portal.kernel.common.model;

import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.common.model.Window;

/**
 * Window result.
 *
 * @author Cédric Krommenhoek
 */
public class WindowResult implements Comparable<WindowResult> {

    /** Window. */
    private final Window window;
    /** Portlet invocation response. */
    private final PortletInvocationResponse response;


    /**
     * Constructor.
     *
     * @param window window
     * @param response portlet invocation response
     */
    public WindowResult(Window window, PortletInvocationResponse response) {
        super();
        this.window = window;
        this.response = response;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(WindowResult other) {
        if (other == null) {
            return 1;
        } else {
            return this.window.compareTo(other.window);
        }
    }


    /**
     * Getter for window.
     *
     * @return the window
     */
    public Window getWindow() {
        return this.window;
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
