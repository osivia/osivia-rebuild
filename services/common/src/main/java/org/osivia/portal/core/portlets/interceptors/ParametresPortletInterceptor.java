/*
 * (C) Copyright 2020 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.core.portlets.interceptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.ResourceInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.core.page.PageProperties;


/**
 * Construction d'un intercepteur de porlet minimal
 *
 * @see PortletInvokerInterceptor
 */
public class ParametresPortletInterceptor extends PortletInvokerInterceptor {

    /**
     * Constructor.
     */
    public ParametresPortletInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
        // Controller context
        ControllerContext controllerContext = (ControllerContext) invocation.getAttribute("controller_context");

        if (controllerContext != null) {
            Map<String, Object> attributes = invocation.getRequestAttributes();
            if (attributes == null) {
                attributes = new HashMap<String, Object>();
            }

            // Ajout de la window
            String windowId = invocation.getWindowContext().getId();

                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

                Window window = (Window) controllerContext.getController().getPortalObjectContainer().getObject(poid);

                attributes.put("osivia.window", window);

                // Ajout du controleur
                attributes.put("osivia.controller", controllerContext);


                if (!(invocation instanceof ResourceInvocation)) {

                    List<MenubarItem> menuBar = new ArrayList<MenubarItem>();

                    attributes.put(Constants.PORTLET_ATTR_MENU_BAR, menuBar);
                }
                
                WrappedPortalWindow portalWindow = new WrappedPortalWindow( window);
                attributes.put("osivia.portal.window", portalWindow);
                
                
               // HTTP Request
                HttpServletRequest httpRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
                attributes.put(Constants.PORTLET_ATTR_HTTP_REQUEST, httpRequest);

                boolean refresh = PageProperties.getProperties().isRefreshingPage();
                if( refresh)
                    attributes.put(Constants.PORTLET_ATTR_PAGE_REFRESH, true);

                // Set attributes
                invocation.setRequestAttributes(attributes);
            
        }

        PortletInvocationResponse response = super.invoke(invocation);

        if (response instanceof UpdateNavigationalStateResponse) {
            Map<String, Object> attributes = ((UpdateNavigationalStateResponse) response).getAttributes();
            if (BooleanUtils.toBoolean(String.valueOf(attributes.get("osivia.ajax.preventRefresh")))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.ajax.preventRefreshWindowId", invocation.getWindowContext().getId());
            }            
        }


        return response;
    }

}
