package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class DefaultCMSPageFactory implements CMSPageFactory {

    Document doc;
    CMSService cmsService;


    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties, CMSService cmsService, Document doc) {
        new DefaultCMSPageFactory(container, containerContext, pageId, pageProperties, cmsService, doc);
    }

    public DefaultCMSPageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties, CMSService cmsService, Document doc) {
        this.cmsService = cmsService;
        this.doc = doc;
        new CMSPage(container, containerContext, pageId, pageProperties, this);

    }


    @Override
    public void createCMSWindows(CMSPage page, Map windows) {
        if( doc instanceof Space)   {
            for(ModuleRef moduleRef : ((Space) doc).getModuleRefs())    {
                page.addWindow(windows, moduleRef.getWindowName(), moduleRef.getModuleId(), moduleRef.getRegion(), moduleRef.getOrder());
            }
        }
    }

    @Override
    public List<PortalObjectId> getTemplatesID(CMSPage page) {
        List<PortalObjectId> templateIds = new ArrayList<>();
        String template = (String) doc.getProperties().get("osivia.template");
        if (template != null) {
            PortalObjectPath mainTemplatePath = new PortalObjectPath(template, PortalObjectPath.CANONICAL_FORMAT);
            templateIds.add(new PortalObjectId("", mainTemplatePath));
        }


        return templateIds;
    }


}
