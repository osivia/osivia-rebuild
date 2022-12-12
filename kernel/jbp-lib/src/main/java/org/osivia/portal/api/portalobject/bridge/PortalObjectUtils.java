/**
 *
 */
package org.osivia.portal.api.portalobject.bridge;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.*;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;


/**
 * Utility class with null-safe methods for portal objects, session and users
 *
 * @author Jean-Sébastien Steux
 * @see PortalObject
 */
public class PortalObjectUtils {

    private static PortalBridge bridgeService = null;

    /**
     * Default constructor.
     * PortalObjectUtils instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public PortalObjectUtils() {
        super();
    }


    private static PortalBridge getService() {
        if( bridgeService == null)  {
            bridgeService = Locator.getService(PortalBridge.class);
        }
        
        return bridgeService;
    }
    
    /**
     * Get current page portal object identifier.
     *
     * @param controllerContext controller context
     * @return current page portal object identifier
     */
    public static final PortalObjectId getPageId(PortalControllerContext portalCtx) {
       return getService().getPageId(portalCtx);

    }


    /**
     * Get current page.
     *
     * @param controllerContext controller context
     * @return current page
     */
    public static final Page getPage(PortalControllerContext portalCtx) {
        return getService().getPage(portalCtx);
    }

    
    /**
     * Get current page.
     *
     * @param controllerContext controller context
     * @return current page
     */
    public static final boolean isDefaultMemberPage(PortalControllerContext portalCtx) {
        return getService().isDefaultMemberPage(portalCtx);
    }
    

    /**
     * Get current portal 
     *
     * @param controllerContext controller context
     * @return current portal
     */
    public static final Portal getPortal(PortalControllerContext portalCtx) {
        return getService().getPortal(portalCtx);
    }

    /**
     * Gets the portal name.
     *
     * @param controllerContext the controller context
     * @return the portal name
     */
    public static final String getPortalName(PortalControllerContext controllerContext) {
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
        return portalName;
    }

    /**
     * Checks if is admin.
     *
     * @param portalCtx the portal ctx
     * @return true, if is admin
     */
    public static final boolean  isAdmin(PortalControllerContext portalCtx) {
        return getService().isAdmin(portalCtx);
    }
    
    
    /**
     * Checks if is admin.
     *
     * @param portalCtx the portal ctx
     * @return true, if is admin
     */
    public static final boolean  isPageRepositoryManager(PortalControllerContext portalCtx) {
        return getService().isPageRepositoryManager(portalCtx);
    }
    
    
    
    /**
     * Get current portal from any portal object.
     *
     * @param po portal object
     * @return current portal
     */
    public static final Portal getPortal(PortalObject po) {
        Portal portal = null;
        if (po instanceof Portal) {
            portal = (Portal) po;
        } else if (po instanceof Page) {
            Page page = (Page) po;
            portal = page.getPortal();
        } else if (po instanceof Window) {
            Window window = (Window) po;
            Page page = window.getPage();
            if (page != null) {
                portal = page.getPortal();
            }
        }
        return portal;
    }





    /**
     * Check if a portal object belongs to a portal type "space".
     *
     * @param po portal object to check, may be null
     * @return true if portal object belongs to a space site
     */
    public static final boolean isSpaceSite(PortalObject po) {
        if (po == null) {
            return false;
        }

        // Get current portal
        Portal portal = getPortal(po);
        if (portal == null) {
            return false;
        }

        String portalType = portal.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
        return StringUtils.equals(InternalConstants.PORTAL_TYPE_SPACE, portalType);
    }



    /**
     * Return HTML safe portal object identifier.
     *
     * @param id portal object identifier
     * @return HTML safe identifier
     */
    public static String getHTMLSafeId(PortalObjectId id) {
        if (id == null) {
            return null;
        }

        String safestFormat = id.toString(PortalObjectPath.SAFEST_FORMAT);
        try {
            return URLEncoder.encode(safestFormat, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }



    /**
     * Get maximized window.
     * 
     * @param controllerContext controller context
     * @param page page
     * @return maximized window
     */
    public static Window getMaximizedWindow(PortalControllerContext portalControllerContext, Page page) {
       return getService().getMaximizedWindow(portalControllerContext, page);
    }

    
    /**
     * Gets the object.
     *
     * @param portalCtx the portal ctx
     * @param id the id
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static PortalObject  getObject(PortalControllerContext portalCtx, PortalObjectId id) throws IllegalArgumentException {
        return getService().getObject(portalCtx, id);
    }
    
    
    
    /**
     * Gets the domains.
     *
     * @param portalCtx the portal ctx
     * @return the domains
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static List<?>  getDomains(PortalControllerContext portalCtx) throws IllegalArgumentException {
        return getService().getDomains(portalCtx);
    }
    
    /**
     * Gets the portal context path.
     *
     * @param portalControllerContext the portal controller context
     * @return the portal context path
     */
    public static String getPortalContextPath(PortalControllerContext portalCtx )    {
        return getService().getPortalContextPath(portalCtx);
    }
    

    
    
    /**
     * Sets the portal session attribute.
     *
     * @param portalCtx the portal ctx
     * @param name the name
     * @param object the object
     */
    public static void setPortalSessionAttribute(PortalControllerContext portalCtx, String name, Object object)    {
        getService().setPortalSessionAttribute(portalCtx, name, object);
    }
    
    /**
     * Gets the portal session attribute.
     *
     * @param portalCtx the portal ctx
     * @param name the name
     * @return the portal session attribute
     */
    public static Object getPortalSessionAttribute(PortalControllerContext portalCtx, String name)    {
       return getService().getPortalSessionAttribute(portalCtx, name);
    }
    
    /**
     * Is the user explicitly refreshing the page
     */
    public static boolean isUserRefreshingPage()	{
    	 return getService().isRefreshingPage();
    }
    
    /** 
     * Get error page associated to current host
     * @param request
     * @return
     */
    public static String getHostErrorPageURI(HttpServletRequest request)	{
    	return getService().getHostErrorPageURI( request);
    }
    
    /**
     * Get charte context associated with current host
     * @param request
     * @return
     */
    public static String getHostCharteContext(HttpServletRequest request)	{
    	return getService().getHostCharteContext( request);
    }
    
    /**
     * Get CMS ID of current host portal
     * 
     * @param request
     * @return
     */
    public static UniversalID getHostPortalID(HttpServletRequest request)	{
    	return getService().getHostPortalID( request);
	}
    
    
	/**
	 * Check if the repository is public or associated to current host
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isRepositoryCompatibleWithHost(HttpServletRequest request, String repository) {
		return getService().checkIfRepositoryIsCompatibleWithHost( request, repository);
	}

}
