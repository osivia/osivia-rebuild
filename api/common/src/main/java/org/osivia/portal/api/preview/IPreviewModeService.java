package org.osivia.portal.api.preview;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Interface IPreviewModeService.
 */
public interface IPreviewModeService {
    
    /**
     * Checks if is previewing.
     *
     * @param portalCtx the portal ctx
     * @param content the content
     * @return true, if is previewing
     * @throws PortalException the portal exception
     */
    public boolean isPreviewing( PortalControllerContext portalCtx, UniversalID content) throws PortalException;
    
    /**
     * Change preview mode.
     *
     * @param portalCtx the portal ctx
     * @param content the content
     * @return true, if successful
     * @throws PortalException the portal exception
     */
    public boolean changePreviewMode( PortalControllerContext portalCtx, UniversalID content) throws PortalException;

    /**
     * Sets the preview.
     *
     * @param portalCtx the portal ctx
     * @param content the content
     * @param preview the preview
     * @throws PortalException the portal exception
     */
    void setPreview(PortalControllerContext portalCtx, UniversalID content, boolean preview) throws PortalException;

    void switchPageEditionMode(PortalControllerContext portalCtx) throws PortalException;

    boolean isEditionMode(PortalControllerContext portalCtx) throws PortalException;

}
