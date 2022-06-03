package org.osivia.portal.core.cms.edition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.HashedMap;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.theming.AbstractRegionBean;
import org.osivia.portal.api.theming.PortletsRegionBean;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.container.dynamic.DynamicTemplatePage;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.theming.RenderedRegions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

@Service("osivia:service=CMSEditionService")
public class CMSEditionService {

    @Autowired
    IPortalUrlFactory portalUrlFActory;

    @Autowired
    IPreviewModeService previewModeService;
    @Autowired
    ILocaleService localeService;

    private CMSService cmsService;


    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    public void prepareEdition(ControllerContext controllerContext, Page page, RenderedRegions renderedRegions, List<String> layoutRegions) {
        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        PortalControllerContext portalControllerContext = new PortalControllerContext(request);

        PageProperties properties = PageProperties.getProperties();

        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
        properties.getPagePropertiesMap().put(InternalConstants.LOCALE_PROPERTY, locale.toString());

        try {

            String cmsEditionUrl = (String) request.getAttribute("osivia.cms.edition.url");
            if (cmsEditionUrl != null) {
                DynamicTemplatePage dynaPage = (DynamicTemplatePage) page;

                
                List<String> regions= new ArrayList<>();
                
                
                List<String> cmsRegions;
                String sCmsRegions =  page.getProperties().get("cms.regions");
                 if( sCmsRegions != null)
                    cmsRegions =   Arrays.asList(sCmsRegions.split(","));   
                else
                    cmsRegions= new ArrayList<>();

                

                regions = new ArrayList<>();
                    
                for (String region:layoutRegions) {
                        // Exclude non-portlet region
                        boolean add = true;
                        for (AbstractRegionBean nonPortletRegion : renderedRegions.getRenderedRegions()) {
                            if( region.equals(nonPortletRegion.getName())) {
                                add = false;
                            }
                        }    
                        if( add)    {
                            if( (dynaPage.getCmsTemplateID() != null && cmsRegions.contains(region))
                               ||
                               (dynaPage.getCmsTemplateID() == null && !cmsRegions.contains(region)))
                            regions.add(region);
                        }
                }
                    
               


                properties.getPagePropertiesMap().put("cms.regions", String.join(",", regions));
                properties.getPagePropertiesMap().put("osivia.cms.edition.url", cmsEditionUrl);


                for (String region : regions) {
                    // Add portlet URL

                    String addPortletUrl;
                    Map<String, String> props = new HashedMap();
                    props.put("osivia.navigationId", page.getDeclaredProperty("osivia.navigationId"));
                    props.put("osivia.content.preview", page.getDeclaredProperty("osivia.content.preview"));
                    props.put("osivia.content.locale", page.getDeclaredProperty("osivia.content.locale"));
                    props.put("osivia.cms.edition.region", region);

                    addPortletUrl = this.portalUrlFActory.getStartPortletUrl(portalControllerContext, "EditionAddPortletInstance", props, PortalUrlType.MODAL);
                    PageProperties.getProperties().getPagePropertiesMap().put("osivia.cms.edition.addPortletUrl." + region, addPortletUrl);
                }

            }

        } catch (PortalException e) {
            throw new RuntimeException(e);
        }


    }


    /**
     * Prepare DiwWindowRenderer datas
     * 
     * @param controllerContext
     * @param window
     */
    public void prepareEdition(ControllerContext controllerContext, Window window) {
        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        PortalControllerContext portalControllerContext = new PortalControllerContext(request);

        Page page = window.getPage();


        String cmsEditionUrl = (String) request.getAttribute("osivia.cms.edition.url");

        PageProperties properties = PageProperties.getProperties();
        String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);

        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
        properties.setWindowProperty(windowId, InternalConstants.LOCALE_PROPERTY, locale.toString());


