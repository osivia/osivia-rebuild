package org.osivia.portal.core.cms.edition;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.dynamic.RestorablePageUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;

public class CMSEditionChangeModeCommand extends ControllerCommand{
    
    PortalObjectId pageId = null;

    private static final CommandInfo INFO = new ActionCommandInfo(false);
    
    private static IPreviewModeService previewModeService = null;
    
    @Override
    public CommandInfo getInfo() {

        return INFO;
    }
    
    public void setPageId(PortalObjectId pageId) {
        this.pageId = pageId;
    }
    
    public PortalObjectId getPageId() {
        return this.pageId;
    }

    public static IPreviewModeService getPreviewModeService() throws Exception {

        if( previewModeService == null) {
            previewModeService = Locator.getService(IPreviewModeService.class);
        }

        return previewModeService;

    }

    @Override
    public ControllerResponse execute() throws ControllerException {
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext().getServerInvocation().getServerContext().getClientRequest());        
        try {
            getPreviewModeService().switchPageEditionMode(portalControllerContext);


            return new UpdatePageResponse(pageId);
            
        } catch (Exception e) {
           throw new ControllerException(e);
        }
    }

}
