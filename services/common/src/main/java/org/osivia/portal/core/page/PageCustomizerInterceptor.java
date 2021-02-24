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
package org.osivia.portal.core.page;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;


/**
 * Page customizer interceptor.
 *
 * @see ControllerInterceptor
 */
public class PageCustomizerInterceptor extends ControllerInterceptor {






    /**
     * Constructor.
     */
    public PageCustomizerInterceptor() {
        super();
    }





    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        // Controller context
        ControllerContext controllerContext = cmd.getControllerContext();

        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        
        String url = (String) request.getAttribute("osivia.full_refresh_url");
        if( url != null)
            return new RedirectionResponse(url);
        
        
        if (cmd instanceof RenderPageCommand) {
            RenderPageCommand rpc = (RenderPageCommand) cmd;

            //PortalObjectUtils.setPageId(controllerContext, rpc.getPage().getId());

        }

   


        ControllerResponse resp;

        try {

            resp = (ControllerResponse) cmd.invokeNext();

        } catch (Exception e) {
            
            throw e;
        } finally {
          
        }


        //
        return resp;
    }


    

}
