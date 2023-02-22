package org.osivia.portal.core.container.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.core.ajax.AjaxResponseHandler;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer.ContainerContext;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;


public class DefaultCMSPageFactory implements CMSPageFactory {

    NavigationItem navItem;
    CMSService cmsService;
    CMSContext cmsContext;
    Document doc;

    
    private static final Logger log = Logger.getLogger(DefaultCMSPageFactory.class);

    public static String getRootPageName() {
        return "root";
    }


    public static void createCMSPage(StaticPortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, NavigationItem navItem) throws CMSException {
        new DefaultCMSPageFactory(container, containerContext, parent, cmsService, cmsContext, navItem);
    }

    public DefaultCMSPageFactory(StaticPortalObjectContainer container, ContainerContext containerContext, PortalObject parent, CMSService cmsService,
            CMSContext cmsContext, NavigationItem navItem) throws CMSException {
        this.cmsService = cmsService;
        this.cmsContext = cmsContext;
        this.navItem = navItem;
        
        log.info("create CMSPage " +navItem.getDocumentId() + " " +cmsContext.isPreview());
        
        doc = cmsService.getCMSSession(cmsContext).getDocument( navItem.getDocumentId());

        String pageName = getRootPageName();
        if (!navItem.isRoot())
            pageName = doc.getId().getInternalID();

        // Create default page
        String path = parent.getId().getPath().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + pageName;
        Map<String, String> pageProperties = new HashMap<>();
//        pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
//        pageProperties.put(ThemeConstants.PORTAL_PROP_THEME, "generic");
        pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");
        
        // Add doc properties
        Map<String, Object> docProperties = doc.getProperties();
        for( String key: docProperties.keySet())    {
            if( docProperties.get(key) instanceof String)
                pageProperties.put(key, (String) docProperties.get(key));
        }
        

        PortalObjectId pageId = new PortalObjectId(parent.getId().getNamespace(), new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT));

        List<String> inheritedRegions = new ArrayList<>();
        if( doc instanceof Page)
            inheritedRegions = ((Page) doc).getInheritedRegions();
        
        CMSPage cmsPage = new CMSPage(container, containerContext, pageId, pageProperties, inheritedRegions, this);
 
        
        if( doc instanceof RepositoryDocument)  {
            cmsPage.setDeclaredProperty("osivia.cms.page.timestamp", new Long(((RepositoryDocument) doc).getTimestamp()).toString());
        }
        
        if (StringUtils.isEmpty(cmsPage.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME))) {

            // If no parent theme, reinit the current theme
            String parentTheme = parent.getProperty(ThemeConstants.PORTAL_PROP_THEME);
            if (parentTheme == null)
                cmsPage.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, "generic");
            
        }
            
/*
        for (NavigationItem child : navItem.getChildren()) {
            org.jboss.portal.core.model.portal.Page page = (org.jboss.portal.core.model.portal.Page) container.getObject(pageId);
            DefaultCMSPageFactory.createCMSPage(container, containerContext, page, cmsService, cmsContext, child);

        }
*/
    }


    @Override
    public void createCMSWindows(CMSPage page, Map windows) {
        int order = 0;
        if (doc instanceof Space) {
            for (ModuleRef moduleRef : ((Space) doc).getModuleRefs()) {
                page.addWindow(windows, moduleRef, order++);
            }
        }

        if (doc instanceof Page) {
            for (ModuleRef moduleRef : ((Page) doc).getModuleRefs()) {
                page.addWindow(windows, moduleRef, order++);
            }
        }
    }

    @Override
    public List<PortalObjectId> getDeclaredTemplatesID(CMSPage page) throws CMSException {

        List<PortalObjectId> templateIds = new ArrayList<>();


        UniversalID templateCMSId;
        if (doc instanceof Templateable) {
            templateCMSId = ((Templateable) doc).getTemplateId();


            if (templateCMSId != null) {

                Document templateDoc = cmsService.getCMSSession(cmsContext).getDocument(templateCMSId);
                String templatePath = "";
                
                
                NavigationItem templateNav = cmsService.getCMSSession(cmsContext).getNavigationItem(templateDoc.getId());
                

                while(! templateNav.isRoot())  {
                    
                    templateDoc = cmsService.getCMSSession(cmsContext).getDocument( templateNav.getDocumentId());

                    templatePath = "/" + templateDoc.getId().getInternalID() + templatePath;
                    templateNav = templateNav.getParent();
                }

                // Add space
                templatePath = "/" + templateNav.getSpaceId().getInternalID() + "/" + getRootPageName() + templatePath;


                PortalObjectPath mainTemplatePath = new PortalObjectPath(templatePath, PortalObjectPath.CANONICAL_FORMAT);
                // add root level

                templateIds.add(new PortalObjectId(templateDoc.getId().getRepositoryName(), mainTemplatePath));
            }   
        }

        return templateIds;
    }


}
