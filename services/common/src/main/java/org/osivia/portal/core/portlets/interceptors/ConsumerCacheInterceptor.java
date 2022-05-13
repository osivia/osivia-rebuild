/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.osivia.portal.core.portlets.interceptors;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.portlet.invocation.ActionInvocation;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.spi.UserContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;

/**
 * Cache markup on the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ConsumerCacheInterceptor extends PortletInvokerInterceptor
{

    
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      // Compute the cache key
      String scopeKey = "cached_markup." + invocation.getWindowContext().getId();
      


      // We use the principal scope to avoid security issues like a user loggedout seeing a cached entry
      // by a previous logged in user
      UserContext userContext = invocation.getUserContext();
      
      ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

      
      
   // v2.0-SP1 : cache init on action
      if (invocation instanceof ActionInvocation) {
          userContext.setAttribute(scopeKey, null);


          // JSS 20130319 : shared cache initialization
          if (invocation.getWindowContext() instanceof WindowContextImpl) {
              String windowId = invocation.getWindowContext().getId();
              PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
              Window window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
              String sharedCacheID = window.getDeclaredProperty("osivia.cacheID");

              if ((window != null) && (sharedCacheID != null)) {
                  Map<String, String[]> publicNavigationalState = invocation.getPublicNavigationalState();
                  sharedCacheID = computedCacheID(sharedCacheID, window, publicNavigationalState);
                  userContext.setAttribute("sharedcache." + sharedCacheID, null);
              }
          }
       }
      
      
      //
      if (invocation instanceof RenderInvocation)
      {
         RenderInvocation renderInvocation = (RenderInvocation)invocation;

         //
         StateString navigationalState = renderInvocation.getNavigationalState();
         Map<String, String[]> publicNavigationalState = renderInvocation.getPublicNavigationalState();
         WindowState windowState = renderInvocation.getWindowState();
         Mode mode = renderInvocation.getMode();
         

  
         //
         CacheEntry cachedEntry = null;         
         
         Window window = null;

         if (invocation.getWindowContext() instanceof WindowContextImpl) {
             String windowId = invocation.getWindowContext().getId();
             PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
             window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
         }

         
         
         
         // v2.0.2 -JSS20130318
         // Shared user's cache
         // pour plus de cohérence, le cache partagé est priorisé par rapport au cache portlet

         boolean skipNavigationCheck = false;
         boolean sharedCache = false;
         

         if (window != null) {
             String sharedCacheID = window.getDeclaredProperty("osivia.cacheID");

             if (sharedCacheID != null) {

                 // On controle que l'état permet une lecture depuis le cache partagé

                 if (((navigationalState == null) || (((ParametersStateString) navigationalState).getSize() == 0))
                         && ((windowState == null) || WindowState.NORMAL.equals(windowState)) && ((mode == null) || Mode.VIEW.equals(mode))) {
                     sharedCacheID = computedCacheID(sharedCacheID, window, publicNavigationalState);
                     cachedEntry = (CacheEntry) userContext.getAttribute("sharedcache." + sharedCacheID);
                     if( cachedEntry != null) {
                         
                         // If space Data is refreshed, all shared cache relative to space are refreshed,
                         // even they are not in the page
                         // -> 'ex:quota'        

                         
                         if( sharedCacheID.contains("/"))   {
                             String spaceId = sharedCacheID.substring(0, sharedCacheID.indexOf("/"));
                         
                             PortalControllerContext portalCtx = new PortalControllerContext( ctx.getServerInvocation().getServerContext().getClientRequest());
                             CMSController ctrl = new CMSController(portalCtx);
                             SpaceCacheBean modifiedTs;
                             
                             try    {
                             
                                 CMSSession session = Locator.getService(org.osivia.portal.api.cms.service.CMSService.class).getCMSSession(ctrl.getCMSContext());
    
                                 modifiedTs = session.getSpaceCacheInformations(new UniversalID(spaceId));
                             } catch( Exception e)  {
                                 throw new RuntimeException( e);
                             }
                             
                             if( modifiedTs.getLastSpaceModification() != null) {
                                 if (cachedEntry.getCreationTs() < modifiedTs.getLastSpaceModification())
                                     cachedEntry = null;    
                             }
                             
                             
                             if( cachedEntry != null)   {
                                 sharedCache = true;
                                 skipNavigationCheck = true;
                             }
                             
                         }
                     }
                 }
             }

             if (cachedEntry == null) {
                 cachedEntry = (CacheEntry) userContext.getAttribute(scopeKey);
              }

         }
         
         
         // Don't test for shared cache, because each time you change of navigation item
         // The page is re-created
         if( cachedEntry != null && sharedCache == false)    {
             if (cachedEntry.getCreationTs() < window.getUpdateTs())
                 cachedEntry = null;
         }


        // CMS cache has been modified (portlet level)
        if (cachedEntry != null && window != null) {
            Boolean refreshWindow = (Boolean) ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.refreshWindow." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT));
            if( BooleanUtils.isTrue(refreshWindow)) {
               cachedEntry = null;
            }
        }         
        
        
        if ((cachedEntry != null) && StringUtils.isNotBlank(window.getProperty("osivia.sequence.priority"))) {
                cachedEntry = null;
        }
 
         //
        if ((cachedEntry != null) && (skipNavigationCheck == false)) {
            // Check time validity for fragment
            boolean useEntry = false;
            StateString entryNavigationalState = cachedEntry.navigationalState;
            Map<String, String[]> entryPublicNavigationalState = cachedEntry.publicNavigationalState;

            // Then check nav state equality
            if (navigationalState == null)
            {
               if (entryNavigationalState == null)
               {
                  useEntry = true;
               }
               else if (entryNavigationalState instanceof ParametersStateString)
               {
                  // We consider a parameters state string empty equivalent to a null value
                  useEntry = ((ParametersStateString)entryNavigationalState).getSize() == 0;
               }
            }
            else if (entryNavigationalState == null)
            {
               if (navigationalState instanceof ParametersStateString)
               {
                  useEntry = ((ParametersStateString)navigationalState).getSize() == 0;
               }
            }
            else
            {
               useEntry = navigationalState.equals(entryNavigationalState);
            }

            // Check public nav state equality
            if (useEntry)
            {
               if (publicNavigationalState == null)
               {
                  if (entryPublicNavigationalState == null)
                  {
                     //
                  }
                  else
                  {
                     useEntry = entryPublicNavigationalState.size() == 0;
                  }
               }
               else if (entryPublicNavigationalState == null)
               {
                  useEntry = publicNavigationalState.size() == 0;
               }
               else
               {
                  ParameterMap publicPM = ParameterMap.wrap(publicNavigationalState);
                  ParameterMap entryPM = ParameterMap.wrap(entryPublicNavigationalState);
                  useEntry = publicPM.equals(entryPM);
               }
            }

            // Then check window state equality
            useEntry &= windowState.equals(cachedEntry.windowState);

            // Then check mode equality
            useEntry &= mode.equals(cachedEntry.mode);

            // Clean if it is null
            if (!useEntry)
            {
               cachedEntry = null;
               userContext.setAttribute(scopeKey, null);
            }
         }

         boolean refresh = BooleanUtils.isTrue((Boolean)ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.refreshCaches"));
         if( refresh && sharedCache) {
             refresh = false;
         }
         
         
         
         ContentResponse fragment = cachedEntry != null ? cachedEntry.contentRef.getContent() : null;

         // If no valid fragment we must invoke
         if (fragment == null || cachedEntry.expirationTimeMillis < System.currentTimeMillis() || refresh)
         {
            // Set validation token for revalidation only we have have a fragment
            if (fragment != null)
            {
               renderInvocation.setValidationToken(cachedEntry.validationToken);
            }

            // Invoke
            PortletInvocationResponse response = super.invoke(invocation);

            // Try to cache any fragment result
            CacheControl control = null;
            if (response instanceof ContentResponse)
            {
               fragment = (ContentResponse)response;
               control = fragment.getCacheControl();
            }
            else if (response instanceof RevalidateMarkupResponse)
            {
               RevalidateMarkupResponse revalidate = (RevalidateMarkupResponse)response;
               control = revalidate.getCacheControl();
            }

            // Compute expiration time, i.e when it will expire
            long expirationTimeMillis = 0;
            String validationToken = null;
            if (control != null)
            {
               if (control.getExpirationSecs() == -1)
               {
                  expirationTimeMillis = Long.MAX_VALUE;
               }
               else if (control.getExpirationSecs() > 0)
               {
                  expirationTimeMillis = System.currentTimeMillis() + control.getExpirationSecs() * 1000;
               }
               if (control.getValidationToken() != null)
               {
                  validationToken = control.getValidationToken();
               }
               else if (cachedEntry != null)
               {
                  validationToken = cachedEntry.validationToken;
               }
            }

            // Cache if we can
            if (expirationTimeMillis > 0)
            {

                
               CacheEntry cacheEntry = new CacheEntry(
                  navigationalState,
                  publicNavigationalState,
                  windowState,
                  mode,
                  fragment,
                  expirationTimeMillis,
                  System.currentTimeMillis(),
                  validationToken
                  );
               
               
               userContext.setAttribute(scopeKey, cacheEntry);
               

               // Shared user's cache
               if ((expirationTimeMillis > 0) && (window != null) && (window.getDeclaredProperty("osivia.cacheID") != null)) {

                   String sharedID = window.getDeclaredProperty("osivia.cacheID");

                   // On controle que l'état permet une mise dans le cache global

                   if (((navigationalState == null) || (((ParametersStateString) navigationalState).getSize() == 0))
                           && ((windowState == null) || WindowState.NORMAL.equals(windowState)) && ((mode == null) || Mode.VIEW.equals(mode))) {
                       sharedID = computedCacheID(sharedID, window, publicNavigationalState);

                       CacheEntry sharedCacheEntry = new CacheEntry(null, null, null, null,  fragment, expirationTimeMillis, System.currentTimeMillis(), validationToken);
                       userContext.setAttribute("sharedcache." + sharedID, sharedCacheEntry);
                   }
               }               
            }

            //
            return response;
         }
         else
         {
            // Use the cached fragment
            return fragment;
         }
      }
      else
      {
         // Invalidate
         userContext.setAttribute(scopeKey, null);

         // Invoke
         return super.invoke(invocation);
      }
   }

   
   

   /**
    * Encapsulate cache information.
    */
   private static class CacheEntry implements  Serializable
   {

      /** The entry navigational state. */
      private final StateString navigationalState;

      /** . */
      private final WindowState windowState;

      /** . */
      private final Mode mode;

      /** . */
      private final Map<String, String[]> publicNavigationalState;

      /** The timed content. */
      private final ContentRef contentRef;

      
      /** . */
      private final long creationTs;
      
      /** . */
      private final long expirationTimeMillis;

      /** . */
      private final String validationToken;
      

    

      public CacheEntry(
         StateString navigationalState,
         Map<String, String[]> publicNavigationalState,
         WindowState windowState,
         Mode mode,
         ContentResponse content,
         long expirationTimeMillis,
         long creationTs,
         String validationToken)
      {
         if (expirationTimeMillis <= 0)
         {
            throw new IllegalArgumentException();
         }
         this.navigationalState = navigationalState;
         this.windowState = windowState;
         this.mode = mode;
         this.publicNavigationalState = publicNavigationalState;
         this.contentRef = new StrongContentRef(content);
         this.expirationTimeMillis = expirationTimeMillis;
         this.creationTs = creationTs;
         this.validationToken = validationToken;

      }

    


    public long getCreationTs() {
        return creationTs;
    }



   }
   

   
   
   public static String computedCacheID(String cacheID, Window window, Map<String, String[]> publicNavigationalState) {

       String computedPath = "";


       // Si le cache est relatif, on préfixe par l'ID de l'espace de publication
       // ce qui permet de partager au sein d'un espace

       if (!(cacheID.charAt(0) == '/')) {

           String spacePath = window.getPage().getProperty("osivia.spaceId");

           if (spacePath != null) {
               computedPath += spacePath + "/";
           }
       }

       computedPath += cacheID;

       return computedPath;

   }
   
}
