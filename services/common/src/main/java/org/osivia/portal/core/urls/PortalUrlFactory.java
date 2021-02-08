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


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.CharEncoding;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.content.ViewContentCommand;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Portal URL factory implementation.
 * 
 * On
 *
 * @author Jean-Sébastien Steux
 * @see IPortalUrlFactory
 */

@Service
public class PortalUrlFactory implements IPortalUrlFactory {

   
    @Autowired
    private IPreviewModeService previewModeService;
    
    
    /** The locale service. */
    @Autowired
    private ILocaleService localeService;

    private IPreviewModeService getPreviewModeService() {

        return previewModeService;       
    }
    
    private ILocaleService getLocaleService() {

        return localeService;       
    }
    
    
    
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, PortalUrlType type) throws PortalException {
       
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Window properties
        if (windowProperties == null) {
            windowProperties = new HashMap<String, String>();
        }

        // URL
        String url;
        try {
            // Page identifier
            String pageId;
            // Region
            String regionId;
            // Window
            String windowName;

            if (PortalUrlType.MODAL.equals(type)) {
                // Modal
                UniversalID pageID = new UniversalID("templates", "OSIVIA_PAGE_MODAL");
                UniversalID parentID = new UniversalID("templates", "OSIVIA_PORTAL_UTILS");

                regionId = "modal-region";
                windowName = "modal-window";
                
                // Start dynamic window command
                ControllerCommand command = new StartDynamicWindowInNewPageCommand();
    
                // Portal URL
                PortalURLImpl portalUrl = new PortalURLImpl(command, controllerContext, null, null);
    
                // URL
                StringBuilder builder = new StringBuilder();
                builder.append(portalUrl.toString());
                builder.append("&parentId=").append(URLEncoder.encode(parentID.toString(), CharEncoding.UTF_8));
                builder.append("&pageId=").append(URLEncoder.encode(pageID.toString(), CharEncoding.UTF_8));
                builder.append("&regionId=").append(regionId);
                builder.append("&pageDisplayName=").append("?????");
                builder.append("&windowName=").append(windowName);
                builder.append("&instanceId=").append(portletInstance);
                builder.append("&props=").append(WindowPropertiesEncoder.encodeProperties(windowProperties));
                builder.append("&params=").append(WindowPropertiesEncoder.encodeProperties(new HashMap<String, String>()));            
                
    
                url = builder.toString();
                


            } else  {
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
                builder.append("&props=").append(WindowPropertiesEncoder.encodeProperties(windowProperties));
                builder.append("&params=").append(WindowPropertiesEncoder.encodeProperties(new HashMap<String, String>()));            
                
    
                url = builder.toString();
        }

        } catch (Exception e) {
            throw new PortalException(e);
        }

        return url;
    }

    @Override
    public String getViewContentUrl(PortalControllerContext portalControllerContext, CMSContext cmsContext,UniversalID id) throws PortalException {


        Locale locale = cmsContext.getlocale();

        Boolean preview = cmsContext.isPreview();
        
        final ViewContentCommand cmd = new ViewContentCommand(id.toString(), locale, preview);
        
 

        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);

        String url = portalURL.toString();


        return url;

    }


}
