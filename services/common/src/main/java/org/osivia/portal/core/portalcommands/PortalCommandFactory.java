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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPortalCommand;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageLocationResponse;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.content.ViewContentCommand;
import org.osivia.portal.core.dynamic.RestorablePageUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.RestorePageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.osivia.portal.core.profiles.IProfileManager;
import org.osivia.portal.core.tracker.RequestContextUtil;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author jeanseb
 * @see DefaultPortalCommandFactory
 */
public class PortalCommandFactory extends DefaultPortalCommandFactory {

    protected static final Log logger = LogFactory.getLog(PortalCommandFactory.class);

    public static String MODAL = "/modal";
    public static String NO_AUTHREDIRECT = "/nr";
    private static String NO_AUTHREDIRECT_SLASH = NO_AUTHREDIRECT+"/";
    public static String SESSION = "/session";
    public static String SESSION_SLASH = SESSION + "/";
    public static String VIEW_STATE = "/vs";
    public static String VIEW_STATE_SLASH = VIEW_STATE + "/";
    
    private CMSService cmsService;

    


    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }
    
    private IProfileManager profileManager;
    
    private IProfileManager getProfileManager() {
        if (profileManager == null) {
            profileManager = Locator.getService(IProfileManager.class);
        }

        return profileManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

        boolean modal = false;
        boolean noredirect = false;
        String browserSession = null;
        
        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        
        
        if( "true".equals(request.getHeader("refresh")))
            PageProperties.getProperties().setRefreshingPage(true);
        
        

        
        // Just to prevent from redirection
        if (requestPath.startsWith(MODAL)) {
            requestPath = requestPath.substring(MODAL.length() );
            modal = true;
        }
        
        // Just to prevent from redirection
        if (requestPath.startsWith(NO_AUTHREDIRECT_SLASH)) {
            requestPath = requestPath.substring(NO_AUTHREDIRECT.length() );
            noredirect = true;
        }
        
        
        // To check the session
        if (requestPath.startsWith(SESSION_SLASH)) {
            int sessionIndex = requestPath.substring(SESSION_SLASH.length()).indexOf("/");
            if( sessionIndex != -1)    {
                browserSession = requestPath.substring(SESSION_SLASH.length() , sessionIndex+SESSION.length() +1);
                requestPath = requestPath.substring(SESSION_SLASH.length()+sessionIndex );
            }
        }
        if( browserSession == null)
            browserSession = request.getHeader("session_check");
        
        
        // To check the view state
        String viewState = request.getHeader("view_state");
        if (requestPath.startsWith(VIEW_STATE)) {
            int viewStateIndex = requestPath.substring(VIEW_STATE_SLASH.length()).indexOf("/");
            if( viewStateIndex != -1)    {
                viewState = requestPath.substring(VIEW_STATE_SLASH.length() , viewStateIndex+VIEW_STATE.length() +1);
                requestPath = requestPath.substring(VIEW_STATE_SLASH.length()+viewStateIndex );
            }
        }       
        

        // compute  server session check
        // session check controls wether session is shared between browser and server

        String currentServerCheck = (String) request.getSession().getAttribute(InternalConstants.SESSION_CHECK);
        String currentUSer = (request.getRemoteUser() != null) ? request.getRemoteUser() : "_anonymous";
        
        boolean updateServerCheck = false;
        if( StringUtils.isNotEmpty(currentServerCheck))   {
            String[] tokens = currentServerCheck.split(":");
            String userName = tokens[0];
            String sessionId = tokens[1];
            if( !StringUtils.equals(request.getSession().getId(), sessionId) || ! StringUtils.equals(userName, request.getRemoteUser()))    {
                updateServerCheck = true;
            }
        }   else
            updateServerCheck = true;

        if( updateServerCheck) {
            currentServerCheck = currentUSer + ":" + request.getSession().getId();
            request.getSession().setAttribute(InternalConstants.SESSION_CHECK, currentServerCheck);
        }

        

        
        if (viewState != null) {
            PageMarkerUtils.setViewState(controllerContext, Integer.parseInt(viewState));
            PageMarkerUtils.restorePageState(controllerContext, viewState);
        }
        

        ControllerCommand cmd = null;
        
               
        boolean handleMapping = true;
        
               
        // we can't handle an ajax Request without incorrect authentification
        // So we ask the browser to reload currentpage
        if( (modal || noredirect) && !StringUtils.equals(currentServerCheck, browserSession))    {
            handleMapping = false;
        }
        
        
        
        if( handleMapping) {
             cmd = super.doMapping(controllerContext, invocation, host, contextPath, requestPath);
        }
        
        
        
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
        
        
        
        
        
        
        
               
        // redirect to default page
        // portal, portal/auth -> cmd = null
        // signout -> ViewportalCommand
        if( cmd == null || cmd instanceof ViewPortalCommand)    {

            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            UniversalID redirectId;
            try {
                 UniversalID portalId;
                 CMSContext cmsContext = new CMSContext(portalCtx);
                 if( cmd == null)   {
                     portalId = getCMSService().getDefaultPortal(cmsContext);
                 }  else{
                     PortalObjectId poid = ((ViewPortalCommand) cmd).getTargetId();
                     portalId = new UniversalID(poid.getNamespace(), poid.getPath().getLastComponentName());
                 } 
  
                 Profile profile = getProfileManager().getMainProfile(portalCtx, portalId);
                 
                 if( profile != null && profile.getUrl() != null)
                     redirectId = new UniversalID(portalId.getRepositoryName(), profile.getUrl());
                 else 
                     redirectId = portalId;
                 
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
            cmd = new ViewContentCommand(redirectId.toString(), Locale.FRENCH, false, null, null, null, null);
        }
        

        

        
        // Force reload of page is session is not consistent
        // Example of use case : go back to anonymous page once logged ...
        // TODO : validate this hack
        

//        if( StringUtils.isNotEmpty(request.getRemoteUser()) && controllerContext.getType() == ControllerContext.AJAX_TYPE && !StringUtils.equals(currentServerCheck, request.getHeader("session_check"))) {
//            String url = controllerContext.renderURL(cmd, null, null);         
//            request.setAttribute("osivia.full_refresh_url", url);
//            System.out.println("portalcommandfactory full refresh");
//         }

        
        
        boolean returnToDefaultPage = false;
        boolean handleAjaxReload = false;
        boolean browserReload = false;        
        
        
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
            returnToDefaultPage = true;
        }
        
        
 
        // open a modal after session is lost
        /*
        if( modal && !StringUtils.equals(currentServerCheck, browserSession))    {
            browserReload = true;
        }
*/
        
        // Ajax no redirect url (example : load select2)
        if( noredirect && !StringUtils.equals(currentServerCheck, browserSession))    {
            handleAjaxReload = true;
        }

        /*
        // navigation inside the modal after session losed
        if( requestPath.startsWith("/templates/OSIVIA_PORTAL_UTILS") && !StringUtils.equals(currentServerCheck, browserSession)) {
            if( ! (cmd instanceof InvokePortletWindowResourceCommand))
                browserReload = true;
        }
        */
        
        // back after a session losed
        if( cmd instanceof RestorePageCommand && !StringUtils.equals(currentServerCheck, browserSession) )    {
            returnToDefaultPage = true;
        }
        
        
        
        if( browserReload) {
            // Create an anused command
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            UniversalID defaultPortalId;
            try {
                 defaultPortalId = getCMSService().getDefaultPortal(new CMSContext(portalCtx));
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
            cmd = new ViewContentCommand(defaultPortalId.toString(), Locale.FRENCH, false, null, null, null, null);
            request.setAttribute("osivia.full_refresh_url", "/refresh");
          
        } else
        
        if( handleAjaxReload) {
            // Create an anused command
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            UniversalID defaultPortalId;
            try {
                 defaultPortalId = getCMSService().getDefaultPortal(new CMSContext(portalCtx));
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
            cmd = new ViewContentCommand(defaultPortalId.toString(), Locale.FRENCH, false, null, null, null, null);
            request.setAttribute("osivia.full_refresh_url", "/portal-assets/redirection/ajax-reload.jsp");
          
        }   else
        
        if( returnToDefaultPage) {
            // only use case : home page
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            UniversalID defaultPortalId;
            try {
                 defaultPortalId = getCMSService().getDefaultPortal(new CMSContext(portalCtx));
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
            cmd = new ViewContentCommand(defaultPortalId.toString(), Locale.FRENCH, false, null, null, null, null);
            String url = controllerContext.renderURL(cmd, null, null);    
            //request.setAttribute("osivia.full_refresh_url", "/refresh");
            request.setAttribute("osivia.full_refresh_url", url);
            System.out.println("portalcommandfactory full refresh");
        }

          
 /*       
        if(cmd instanceof RenderPageCommand)    {
            PortalObjectUtilsInternal.setPageId(controllerContext, ((RenderPageCommand) cmd).getTargetId());
        }
 */       
      
 

        return cmd;
    }

}
