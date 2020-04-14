package org.osivia.portal.api.cms;

import javax.servlet.http.HttpSession;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Class CMSContext.
 */
public class CMSContext {
    
    
    /** The portal ctx. */
    private final PortalControllerContext portalCtx;
    
    /**
     * Instantiates a new CMS context.
     *
     * @param portalCtx the portal ctx
     */
    public CMSContext(PortalControllerContext portalCtx) {
        super();
        this.portalCtx = portalCtx;
    }

    
    public PortalControllerContext getPortalControllerContext() {
        return portalCtx;
    }


    
}
