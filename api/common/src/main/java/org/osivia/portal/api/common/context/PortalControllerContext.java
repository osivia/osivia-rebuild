package org.osivia.portal.api.common.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Portal controller context interface.
 *
 * @author Cédric Krommenhoek
 */
public interface PortalControllerContext {

    HttpServletRequest getRequest();


    HttpServletResponse getResponse();


    ServletContext getServletContext();

}
