/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.context;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * The Class PortalControllerContext.
 * 
 * describes the calling context to portal services 
 */
public class PortalControllerContext {

	/** The request. */
    private PortletRequest request;
	
	/** The response. */
    private PortletResponse response;
	
	/** The portlet ctx. */
    private PortletContext portletCtx;
    
    /** The http request */
    private HttpServletRequest mainRequest;
	

	/**
	 * Instantiates a new portal controller context.
	 *
	 * @param portletCtx the portlet ctx
	 * @param request the request
	 * @param response the response
	 */
	public PortalControllerContext(PortletContext portletCtx, PortletRequest request, PortletResponse response) {
		super();
		this.request = request;
		this.response = response;
		this.portletCtx = portletCtx;
	}


    /**
     * Instantiates a new portal controller context.
     *
     * @param portletCtx the portlet ctx
     * @param request the request
     * @param response the response
     */
    public PortalControllerContext(HttpServletRequest mainRequest) {
        super();
        this.mainRequest = mainRequest;
    }


    /**
     * Get HTTP servlet request.
     * 
     * @return HTTP servlet request
     */
    public HttpServletRequest getHttpServletRequest() {
        return mainRequest;
    }




	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public PortletRequest getRequest() {
		return request;
	}


	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public PortletResponse getResponse() {
		return response;
	}


	/**
	 * Gets the portlet ctx.
	 *
	 * @return the portlet ctx
	 */
	public PortletContext getPortletCtx() {
		return portletCtx;
	}
	
}
