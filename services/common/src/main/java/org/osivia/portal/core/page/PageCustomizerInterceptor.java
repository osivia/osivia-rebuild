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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.cms.edition.CMSEditionService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Page customizer interceptor.
 *
 * @see ControllerInterceptor
 */
@Service(  "osivia:service=Interceptor,type=Command,name=PageCustomizer" )

public class PageCustomizerInterceptor extends ControllerInterceptor {



    @Autowired
    private CMSEditionService cmsEditionService;


    /**
     * Constructor.
     */
    public PageCustomizerInterceptor() {
        super();
    }


    /**
     * Unset max mode.
     *
     * @param windows the windows
     * @param controllerCtx the controller ctx
     */
    public static void unsetMaxMode(Collection<PortalObject> windows, ControllerContext controllerCtx) {


       Iterator<PortalObject> i = windows.iterator();

        while (i.hasNext()) {

            Window window = (Window) i.next();

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            WindowNavigationalState windowNavState = (WindowNavigationalState) controllerCtx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
            // On regarde si la fenêtre est en vue MAXIMIZED


            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {

                // On la force en vue NORMAL
                WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL, windowNavState.getMode(),
                        windowNavState.getContentState());
                controllerCtx.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
            }
        }
        
        controllerCtx.removeAttribute(Scope.REQUEST_SCOPE, "osivia.taskbar.active.id");
    }

    
    
    
    /**
     * Check if current user is an administrator.
     *
     * @param controllerContext controller context
     * @return true if current user is an administrator
     */
    public static boolean isAdministrator(ControllerContext controllerContext) {
        Boolean isAdministrator = (Boolean) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin");
        if (isAdministrator == null) {

            isAdministrator = controllerContext.getServerInvocation().getServerContext().getClientRequest().isUserInRole("Administrators");
            
          
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin", isAdministrator);
        }
        return isAdministrator;
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

            PortalObjectUtilsInternal.setPageId(controllerContext, rpc.getPage().getId());

        }

 


        ControllerResponse resp;

        try {

            resp = (ControllerResponse) cmd.invokeNext();

        } catch (Exception e) {
            
            throw e;
        } finally {
          
        }

 
        if (cmd instanceof RenderWindowCommand) {

            RenderWindowCommand rwc = (RenderWindowCommand) cmd;
            Window window = rwc.getWindow();
            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);


            PageProperties properties = PageProperties.getProperties();
            
            // Bootstrap panel style indicator
            String bootstrapPanelStyle = window.getDeclaredProperty("osivia.bootstrapPanelStyle");
            properties.setWindowProperty(windowId, "osivia.bootstrapPanelStyle", bootstrapPanelStyle);
            
            // Should we hide the portlet (empty response + hideEmptyPortlet positionned)
            boolean hidePortlet = false;
            String emptyResponse = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.emptyResponse." + windowId);
            if ("1".equals(emptyResponse)) {
                if ("1".equals(window.getDeclaredProperty("osivia.hideEmptyPortlet"))) {
                    hidePortlet = true;
                }
            }
            properties.setWindowProperty(windowId, "osivia.hidePortlet", Boolean.toString(hidePortlet));
            
            
            // Hide edition bar in admin mode and MAXIMIZED state
            Boolean hideEditionBar = false;
            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
            WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            if ((windowNavState != null) && Mode.ADMIN.equals(windowNavState.getMode())) {
                hideEditionBar = true;
            }
            
            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                hideEditionBar = true;
            }
            
            if( !hideEditionBar)
                cmsEditionService.prepareEdition(controllerContext, window);
        }  
        
        
        if ((cmd instanceof InvokePortletWindowActionCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {
            // Current window
            Window window = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode"))) {
                Collection<PortalObject> windows = new ArrayList<PortalObject>(window.getPage().getChildren(PortalObject.WINDOW_MASK));

                unsetMaxMode(windows, controllerContext);
            }
        }
        
        

        //
        return resp;
    }


    

}
