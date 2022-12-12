package org.osivia.portal.api.portalobject.bridge;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.page.PageProperties;

/**
 * The Interface PortalBridge.
 */
public interface PortalBridge {
    
        /**
         * Gets the page portal object.
         *
         * @param controllerContext the controller context
         * @return the page
         */
        public  Page getPage(PortalControllerContext portalCtx) ;
        
        
        /**
         * Checks if is default member page.
         *
         * @param portalCtx the portal ctx
         * @return true, if is default member page
         */
        public boolean isDefaultMemberPage(PortalControllerContext portalCtx);

        /**
         * Gets the page id.
         *
         * @param controllerContext the controller context
         * @return the page id
         */
        public PortalObjectId getPageId(PortalControllerContext portalCtx);
        
        /**
         * Get current portal from controller context.
         *
         * @param controllerContext controller context
         * @return current portal
         */
        public  Portal getPortal(PortalControllerContext portalCtx) ;
        
        
        
        /**
         * Checks if user is administrator.
         *
         * @param controllerContext the controller context
         * @return true, if is admin
         */
        public  boolean  isAdmin(PortalControllerContext portalCtx);
        
        
        
        /**
         * Checks if user is repository manager.
         *
         * @param controllerContext the controller context
         * @return true, if is admin
         */
        public  boolean  isPageRepositoryManager(PortalControllerContext portalCtx);
        
        
        /**
         * Gets the maximized window.
         *
         * @param portalControllerContext the portal controller context
         * @param page the page
         * @return the maximized window
         */
        public  Window getMaximizedWindow(PortalControllerContext portalControllerContext, Page page);
        
        
        /**
         * Gets the portal object.
         *
         * @param portalCtx the portal ctx
         * @param id the id
         * @return the object
         * @throws IllegalArgumentException the illegal argument exception
         */
        public  PortalObject  getObject(PortalControllerContext portalCtx, PortalObjectId id) throws IllegalArgumentException ;
        
        
        
        /**
         * Gets the domains.
         *
         * @param portalCtx the portal ctx
         * @return the domains
         * @throws IllegalArgumentException the illegal argument exception
         */
        public List<?>  getDomains(PortalControllerContext portalCtx) throws IllegalArgumentException ;
        
        
        /**
         * Gets the portal context path.
         *
         * @param portalControllerContext the portal controller context
         * @return the portal context path
         */
        public  String getPortalContextPath(PortalControllerContext portalControllerContext ) ;
        
        
        
        /**
         * Sets a portal session attribute.
         *
         * @param portalControllerContext the portal controller context
         * @param name the name
         * @param object the object
         */
        public  void setPortalSessionAttribute(PortalControllerContext portalControllerContext, String name, Object object)   ;
        
        
        /**
         * Gets the portal session attribute.
         *
         * @param portalControllerContext the portal controller context
         * @param name the name
         * @return the portal session attribute
         */
        public Object getPortalSessionAttribute(PortalControllerContext portalControllerContext, String name) ;
        

        
        /**
         * Is the user explicitly refreshing the page
         */
        public boolean isRefreshingPage() ;


        /**
         * Get errorPageUrl
         * @param request
         * @return
         */
		String getHostErrorPageURI(HttpServletRequest request);


		/**
		 * Get current host context
		 * @param request
		 * @return
		 */
		String getHostCharteContext(HttpServletRequest request);


		/**
		 * Get Host Default portal CMS ID
		 * 
		 * @param request
		 * @return
		 */
		UniversalID getHostPortalID(HttpServletRequest request); 
		
		/**
		 * Check if the repository is public or associated to current host
		 * 
		 * @param request
		 * @return
		 */
		public boolean checkIfRepositoryIsCompatibleWithHost(HttpServletRequest request, String repository);




}