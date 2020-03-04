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
package org.osivia.portal.core.urls;


import java.util.Map;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.context.ControllerContextAdapter;

/**
 * Portal URL factory implementation.
 * 
 * On
 *
 * @author Jean-Sébastien Steux
 * @see IPortalUrlFactory
 */
public class PortalUrlFactory implements IPortalUrlFactory {

   
    /**
     * {@inheritDoc}
     */
    @Override
    
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties) throws PortalException {
        
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        
        return"url";
    }
    

    /* 
    
    public String getStartPortletUrlOld(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties) throws PortalException {
       
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Window properties
        if (windowProperties == null) {
            windowProperties = new HashMap<String, String>();
        }

        // URL
        String url;
        try {
            // Current portal name
            String currentPortal = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

            // Page identifier
            String pageId;
            // Region
            String regionId;
            // Window
            String windowName;


            // Default
            PortalObjectId pageObjectId = PortalObjectUtils.getPageId(controllerContext);
            pageId = URLEncoder.encode(pageObjectId.toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
            regionId = "virtual";
            windowName = "dynamicPortlet";




            // Start dynamic window command
            ControllerCommand command = new StartDynamicWindowCommand();

            // Portal URL
            PortalURLImpl portalUrl = new PortalURLImpl(command, controllerContext, null, null);

            // URL
            StringBuilder builder = new StringBuilder();
            builder.append(portalUrl.toString());
            builder.append("&pageId=").append(pageId);
            builder.append("&regionId=").append(regionId);
            builder.append("&windowName=").append(windowName);
            builder.append("&instanceId=").append(portletInstance);

            url = builder.toString();

        } catch (Exception e) {
            throw new PortalException(e);
        }

        return url;
    }
*/

}
