package org.osivia.portal.api.refresh;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Cache specific strategies
 * 
 * @author jsste
 */
public interface IRefreshService {
    
    public static String REFRESH_STRATEGY_SAFRAN = "safran";
    
    public void applyRefreshStrategy( PortalControllerContext portalCtx, String strategyName)  throws PortalException;
}
