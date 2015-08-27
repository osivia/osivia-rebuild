package org.osivia.portal.api.common.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Portal controller context factory interface.
 *
 * @author Cédric Krommenhoek
 */
public interface PortalControllerContextFactory {

    /**
     * Get portal controller context.
     *
     * @param httpServletRequest HTTP servlet request
     * @param httpServletResponse HTTP servlet response
     * @param servletContext servlet context
     * @return portal controller context
     */
    PortalControllerContext getContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ServletContext servletContext);

}
