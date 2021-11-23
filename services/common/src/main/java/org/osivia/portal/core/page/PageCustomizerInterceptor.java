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
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

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
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;


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

            Principal principal = controllerContext.getServerInvocation().getServerContext().getClientRequest().getUserPrincipal();

            if( principal != null && principal.toString().contains("Administrators")) {
                isAdministrator =  true;
            }   else
                isAdministrator =  false;
 

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

        if (cmd instanceof RenderWindowCommand) {

            RenderWindowCommand rwc = (RenderWindowCommand) cmd;
            Window window = rwc.getWindow();
            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);


            PageProperties properties = PageProperties.getProperties();
            
            // Bootstrap panel style indicator
            String bootstrapPanelStyle = window.getDeclaredProperty("osivia.bootstrapPanelStyle");
            properties.setWindowProperty(windowId, "osivia.bootstrapPanelStyle", bootstrapPanelStyle);

        }  


        ControllerResponse resp;

        try {

            resp = (ControllerResponse) cmd.invokeNext();

        } catch (Exception e) {
            
            throw e;
        } finally {
          
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
