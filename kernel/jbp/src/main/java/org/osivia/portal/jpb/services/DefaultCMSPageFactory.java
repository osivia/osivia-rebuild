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
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class DefaultCMSPageFactory implements CMSPageFactory {

    NavigationItem navItem;
    CMSService cmsService;
    CMSContext cmsContext;
    Document doc;


    public static String getRootPageName() {
        return "root";
    }


    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, NavigationItem navItem) throws CMSException {
        new DefaultCMSPageFactory(container, containerContext, parent, cmsService, cmsContext, navItem);
    }

    public DefaultCMSPageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, NavigationItem navItem) throws CMSException {
        this.cmsService = cmsService;
        this.cmsContext = cmsContext;
        this.navItem = navItem;
        
        doc = cmsService.getDocument(cmsContext, navItem.getDocumentId());

        String pageName = getRootPageName();
        if (!navItem.isRoot())
            pageName = doc.getId().getInternalID();

        // Create default page
        String path = parent.getId().getPath().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + pageName;
        Map<String, String> pageProperties = new HashMap<>();
        pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
        pageProperties.put(ThemeConstants.PORTAL_PROP_THEME, "generic");
        pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");

        PortalObjectId pageId = new PortalObjectId(parent.getId().getNamespace(), new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT));

        new CMSPage(container, containerContext, pageId, pageProperties, this);

        for (NavigationItem child : navItem.getChildren()) {
            org.jboss.portal.core.model.portal.Page page = (org.jboss.portal.core.model.portal.Page) container.getObject(pageId);
            DefaultCMSPageFactory.createCMSPage(container, containerContext, page, cmsService, cmsContext, child);

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


        UniversalID templateCMSId;
        if (doc instanceof Templateable) {
            templateCMSId = ((Templateable) doc).getTemplateId();


            if (templateCMSId != null) {

                Document templateDoc = cmsService.getDocument(cmsContext, templateCMSId);
                String templatePath = "/" + templateDoc.getId().getInternalID();
                
                NavigationItem templateNav = cmsService.getNavigationItem(cmsContext, templateDoc.getId(), CMSService.PRIMARY_NAVIGATION_TREE);
                

                while (! templateNav.getParent().isRoot()) {
                    templateNav = templateNav.getParent();
                    templateDoc = cmsService.getDocument(cmsContext, templateNav.getDocumentId());

                    templatePath = "/" + templateDoc.getId().getInternalID() + templatePath;
                }

                // Add space
                templatePath = "/" + templateDoc.getSpaceId().getInternalID() + "/" + getRootPageName() + templatePath;


                PortalObjectPath mainTemplatePath = new PortalObjectPath(templatePath, PortalObjectPath.CANONICAL_FORMAT);
                // add root level

                templateIds.add(new PortalObjectId(templateDoc.getId().getRepositoryName(), mainTemplatePath));
            }
        }

        return templateIds;
    }


}
