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

    /**
     * Permalink type "CMS".
     */
    String PERM_LINK_TYPE_CMS = "cms";
    
    /**
     * Contextualization type "portlet".
     */
    String CONTEXTUALIZATION_PORTLET = "portlet";
    
    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance         portlet instance
     * @param windowProperties        window properties
     * @return URL
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, PortalUrlType type)
            throws PortalException;
    
    /**
     * {@inheritDoc}
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

    @Deprecated
    String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance,
            Map<String, String> windowProperties, Map<String, String> params) throws PortalException;

    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties)
            throws PortalException;
    @Deprecated
    String getPermaLink(PortalControllerContext portalControllerContext, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException;

  
}
