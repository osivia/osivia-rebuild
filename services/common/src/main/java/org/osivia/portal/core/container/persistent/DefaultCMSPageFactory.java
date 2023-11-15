package org.osivia.portal.core.container.persistent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.osivia.portal.core.ajax.AjaxResponseHandler;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer.ContainerContext;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;


public class DefaultCMSPageFactory implements CMSPageFactory {

    NavigationItem navItem;
    CMSService cmsService;
    CMSContext cmsContext;
    Document doc;
    StaticPortalObjectContainer container;
    
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
        this.container = container;
        this.cmsService = cmsService;
        this.cmsContext = cmsContext;
        this.navItem = navItem;
        
        log.debug("create CMSPage " +navItem.getDocumentId() + " " +cmsContext.isPreview());
        
        doc = cmsService.getCMSSession(cmsContext).getDocument( navItem.getDocumentId());

        String pageName = getRootPageName();
        if (!navItem.isRoot())
            pageName = doc.getId().getInternalID();

        // Create default page
        String path = parent.getId().getPath().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + pageName;

        Map<String, String> pageProperties = new HashMap<>();
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
            if (parentTheme == null)    {
                log.error("can't fin parent theme for page " +navItem.getDocumentId() + " " +cmsContext.isPreview());
                cmsPage.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, "generic");
            }
            
        }
           
    }

    void checkRolesAndAddWindow(CMSPage page, Map windows, ModuleRef moduleRef, int order ) {

        HttpServletRequest request = cmsContext.getPortalControllerContext().getHttpServletRequest();
        
        boolean rolesChecked = false;
        
        // Check if window is restricted to a role
        
        List<String> roles = new ArrayList<String>();
        String sRoles = StringUtils.defaultString(moduleRef.getProperties().get("osivia.roles"));
        if( StringUtils.isNotEmpty(sRoles)) {
            roles = Arrays.asList(sRoles.split(","));
            for (String role : roles) {
                if (request.isUserInRole(role)) {
                    rolesChecked = true;
                }
            }
        }   else    {
            rolesChecked = true;
        }
        
        boolean onlyVisibleInEditionMode;
        
        if( rolesChecked == true)    {
            onlyVisibleInEditionMode = false;
        }   else    {
            PortalControllerContext portalControllerContext = new PortalControllerContext(request);
            boolean isAdministrator = portalControllerContext.getHttpServletRequest().isUserInRole("Administrators");
            if ((PortalObjectUtils.isPageRepositoryManager(portalControllerContext) || isAdministrator)) {
                // Only show in preview mode
                onlyVisibleInEditionMode = true;
             }  else {
                onlyVisibleInEditionMode = false;
            }
        }  
        
        if( rolesChecked || onlyVisibleInEditionMode)
            page.addWindow(windows, moduleRef, order++, onlyVisibleInEditionMode);
    }

    
    
    
    
    @Override
    public void createCMSWindows(CMSPage page, Map windows) {
        
        List<ModuleRef> modulesList;
        
        if (doc instanceof Space) {
            modulesList = ((Space) doc).getModuleRefs() ;
        } else if (doc instanceof Page) {
            modulesList = ((Page) doc).getModuleRefs() ;
        }   else
            modulesList = new ArrayList<>();
        
        addModules(modulesList, page, windows);
    }


    private int addModules(List<ModuleRef> modulesList, CMSPage page, Map windows) {
        int order = 0;
        
        // template modules
        for (ModuleRef moduleRef : modulesList) {
            checkRolesAndAddWindow( page, windows, moduleRef, order++);
        }
       
        
        // customized additional modules
        HttpServletRequest request = cmsContext.getPortalControllerContext().getHttpServletRequest();
        
        UniversalID portalId = PortalObjectUtils.getHostPortalID(request);
        List<ModuleRef> additionalModules = container.customizeModules(portalId.getRepositoryName());
        
        if( additionalModules != null)  {
            for (ModuleRef moduleRef : additionalModules) {
                               
                Map<String,String> properties = new HashMap<>(moduleRef.getProperties());
                properties.put( "osivia.customizedWindow" , "1");
             
                ModuleRef adaptedModule = new ModuleRef(moduleRef.getWindowName(),moduleRef.getRegion(), moduleRef.getModuleId(), properties);
                checkRolesAndAddWindow( page, windows, adaptedModule, order++);
            }
        }
        
        return order;
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
