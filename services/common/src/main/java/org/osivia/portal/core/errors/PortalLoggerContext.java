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
package org.osivia.portal.core.errors;

import java.security.Principal;
import java.util.Set;
import java.util.Stack;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.WindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokeWindowCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.identity.auth.UserPrincipal;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.dynamic.RestorablePageUtils;
import org.osivia.portal.core.error.CustomPageControlPolicy;
import org.osivia.portal.core.error.ErrorValve;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.tracker.ITracker;



/**
 * The Class PortalLoggerContext.
 */
public class PortalLoggerContext {

    /** The page. */
    private final String page;
    
    /** The portlet. */
    private final String portlet;
    
    /** The user. */
    private final String user;
    
    /** The portal session id. */
    private final String portalSessionID;
    
    /** The action. */
    private final boolean action;


    
    /**
     * Checks if is action.
     *
     * @return true, if is action
     */
    public boolean isAction() {
        return action;
    }


    /**
     * Gets the page.
     *
     * @return the page
     */
    public String getPage() {
        return page;
    }


    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    
    /**
     * Gets the portlet.
     *
     * @return the portlet
     */
    public String getPortlet() {
        return portlet;
    }

    
    
    /**
     * Gets the portal session id.
     *
     * @return the portal session id
     */
    public String getPortalSessionID() {
        return portalSessionID;
    }


    /**
     * Instantiates a new portal logger context.
     */
    public PortalLoggerContext() {
        this(null);
    }


    /**
     * Instantiates a new portal logger context.
     *
     * @param windowId the window id
     */
    public PortalLoggerContext(PortalObjectId windowId) {

        /* Get current context (request, command) */


        String newPage = null;
        String newPortlet = null;
        String newUser = null;
        String newPortalSessionID = null;
        boolean newAction = false;


        Stack<?> stack = null;


        HttpServletRequest request = ErrorValve.mainRequest.get();
        String url = null;

        String navigationPath = null;


        if (request != null) {
            // portal url
            url = (String) request.getAttribute("osivia.trace.url");
            if( url == null)
                url = request.getRequestURI();
        }

        ITracker tracker = Locator.findMBean(ITracker.class, "osivia:service=Tracker");
        if (tracker != null) {
            stack = tracker.getStack();

        }

        if (stack != null && stack.size() > 0) {

            Object topCmd = stack.peek();

            if (topCmd instanceof ControllerCommand) {
                // Command
                ControllerCommand command = (ControllerCommand) stack.peek();
                // Controller context
                ControllerContext controllerContext;
                if (command == null) {
                    controllerContext = null;
                } else {
                    controllerContext = command.getControllerContext();
                }

                PortalObjectId pageId = null;

                if (command instanceof WindowCommand) {
                    pageId = new PortalObjectId("", ((WindowCommand) command).getTargetId().getPath().getParent());
                    newPortlet = buildPortletName(command, ((WindowCommand) command).getTargetId());
                    
                    if ((controllerContext != null) && (request == null)) {
                        // error logged inside a portlet
                        request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
                    }
                    
                    if (command instanceof InvokeWindowCommand)
                        newAction = true;

                } else if (command instanceof RenderPageCommand) {
                    pageId = ((RenderPageCommand) command).getTargetId();
                    if (windowId != null) {
                        newPortlet = buildPortletName(command, windowId);
                    }

                }

                // COmpute current page name
                // for cms : /portal/cms/path
                // for dynamic page : template name

                /*
                if ((controllerContext != null) && (pageId != null)) {
                    navigationPath = PagePathUtils.getContentPath(controllerContext, pageId);
                    if (navigationPath != null) {
                        newPage = "/portal/cms" + navigationPath;
                    } else {
                        String restorableName = RestorablePageUtils.getPageLogName(pageId);
                        if (restorableName != null)
                            newPage = restorableName;
                    }
                } else {
                    if (command instanceof CmsCommand) {
                        newPage = "/portal/cms" + ((CmsCommand) command).getCmsPath();
                    }
                }
                */


            }
        }


        if (newPage == null && url != null)
            newPage = url;

        if (request != null) {
            newPortalSessionID = request.getRequestedSessionId();
       }


        // Get user


        if( request != null)    {
            if( request.getUserPrincipal() != null)
                newUser = request.getUserPrincipal().getName();
            else
                // Binary resource
                newUser = (String) request.getAttribute("osivia.delegation.userName");
        }


        portalSessionID = newPortalSessionID;
        page = newPage;
        user = newUser;
        portlet = newPortlet;
        action = newAction;
    }




    /**
     * Builds the portlet name.
     *
     * @param cmd the cmd
     * @param windowId the window id
     * @return the string
     */
    private String buildPortletName(ControllerCommand cmd, PortalObjectId windowId) {
        String newPortlet = null;
        if (windowId == null && cmd instanceof WindowCommand) {
            windowId = ((WindowCommand) cmd).getTargetId();
        }
        if (windowId != null) {
            newPortlet = windowId.getPath().getLastComponentName();
            String portletName = CustomPageControlPolicy.getPortletName(cmd.getControllerContext(), windowId);
            if (portletName != null)
                newPortlet += ":" + portletName;
        }
        return newPortlet;
    }


}
