/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.osivia.portal.core.content;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;


/**
 * '/content' url entry point
 */
public class ViewContentCommand extends ControllerCommand {

    /** . */
    private static final CommandInfo info = new ActionCommandInfo(false);

    private CMSService cmsService;


    private IPublicationManager publicationManager;

    /** . */
    private String contentId;
    private Locale locale;
    


    private boolean preview;

    
    
    public ViewContentCommand(String contentId, Locale locale, boolean preview) {
        this.contentId = contentId;
        this.locale = locale;
        this.preview = preview;
    }
    
    public Locale getLocale() {
        return locale;
    }

    
    public boolean isPreview() {
        return preview;
    }

    private IPreviewModeService getPreviewModeService() {

        return Locator.getService(IPreviewModeService.class);      
    }
    
    private ILocaleService getLocaleService() {

        return Locator.getService(ILocaleService.class);     
    }



    private IPublicationManager getPublicationManager() {
        if (publicationManager == null) {
            publicationManager = Locator.getService(IPublicationManager.class);
        }
        return publicationManager;
    }


    public CommandInfo getInfo() {
        return info;
    }

    public String getContentId() {
        return contentId;
    }

    public ControllerResponse execute() throws ControllerException {

        ControllerContext controllerContext = this.getControllerContext();

        try {

            
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
            
            UniversalID contentId = new UniversalID(getContentId().replaceAll("/", ":"));
            
            getLocaleService().setLocale(portalCtx, locale);
            getPreviewModeService().setPreview(portalCtx, contentId, preview);


            PortalObjectId pageId = getPublicationManager().getPageId(portalCtx, null, contentId);

            return new UpdatePageResponse(pageId);


        } catch (Exception e) {
            // TODO : error management
            e.printStackTrace();

            throw new ControllerException(e);
        }
    }
}
