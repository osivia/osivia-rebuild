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


import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.content.ViewContentCommand;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalcommands.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.osivia.portal.core.utils.URLUtils;
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

@Service("osivia:service=UrlFactory")
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
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties,  PortalUrlType type) throws PortalException {
        return getStartPortletUrl(portalControllerContext, portletInstance, windowProperties, new HashMap<String,String>(), type);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, Map<String, String> windowParams, PortalUrlType type) throws PortalException {
       
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
                
                windowProperties.put("osivia.windowState", "normal");
                windowProperties.put("osivia.hideTitle", "1");

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
                builder.append("&params=").append(WindowPropertiesEncoder.encodeProperties(windowParams));            
                
    
                url = builder.toString();
                
                url = this.adaptPortalUrlToModal(portalControllerContext, builder.toString());

            } else  {
                // Default
                PortalObjectId pageObjectId = PortalObjectUtilsInternal.getPageId(controllerContext);
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
                builder.append("&params=").append(WindowPropertiesEncoder.encodeProperties(windowParams));            
                
                
    
                url = builder.toString();
        }

        } catch (Exception e) {
            throw new PortalException(e);
        }

        return url;
    }
    
    
    /**
     * {@inheritDoc}
     */

    public String adaptPortalUrlToModal(PortalControllerContext portalCtx, String originalUrl) {


        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
        // Server context
        final ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        // Portal context path
        final String portalContextPath = serverContext.getPortalContextPath();


        final String prefix = StringUtils.substringBefore(originalUrl, portalContextPath);
        String suffix = StringUtils.substringAfter(originalUrl, portalContextPath);



        boolean auth = false;
        if (suffix.startsWith("/auth/")) {
            suffix = StringUtils.removeStart(suffix, "/auth/");
            auth = true;
        } else {
            suffix = StringUtils.removeStart(suffix, "/");
        }

        // Popup command
        String popupCommand = PortalCommandFactory.NO_AUTHREDIRECT+PortalCommandFactory.SESSION+"/";


        // URL
        final StringBuilder url = new StringBuilder();
        url.append(prefix);
        url.append(portalContextPath);
        if (auth) {
            url.append("/auth");
        }
        if (popupCommand != null) {
            url.append(popupCommand);
        }
        url.append(suffix);

        return url.toString();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCMSUrl(PortalControllerContext portalControllerContext, String pagePath, String cmsPath, Map<String, String> pageParams,
                            String contextualization, String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference) {

        return getBasePortalUrl( portalControllerContext);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHttpErrorUrl(PortalControllerContext portalControllerContext, int httpErrorCode) {
        final HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        final String uri = System.getProperty("error.defaultPageUri");
        final String url = URLUtils.createUrl(request, uri, null);
        return URLUtils.addParameter(url, "httpCode", String.valueOf(httpErrorCode));
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance,
                                           Map<String, String> windowProperties, Map<String, String> params) throws PortalException {
        
        return null;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties)
            throws PortalException {
        return this.getStartPortletUrl(portalControllerContext, portletInstance, windowProperties, PortalUrlType.DEFAULT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermaLink(PortalControllerContext portalCtx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException {
        
        if (IPortalUrlFactory.PERM_LINK_TYPE_SHARE.equals(permLinkType)) {
           return getViewContentUrl(portalCtx, new CMSContext(portalCtx), new UniversalID("nx",cmsPath));
        }        
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
                                  Map<String, String> params) throws PortalException {

        try {
            // TODO : refonte a valider après report
            final ControllerCommand cmd = new StartDynamicPageCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

            final String parentId = URLEncoder
                    .encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
            final String templateId = URLEncoder
                    .encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            String url = portalURL.toString();
            url += "&parentId=" + parentId + "&pageName=" + pageName + "&templateId=" + templateId + "&props=" + WindowPropertiesEncoder.encodeProperties(props)
                    + "&params=" + WindowPropertiesEncoder.encodeProperties(params);
            return url;
        } catch (final Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName, Map<String, String> props, Map<String, String> params)
            throws PortalException {
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
        // if (portalName == null)
        // portalName = "default";

        portalName = "/" + portalName;

        return this.getStartPageUrl(ctx, portalName, pageName, templateName, props, params);
    }
    
    



    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl) throws PortalException {
        //TODO
        return redirectionUrl;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHomePageUrl(PortalControllerContext portalControllerContext, boolean refresh) throws PortalException {
        // Controller context


        return "";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getBasePortalUrl(PortalControllerContext portalControllerContext) {
        final HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        return URLUtils.createUrl(request);
    }
  
   
}
