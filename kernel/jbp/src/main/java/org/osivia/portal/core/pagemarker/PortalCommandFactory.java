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
 */
package org.osivia.portal.core.pagemarker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;

import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.tracker.RequestContextUtil;


/**
 *
 * @author jeanseb
 * @see DefaultPortalCommandFactory
 */
public class PortalCommandFactory extends DefaultPortalCommandFactory {

    protected static final Log logger = LogFactory.getLog(PortalCommandFactory.class);


   
    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

 
        RequestContextUtil.setControllerContext(controllerContext);
        
        String viewState = controllerContext.getServerInvocation().getServerContext().getClientRequest().getHeader("view_state");
        
        if (viewState != null) {
            PageMarkerUtils.setViewState(controllerContext, Integer.parseInt(viewState));
            PageMarkerUtils.restorePageState(controllerContext, viewState);
        }

        ControllerCommand cmd = super.doMapping(controllerContext, invocation, host, contextPath, requestPath);
        
        if(cmd instanceof RenderPageCommand)    {
            PortalObjectUtils.setPageId(controllerContext, ((RenderPageCommand) cmd).getTargetId());
        }
        


  

        return cmd;
    }

}
