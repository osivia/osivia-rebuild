package org.osivia.portal.core.cms.cache;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.ParentRequest;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.springframework.stereotype.Service;

/**
 * The Class RequestCacheManager.
 * 
 * Manage the impacts of cms request updating on portal objet
 */
@Service
public class RequestCacheManager {
    

    // Compute the listener key
    private static String CMS_LISTENER_KEY = "listener.";
    
    private CMSService cmsService;
    
    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    
    /**
     * Gets the CMS request update ts.
     *
     * @param ctx the ctx
     * @param window the window
     * @return the CMS request update ts
     */
       
    public  Long getCMSRequestUpdateTs(ControllerContext ctx, Window window) {
        
        Long updateTs = null;
        
        String   parentCacheUri = (String)  window.getDeclaredProperty(Constants.WINDOW_PROP_CACHE_PARENT_URI);
        if( StringUtils.isEmpty(parentCacheUri))    {
            return null;
        }
           
        UniversalID parentId = new UniversalID(parentCacheUri);
        
        String cmsListenerKey = CMS_LISTENER_KEY +parentId.getRepositoryName();
        
        CMSPortalCacheCacheListener listener = (CMSPortalCacheCacheListener) ctx.getAttribute(Scope.PRINCIPAL_SCOPE, cmsListenerKey);
        if (listener == null) {
            listener = new CMSPortalCacheCacheListener();
            
            PortalControllerContext portalCtx = new PortalControllerContext(ctx.getServerInvocation().getServerContext().getClientRequest());
            CMSContext cmsContext = new CMSContext(portalCtx);
            getCMSService().addListener(cmsContext, parentId.getRepositoryName(), listener);
            
            ctx.setAttribute(Scope.PRINCIPAL_SCOPE, cmsListenerKey, listener);
        }


        for (CMSPortalCacheEvent event : listener.getEvents()) {
            for (Request request : event.getEvent().getDirtyRequests()) {
                if (request instanceof ParentRequest) {
                    if (((ParentRequest) request).getParentId().equals(parentId)) {
                        if( updateTs == null)
                            updateTs = event.getTimestamp();
                        else    {
                            updateTs = Math.max(updateTs, event.getTimestamp());
                        }
                    }
                }
            }
        }
        
        
        return updateTs;
    }


}
