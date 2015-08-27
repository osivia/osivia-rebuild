package org.osivia.portal.services.common.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osivia.portal.api.common.context.PortalControllerContext;

/**
 * Portal controller context implementation.
 *
 * @author Cédric Krommenhoek
 * @see PortalControllerContext
 */
public class PortalControllerContextImpl implements PortalControllerContext {

    /** HTTP servlet request. */
    private HttpServletRequest httpServletRequest;
    /** HTTP servlet response. */
    private HttpServletResponse httpServletResponse;
    /** Servlet context. */
    private ServletContext servletContext;


    /**
     * Constructor.
     */
    public PortalControllerContextImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public HttpServletRequest getRequest() {
        return this.httpServletRequest;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public HttpServletResponse getResponse() {
        return this.httpServletResponse;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }


    /**
     * Setter for httpServletRequest.
     *
     * @param httpServletRequest the httpServletRequest to set
     */
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Setter for httpServletResponse.
     *
     * @param httpServletResponse the httpServletResponse to set
     */
    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * Setter for servletContext.
     *
     * @param servletContext the servletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