        try {
            if (cmsEditionUrl != null && !window.getName().equals("edition")) {
                
                List<String> cmsRegions;
                String sCmsRegions =  page.getProperties().get("cms.regions");
                 if( sCmsRegions != null)
                    cmsRegions =   Arrays.asList(sCmsRegions.split(","));   
                else
                    cmsRegions= new ArrayList<>();


                
                DynamicTemplatePage dynaPage = (DynamicTemplatePage) window.getPage();
                
                
                String region = window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);


                    if( (dynaPage.getCmsTemplateID() != null && cmsRegions.contains(region))
                            ||
                        (dynaPage.getCmsTemplateID() == null && !cmsRegions.contains(region))) {                    
                    properties.setWindowProperty(windowId, InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY, String.valueOf(true));
                    properties.setWindowProperty(windowId, "osivia.cms.edition.url", (String) request.getAttribute("osivia.cms.edition.url"));
                    properties.setWindowProperty(windowId, "osivia.cms.edition.windowName", window.getName());


                    String addPortletUrl;
                    Map<String, String> props = new HashMap<String, String>();
                    props.put("osivia.navigationId", page.getDeclaredProperty("osivia.navigationId"));
                    props.put("osivia.content.preview", page.getDeclaredProperty("osivia.content.preview"));
                    props.put("osivia.content.locale", page.getDeclaredProperty("osivia.content.locale"));
                    props.put("osivia.cms.edition.targetWindow", window.getName());

                    addPortletUrl = this.portalUrlFActory.getStartPortletUrl(portalControllerContext, "EditionAddPortletInstance", props, PortalUrlType.MODAL);
                    properties.setWindowProperty(windowId, "osivia.cms.edition.addPortletUrl", addPortletUrl);

                    String deletePortletUrl;
                    deletePortletUrl = this.portalUrlFActory.getStartPortletUrl(portalControllerContext, "EditionDeletePortletInstance", props, PortalUrlType.MODAL);
                    properties.setWindowProperty(windowId, "osivia.cms.edition.deletePortletUrl", deletePortletUrl);

                    String editPortletUrl;
                    editPortletUrl = this.portalUrlFActory.getStartPortletUrl(portalControllerContext, "EditionModifyPortletInstance", props, PortalUrlType.MODAL);
                    properties.setWindowProperty(windowId, "osivia.cms.edition.modityPortletUrl", editPortletUrl);
                }

            }
        } catch (PortalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the original document with windows modifications
     * 
     * @param controllerContext
     * @param window
     * @param localProperties
     */
    public void updateModule(ControllerContext controllerContext, Window window, Map<String, String> localProperties) {

        try {
            // HTTP Request
            HttpServletRequest httpRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
            PortalControllerContext portalControllerContext = new PortalControllerContext(httpRequest);

            String contentId = window.getPage().getProperty("osivia.contentId");
            UniversalID id = new UniversalID(contentId);

            CMSContext cmsContext = new CMSContext(portalControllerContext);

            cmsContext.setPreview(previewModeService.isPreviewing(portalControllerContext, id));
            cmsContext.setLocale(localeService.getLocale(portalControllerContext));


            Document document = getCMSService().getCMSSession(cmsContext).getDocument(id);

            if (document instanceof ModulesContainer) {


                // Search src module
                ModuleRef srcModule = null;
                List<ModuleRef> modules = ((ModulesContainer) document).getModuleRefs();
                for (ModuleRef module : modules) {
                    if (module.getWindowName().equals(window.getName())) {
                        srcModule = module;
                        break;
                    }
                }

                if (srcModule != null) {

                    for (String name : localProperties.keySet()) {
                        String value = localProperties.get(name);
                        if (value != null) {
                            srcModule.getProperties().put(name, value);
                        } else
                            srcModule.getProperties().remove(name);
                    }

                    /* Update document */

                    TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                    if (repository instanceof TestRepository) {
                        ((TestRepository) repository).updateDocument(id.getInternalID(), (RepositoryDocument) document);
                    }
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
