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
package org.osivia.portal.api.urls;


import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;



import java.util.Map;

/**
 * Portal URL factory API interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface IPortalUrlFactory {
    
    String MBEAN_NAME = "osivia:service=UrlFactory";

    /**
     * Permalink type "CMS".
     */
    String PERM_LINK_TYPE_CMS = "cms";
    /**
     * Permalink type "RSS".
     */
    String PERM_LINK_TYPE_RSS = "rss";    
    /**
     * Permalink type "page".
     */
    String PERM_LINK_TYPE_PAGE = "page";    
    
    /**
     * Permalink type share.
     */
    String PERM_LINK_TYPE_SHARE = "share";
    /**
     * Contextualization type "portlet".
     */
    String CONTEXTUALIZATION_PORTLET = "portlet";
    
    /**
     * Contextualization type "portal".
     */
    String CONTEXTUALIZATION_PORTAL = "portal";    
    
    
    
    /**
     * Display context refresh.
     */
    String DISPLAYCTX_REFRESH = "refreshPageAndNavigation";
    
    /**
     * Display context preview (live version for validation purpose).
     */
    String DISPLAYCTX_PREVIEW_LIVE_VERSION = "preview";    
    
    /**
     * Popup URL adapter close status.
     */
    int POPUP_URL_ADAPTER_CLOSE = 1;
    
    
    
    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance         portlet instance
     * @param windowProperties        window properties
     * @param type the type
     * @return URL
     * @throws PortalException the portal exception
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, PortalUrlType type)
            throws PortalException;
    
    
    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance         portlet instance
     * @param windowProperties        window properties
     * @param windowParams the window params
     * @param type the type
     * @return URL
     * @throws PortalException the portal exception
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties,Map<String, String> windowParams, PortalUrlType type)
            throws PortalException;
    
    

    /**
     * Gets the view content url.
     *
     * @param portalControllerContext the portal controller context
     * @param cmsContext the cms context
     * @param id the id
     * @return the view content url
     * @throws PortalException the portal exception
     */
    public String getViewContentUrl(PortalControllerContext portalControllerContext, CMSContext cmsContext, UniversalID id) throws PortalException;

    
    /**
     * Get CMS URL.
     *
     * @param portalControllerContext portal controller context
     * @param pagePath                page path
     * @param cmsPath                 CMS path
     * @param pageParams              page parameters
     * @param contextualization       contextualization
     * @param displayContext          display context
     * @param hideMetaDatas           hide meta datas
     * @param scope                   scope
     * @param displayLiveVersion      display live version
     * @param windowPermReference     window perm reference
     * @return CMS url
     */
    @Deprecated
    String getCMSUrl(PortalControllerContext portalControllerContext, String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference);


    /**
     * Get HTTP error page URL.
     *
     * @param portalControllerContext portal controller context
     * @param httpErrorCode           HTTP error code (example : 404)
     * @return HTTP error page URL
     */
    String getHttpErrorUrl(PortalControllerContext portalControllerContext, int httpErrorCode);
    
    /**
     * Get base portal URL.
     *
     * @param portalControllerContext portal controller context
     * @return base portal URL
     */
    String getBasePortalUrl(PortalControllerContext portalControllerContext);    

    @Deprecated
    String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance,
            Map<String, String> windowProperties, Map<String, String> params) throws PortalException;

    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties)
            throws PortalException;
    @Deprecated
    String getPermaLink(PortalControllerContext portalControllerContext, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException;


    /**
     * Get start page URL.
     *
     * @param portalControllerContext portal controller context
     * @param parentName              parent page name
     * @param pageName                page name
     * @param templateName            template name
     * @param props                   page properties
     * @param params                  page parameters
     * @return start page URL
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String parentName, String pageName, String templateName, Map<String, String> props,
                           Map<String, String> params) throws PortalException;


    /**
     * Get start page URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageName                page name
     * @param templateName            template name
     * @param props                   page properties
     * @param params                  page parameters
     * @return start page URL
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String pageName, String templateName, Map<String, String> props,
                           Map<String, String> params) throws PortalException;

    String getHomePageUrl(PortalControllerContext portalControllerContext, boolean refresh) throws PortalException;


    String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl) throws PortalException;

  
    
}
