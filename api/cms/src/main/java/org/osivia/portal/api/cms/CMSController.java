package org.osivia.portal.api.cms;

import org.apache.commons.lang3.BooleanUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.windows.WindowFactory;

public class CMSController {

    /** The portal ctx. */
    private final PortalControllerContext portalCtx;

    public CMSController(PortalControllerContext portalCtx) {
        super();
        this.portalCtx = portalCtx;
        
        if( portalCtx.getRequest() != null) {
            String sPreview = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.content.preview");
            preview = BooleanUtils.toBoolean(sPreview);
        }
        
    }


    /** The preview. */
    private boolean preview;

    
    public boolean isPreview() {
        return preview;
    }

    
    
    
    public CMSContext getCMSContext()   {
        CMSContext cmsContext = new CMSContext(portalCtx);
        
        cmsContext.setPreview(this.preview);
        return cmsContext;
    }
    
}
