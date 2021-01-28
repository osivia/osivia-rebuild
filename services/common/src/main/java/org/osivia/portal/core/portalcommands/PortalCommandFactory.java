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
package org.osivia.portal.core.portalcommands;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPortalCommand;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.content.ViewContentCommand;
import org.osivia.portal.core.dynamic.RestorablePageUtils;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.tracker.RequestContextUtil;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author jeanseb
 * @see DefaultPortalCommandFactory
 */
public class PortalCommandFactory extends DefaultPortalCommandFactory {

    protected static final Log logger = LogFactory.getLog(PortalCommandFactory.class);



    private CMSService cmsService;

    
    
    /** The locale service. */
    @Autowired
    private ILocaleService localeService;

    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

         
        String viewState = controllerContext.getServerInvocation().getServerContext().getClientRequest().getHeader("view_state");
        
        if (viewState != null) {
            PageMarkerUtils.setViewState(controllerContext, Integer.parseInt(viewState));
            PageMarkerUtils.restorePageState(controllerContext, viewState);
        }

        ControllerCommand cmd = super.doMapping(controllerContext, invocation, host, contextPath, requestPath);
        
        
        
        // Restauration of pages in case of loose of sessions
        
        PortalObjectId targetRestoreIdObject = null;
        PortalObjectPath targetRestorePath = null;
        String newPath = requestPath;
        if ((cmd instanceof ViewPortalCommand) || (cmd instanceof ViewPageCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {
            if (!StringUtils.isEmpty(newPath)) {
                targetRestoreIdObject = ((PortalObjectCommand) cmd).getTargetId();

                int portalPathIndex = newPath.indexOf('/', 1);

                if (portalPathIndex != -1) {
                    targetRestorePath = PortalObjectPath.parse(newPath.substring(portalPathIndex), PortalObjectPath.CANONICAL_FORMAT);
                }
            }
        }


        if (targetRestorePath != null) {
            PortalObjectId realPathId = new PortalObjectId(targetRestoreIdObject.getNamespace(), targetRestorePath);

            PortalObject po = controllerContext.getController().getPortalObjectContainer().getObject(realPathId);

            if (po == null) {
                String pagePath = targetRestorePath.getName(1);

                // Dynamic page creation : session may have been lost
                if (RestorablePageUtils.isRestorable(pagePath)) {
                    PortalObjectId portalId = null;
                    if (cmd instanceof ViewPortalCommand) {
                        portalId = targetRestoreIdObject;
                    } else {
                        PortalObjectPath parentPath = targetRestoreIdObject.getPath().getParent();
                        portalId = new PortalObjectId("", parentPath);
                    }

                    // Restore the page
                    RestorablePageUtils.restore(controllerContext, portalId, pagePath);
                    cmd = super.doMapping(controllerContext, invocation, host, contextPath, newPath);

                    if (cmd instanceof ViewPageCommand) {
                        // Remove parameters
                        cmd = new ViewPageCommand(((ViewPageCommand) cmd).getTargetId());
                    }
                }
            }
        }   
        

        // No more static pages :)
        
        boolean staticPage = false;
        if(cmd instanceof ViewPortalCommand)
            staticPage = true;
        else if ((cmd instanceof ViewPageCommand))  {
             PortalObjectId pageId = ((ViewPageCommand) cmd).getTargetId();
             String pageName = pageId.getPath().getLastComponentName();
             if (!RestorablePageUtils.isRestorable(pageName))   {
                 staticPage = true;
             }
        }
        
        if( staticPage) {
            // only use case : home page
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            UniversalID defaultPortalId;
            try {
                 defaultPortalId = getCMSService().getDefaultPortal(new CMSContext(portalCtx));
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
                cmd = new ViewContentCommand(defaultPortalId.toString(), Locale.FRENCH, false);
        }
        
        
 
        
        
        
        
        
        
        if(cmd instanceof RenderPageCommand)    {
            PortalObjectUtils.setPageId(controllerContext, ((RenderPageCommand) cmd).getTargetId());
        }
        
 

        return cmd;
    }

}
