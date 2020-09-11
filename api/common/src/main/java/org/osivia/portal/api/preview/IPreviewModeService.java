package org.osivia.portal.api.preview;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;

public interface IPreviewModeService {
    
    public boolean isPreviewing( PortalControllerContext portalCtx, UniversalID content) throws PortalException;
    public boolean changePreviewMode( PortalControllerContext portalCtx, UniversalID content) throws PortalException;

}
