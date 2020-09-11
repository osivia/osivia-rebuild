package org.osivia.portal.api.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.windows.WindowFactory;

/**
 * The Class CMSContext.
 */
public class CMSContext {


    /** The portal ctx. */
    private final PortalControllerContext portalCtx;

    /** The preview. */
    private boolean preview;


    public boolean isPreview() {
        return preview;
    }


    public void setPreview(boolean preview) {
        this.preview = preview;
    }


    /**
     * Instantiates a new CMS context.
     *
     * @param portalCtx the portal ctx
     */
    public CMSContext(PortalControllerContext portalCtx) {
        super();
        this.portalCtx = portalCtx;
   }
    


    
    public static CMSContext createFromEditionMode(PortalControllerContext portalCtx, UniversalID fromContent) {
        CMSContext cmsContext = new CMSContext(portalCtx);

        if (fromContent != null) {
            // TODO : appeler service
            HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
            String editionMode = (String) mainRequest.getSession().getAttribute("editionMode."+fromContent.getRepositoryName());
            if ("preview".equals(editionMode)) {
                cmsContext.setPreview(true);
            }
        }
        
        return cmsContext;

    }
    

    public PortalControllerContext getPortalControllerContext() {
        return portalCtx;
    }


}
