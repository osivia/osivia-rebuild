package org.osivia.portal.kernel.common.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Portal response.
 *
 * @author Cédric Krommenhoek
 * @see HttpServletResponseWrapper
 */
public abstract class PortalResponse extends HttpServletResponseWrapper {

    /** Request URI. */
    private final String requestURI;
    /** Count. */
    private int count;


    /**
     * Constructor.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     */
    public PortalResponse(HttpServletRequest request, HttpServletResponse response) {
        super(response);
        this.requestURI = request.getRequestURI();
        this.count = 0;
    }


    /**
     * Get next identifier.
     *
     * @return next identifier
     */
    public String getNextId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.requestURI);
        builder.append("/");
        builder.append(this.count++);
        return builder.toString();
    }

}
