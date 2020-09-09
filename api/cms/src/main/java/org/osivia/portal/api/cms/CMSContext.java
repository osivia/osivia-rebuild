package org.osivia.portal.api.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;

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
    public CMSContext(PortalControllerContext portalCtx, UniversalID fromContent) {
        super();
        this.portalCtx = portalCtx;

        if (fromContent != null) {
            HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
            String editionMode = (String) mainRequest.getSession().getAttribute("editionMode."+fromContent.getRepositoryName());
            if ("preview".equals(editionMode)) {
                setPreview(true);
            }
        }

    }


    public PortalControllerContext getPortalControllerContext() {
        return portalCtx;
    }


}
