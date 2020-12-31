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

import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.spi.UserContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.ParentRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.cache.CMSPortalCacheCacheListener;
import org.osivia.portal.core.cms.cache.CMSPortalCacheEvent;
import org.osivia.portal.core.cms.cache.RequestCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.WindowState;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.Mode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Cache markup on the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ConsumerCacheInterceptor extends PortletInvokerInterceptor
{

    @Autowired
    private RequestCacheManager requestCacheMgr;
    
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      // Compute the cache key
      String scopeKey = "cached_markup." + invocation.getWindowContext().getId();
      


      // We use the principal scope to avoid security issues like a user loggedout seeing a cached entry
      // by a previous logged in user
      UserContext userContext = invocation.getUserContext();
      
      ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

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
         CacheEntry cachedEntry = (CacheEntry) userContext.getAttribute(scopeKey);
         
         
         Window window = null;

         if (invocation.getWindowContext() instanceof WindowContextImpl) {
             String windowId = invocation.getWindowContext().getId();
             PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
             window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
         }
         
         
        // Window has been modified
        if (cachedEntry != null && window != null) {
            if (cachedEntry.getCreationTs() < window.getUpdateTs())
                cachedEntry = null;
        }


        // CMS cache has been modified
        if (cachedEntry != null && window != null) {

            Long updateTs = requestCacheMgr.getCMSRequestUpdateTs(ctx, window);

            if (updateTs != null) {
                if (cachedEntry.creationTs < updateTs) {
                    cachedEntry = null;
                }
            }
        }         
      
         
         

         //
         if (cachedEntry != null)
         {
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

         ContentResponse fragment = cachedEntry != null ? cachedEntry.contentRef.getContent() : null;

         // If no valid fragment we must invoke
         if (fragment == null || cachedEntry.expirationTimeMillis < System.currentTimeMillis())
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
   private static class CacheEntry implements RepositoryListener, Serializable
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


    @Override
    public void contentModified(CMSEvent event) {
        // TODO Auto-generated method stub
        
    }
   }
}
