package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class DefaultCMSPageFactory implements CMSPageFactory {

    Document doc;
    CMSService cmsService;
    
    public static String getRootPageName() {
        return "root";
    }


    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, Document doc) throws CMSException {
       new DefaultCMSPageFactory(container, containerContext, parent, cmsService, cmsContext, doc);
    }

    public DefaultCMSPageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, Document doc) throws CMSException {
        this.cmsService = cmsService;
        this.doc = doc;

        String pageName = getRootPageName();
        if( doc instanceof Page)
            pageName = doc.getName();
        
        // Create default page
        String path = parent.getId().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + pageName;
        Map<String, String> pageProperties = new HashMap<>();
        pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
        pageProperties.put(ThemeConstants.PORTAL_PROP_THEME, "generic");
        pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");

        PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT));

        new CMSPage(container, containerContext, pageId, pageProperties, this);
        
        for( String childId : doc.getChildrenId()) {
            Document child = cmsService.getDocument(cmsContext, childId);
            org.jboss.portal.core.model.portal.Page page = (org.jboss.portal.core.model.portal.Page) container.getObject(pageId);
            DefaultCMSPageFactory.createCMSPage(container, containerContext, page,cmsService, cmsContext, child);

        }

    }


    @Override
    public void createCMSWindows(CMSPage page, Map windows) {
        if (doc instanceof Space) {
            for (ModuleRef moduleRef : ((Space) doc).getModuleRefs()) {
                page.addWindow(windows, moduleRef.getWindowName(), moduleRef.getModuleId(), moduleRef.getRegion(), moduleRef.getOrder());
            }
        }
        
        if (doc instanceof Page) {
            for (ModuleRef moduleRef : ((Page) doc).getModuleRefs()) {
                page.addWindow(windows, moduleRef.getWindowName(), moduleRef.getModuleId(), moduleRef.getRegion(), moduleRef.getOrder());
            }
        }
    }

    @Override
    public List<PortalObjectId> getTemplatesID(CMSPage page) {
        List<PortalObjectId> templateIds = new ArrayList<>();
        String template = (String) doc.getProperties().get("osivia.template");
        if (template != null) {
            // Insert root level
            int indexPage = template.indexOf('/', 1);
            if( indexPage != -1) {
                template = template.substring(0,indexPage) + "/" + getRootPageName() + template.substring(indexPage);
            }
            
            
            PortalObjectPath mainTemplatePath = new PortalObjectPath(template, PortalObjectPath.CANONICAL_FORMAT);
            // add root level
            
            
            templateIds.add(new PortalObjectId("", mainTemplatePath));
        }


        return templateIds;
    }


}
