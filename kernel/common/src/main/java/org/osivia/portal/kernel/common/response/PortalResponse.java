package org.osivia.portal.kernel.common.response;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Portal response abstract super-class.
 * 
 * @author Cédric Krommenhoek
 * @see HttpServletResponseWrapper
 */
public abstract class PortalResponse extends HttpServletResponseWrapper {

    /**
     * Portal response.
     *
     * @param response HTTP servlet response
     */
    public PortalResponse(HttpServletResponse response) {
        super(response);
    }

}
