package org.osivia.portal.api.apps;

import java.util.List;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * @author jsste
 */
public interface IAppsService { 
    
    /** MBean name. */
    String MBEAN_NAME = "osivia:service=ApplicationsService";
    
    /**
     * Get the applications
     * 
     * @return
     */
    public List<App> getApps( PortalControllerContext portalCtx);

    /**
     * Get application by Id
     * 
     * @return
     */
    App getApp(PortalControllerContext portalCtx, String appId); 
}
