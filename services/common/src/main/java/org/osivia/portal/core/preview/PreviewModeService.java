package org.osivia.portal.core.preview;

import javax.servlet.http.HttpServletRequest;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.springframework.stereotype.Service;

@Service( "osivia:service=PreviewModeService")
public class PreviewModeService implements IPreviewModeService {

    @Override
    public boolean isPreviewing(PortalControllerContext portalCtx, UniversalID content) throws PortalException {
        boolean previewing;
        HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
        String editionMode = (String) mainRequest.getSession().getAttribute("editionMode."+content.getRepositoryName());
        if ("preview".equals(editionMode)) {
            previewing = true;
        } else
            previewing = false;
        return previewing;
    }

    @Override
    public boolean changePreviewMode(PortalControllerContext portalCtx, UniversalID content) throws PortalException {
        HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
        
        String editionModeKey = "editionMode."+content.getRepositoryName();
        String editionMode = (String) mainRequest.getSession().getAttribute(editionModeKey);
        if ("preview".equals(editionMode)) {
            mainRequest.getSession().removeAttribute(editionModeKey);
        } else
            mainRequest.getSession().setAttribute(editionModeKey, "preview");

        return false;
    }
    
    
    @Override
    public void setPreview(PortalControllerContext portalCtx, UniversalID content,boolean preview) throws PortalException {
        HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
        String editionModeKey = "editionMode."+content.getRepositoryName();
        if (!preview) {
            mainRequest.getSession().removeAttribute(editionModeKey);
         } else
            mainRequest.getSession().setAttribute(editionModeKey, "preview");
    }
 

}
