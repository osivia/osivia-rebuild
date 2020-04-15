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
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class DefaultCMSPageFactory implements CMSPageFactory {

    Document doc;
    CMSService cmsService;
    CMSContext cmsContext;
    
    
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
        this.cmsContext = cmsContext;       
        this.doc = doc;

        String pageName = getRootPageName();
        if( doc instanceof Page)
            pageName = doc.getId().getInternalID();
        
        // Create default page
        String path = parent.getId().getPath().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + pageName;
        Map<String, String> pageProperties = new HashMap<>();
        pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
        pageProperties.put(ThemeConstants.PORTAL_PROP_THEME, "generic");
        pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");

        PortalObjectId pageId = new PortalObjectId(parent.getId().getNamespace(), new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT));

        new CMSPage(container, containerContext, pageId, pageProperties, this);
        
        for( Document child : doc.getChildren()) {
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
    public List<PortalObjectId> getTemplatesID(CMSPage page) throws CMSException {
        
        List<PortalObjectId> templateIds = new ArrayList<>();
        String templateCMSId = (String) doc.getProperties().get("osivia.template");
        if (templateCMSId != null) {
            
            Document templateDoc = cmsService.getDocument(cmsContext, new UniversalID(templateCMSId));
            
            
            String templatePath = "/" + templateDoc.getId().getInternalID();
            while (templateDoc.getParent() instanceof Page) {
                templateDoc = templateDoc.getParent();
                templatePath = "/" + templateDoc.getId() + templatePath;
            }
            
            // Add space
            templatePath = "/" + templateDoc.getSpaceId().getInternalID() + "/" + getRootPageName() + templatePath;

            
            PortalObjectPath mainTemplatePath = new PortalObjectPath(templatePath, PortalObjectPath.CANONICAL_FORMAT);
            // add root level
              
            templateIds.add(new PortalObjectId(templateDoc.getId().getRepositoryName(), mainTemplatePath));
        }


        return templateIds;
    }


}
