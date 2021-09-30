package org.osivia.portal.core.content;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.container.persistent.DefaultCMSPageFactory;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.springframework.beans.factory.annotation.Autowired;




public class PublicationManager implements IPublicationManager {

    private CMSService cmsService;

    private IDynamicService dynamicService;
    
    @Autowired
    private IPreviewModeService previewModeService;
    
    
    /** The locale service. */
    @Autowired
    private ILocaleService localeService;

    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }


    private IDynamicService getDynamicService() {
        if (dynamicService == null) {
            dynamicService = Locator.getService(IDynamicService.class);
        }
        return dynamicService;
    }

    private IPreviewModeService getPreviewModeService() {

        return previewModeService;       
    }
    
    private ILocaleService getLocaleService() {

        return localeService;       
    }
    
    

    protected PortalObjectId getPageTemplate(CMSContext cmsContext, Document doc, NavigationItem navigation) throws ControllerException {

        Document space;
        try {
            space = getCMSService().getCMSSession(cmsContext).getDocument(navigation.getSpaceId());


            String spaceTemplateID =  space.getId().getInternalID();

            spaceTemplateID += IPublicationManager.PAGEID_CTX;
            if( cmsContext.isPreview()) {
                spaceTemplateID += IPublicationManager.PAGEID_ITEM_SEPARATOR +IPublicationManager.PAGEID_PREVIEW + IPublicationManager.PAGEID_VALUE_SEPARATOR +"true";
            }
            spaceTemplateID += IPublicationManager.PAGEID_ITEM_SEPARATOR +IPublicationManager.PAGEID_LOCALE + IPublicationManager.PAGEID_VALUE_SEPARATOR + cmsContext.getlocale();
            
            
            String spacePath = space.getId().getRepositoryName() + ":" + "/" + spaceTemplateID + "/" + DefaultCMSPageFactory.getRootPageName();

            String templateRelativePath = "";

            Document templateDoc = null;

            // Find first page
            while (!navigation.isRoot()) {
                Document navDoc = getCMSService().getCMSSession(cmsContext).getDocument(navigation.getDocumentId());
                if (navDoc instanceof Templateable) {
                    templateDoc = navDoc;
                    break;
                }
                navigation = navigation.getParent();
            }

            if (templateDoc != null) {

                NavigationItem nav = getCMSService().getCMSSession(cmsContext).getNavigationItem(templateDoc.getId());

                while (!nav.isRoot()) {
                    templateRelativePath = "/" + nav.getDocumentId().getInternalID() + templateRelativePath;
                    nav = nav.getParent();
                }

            }

            String templatePath = spacePath + templateRelativePath;

            PortalObjectId templateId = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);
            return templateId;
        } catch (CMSException e) {
            throw new ControllerException(e);
        }
    }


    @Override
    public PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID parentID, UniversalID docId) throws ControllerException {


        PortalObjectId pageId = null;

        try {
            CMSContext cmsContext = new CMSContext(portalCtx);
            
            // check if user repository is compatible with context (locale, preview)
            
            NativeRepository userRepository = getCMSService().getUserRepository(cmsContext, docId.getRepositoryName());
            if(  getPreviewModeService().isPreviewing(portalCtx, docId) && !userRepository.supportPreview())
                getPreviewModeService().changePreviewMode(portalCtx, docId);
            if( !userRepository.getLocales().contains(getLocaleService().getLocale(portalCtx)))
                getLocaleService().setLocale(portalCtx, null);
            
            
            
            cmsContext.setPreview(getPreviewModeService().isPreviewing(portalCtx, docId));
            cmsContext.setLocale(getLocaleService().getLocale(portalCtx));
            

             
            Document doc = getCMSService().getCMSSession(cmsContext).getDocument( docId);
            
            
            
            // Force load of dirty datas associated to current space
            UniversalID spaceId = doc.getSpaceId();
            if (spaceId != null) {
                CMSController ctrl = new CMSController(portalCtx);

                CMSSession session;
                try {
                    session = Locator.getService(org.osivia.portal.api.cms.service.CMSService.class).getCMSSession(ctrl.getCMSContext());
                    SpaceCacheBean modifiedTs = session.getSpaceCacheInformations(spaceId);
                    if( modifiedTs.getLastSpaceModification() != null)
                        PageProperties.getProperties().setCheckingSpaceTS(modifiedTs.getLastSpaceModification());
                } catch (CMSException e) {
                    throw new RuntimeException(e);
                }
            }
           
            
            
            
            boolean pageDisplay = false;
            
            
            //System.out.println("*** PUBMANAGER " + docId );
            
            NavigationItem navigation;
            String pagePath = null;
            

            try {
                 navigation = getCMSService().getCMSSession(cmsContext).getNavigationItem(docId);
                 
//                 if( docId.getInternalID().contains("kFG8vy"))
//                     System.out.println("*** PUBMANAGER NAV-> " + navigation.getDocumentId() );
            } catch( CMSException e) {
                navigation = null;
            }
            
            if( navigation != null) {
                
                ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
                
                final NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);               
                

                PageNavigationalState previousPNS = null;
                PortalObjectId currentPageId = PortalObjectUtilsInternal.getPageId(controllerContext);
                if( currentPageId != null)
                    previousPNS = nsContext.getPageNavigationalState(currentPageId.toString());
                
            
                Document space = getCMSService().getCMSSession(cmsContext).getDocument( navigation.getSpaceId());
                
                String templatePath = null;
                
                // Navigation associated to the type of item (eq: folder)
                if( navigation.getCustomizedTemplateId() != null) {
                    
                     Document template = getCMSService().getCMSSession(cmsContext).getDocument( navigation.getCustomizedTemplateId());

                     templatePath = navigation.getCustomizedTemplateId().getRepositoryName()+":/"+ template.getSpaceId().getInternalID() + "/" + DefaultCMSPageFactory.getRootPageName() + "/" + navigation.getCustomizedTemplateId().getInternalID();
                 }

                
                if  (templatePath == null)
                     templatePath = getPageTemplate(cmsContext, doc, navigation).toString(PortalObjectPath.CANONICAL_FORMAT);
    
                Map<String, String> properties = new HashMap<String, String>();
    
                properties.put("osivia.contentId", docId.toString());
                properties.put("osivia.navigationId", navigation.getDocumentId().toString());
                properties.put("osivia.spaceId", navigation.getSpaceId().toString());
                properties.put("osivia.content.preview", BooleanUtils.toStringTrueFalse(doc.isPreview()));            
                properties.put("osivia.content.locale", doc.getLocale().toString()); 
    
                Map<String, String> parameters = new HashMap<String, String>();
    
                Map<Locale, String> displayNames = new HashMap<Locale, String>();
                String displayName = space.getTitle();
                if (StringUtils.isNotEmpty(displayName)) {
                    displayNames.put(Locale.FRENCH, displayName);
                }
    
                
                String pageDynamicID =  "space_" + navigation.getSpaceId().getInternalID();
                pageDynamicID += IPublicationManager.PAGEID_CTX;
                if( cmsContext.isPreview()) {
                    pageDynamicID += IPublicationManager.PAGEID_ITEM_SEPARATOR +IPublicationManager.PAGEID_PREVIEW + IPublicationManager.PAGEID_VALUE_SEPARATOR +"true";
                }
                pageDynamicID += IPublicationManager.PAGEID_ITEM_SEPARATOR +IPublicationManager.PAGEID_LOCALE + IPublicationManager.PAGEID_VALUE_SEPARATOR + cmsContext.getlocale();
                 
                
                if( parentID == null)   {
                    if( "false".equals(space.getProperties().get("osivia.connect.templated")))
                        parentID = getCMSService().getDefaultPortal(cmsContext);
                    else
                        parentID = doc.getSpaceId();
                }
                
                 pagePath = getDynamicService().startDynamicPage(portalCtx, parentID.getRepositoryName()+":/"+parentID.getInternalID(), pageDynamicID,
                        displayNames, templatePath, properties, parameters, null);
                 
                 if(  "nx".equals(doc.getId().getRepositoryName())) {
                     if( navigation.getDocumentId().equals(doc.getId()))
                         pageDisplay = true;
                 }
                 
                 
                 Page page = (Page) controllerContext.getController().getPortalObjectContainer().getObject(PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT));
                 
                 
                 // Propagation des selecteurs si les paramètres ne sont pas explicites
                 final Map<QName, String[]> pageState = new HashMap<QName, String[]>();   
               
//                 if ((previousPNS != null) && ((this.pageParams == null) || (this.pageParams.size() == 0))) {
                 if (previousPNS != null) {
                     if ("1".equals(page.getProperty("osivia.cms.propagateSelectors"))) {
                         final String[] selectors = previousPNS.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"));

                         if (selectors != null) {
                             previousPNS = nsContext.getPageNavigationalState(page.getId().toString());
                             pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"), selectors);
                             nsContext.setPageNavigationalState(page.getId().toString(), new PageNavigationalState(pageState));
                         }
                     }
                 }                
                 
                 
                 
                 
                 
                 
                 
                 
                 
                
            }   else    {
                // Empty page
                Map<Locale, String> displayNames = new HashMap<Locale, String>();
                String displayName = "content";
                if (StringUtils.isNotEmpty(displayName)) {
                    displayNames.put(Locale.FRENCH, displayName);
                }
                pagePath = getDynamicService().startDynamicPage(portalCtx, "templates:/portalA", "content",
                        displayNames, "templates:/portalA__ctx__locale_fr/root/ID_EMPTY", new HashMap<>(), new HashMap<>(), null);
              
            }
            


            if ((!(doc instanceof Templateable)) && pageDisplay == false) {

                Map<String, String> windowProperties = new HashMap<String, String>();
                Map<String, String> windowParams = new HashMap<String, String>();

                windowProperties.put(Constants.WINDOW_PROP_URI, doc.getId().toString());
                
                String instance;
                
                

                
                if( "nx".equals(doc.getId().getRepositoryName()))   {
                        instance = "toutatice-portail-cms-nuxeo-viewDocumentPortletInstance";
                    
                }   else    {
                    if( "folder".equals(doc.getType())) {
                        instance = "BrowserInstance";
                        windowProperties.put(Constants.WINDOW_PROP_CACHE_PARENT_URI, doc.getId().toString());
                    }
                    else
                        instance = "ContentInstance";
                }
                

                windowProperties.put("osivia.hideTitle", "1");
                
                if( navigation != null)
                    windowProperties.put("osivia.cms.contextualization", "1");
                    
                
                
                getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", "virtual", instance, windowProperties, windowParams);

            }


            pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);


        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return pageId;

    }


}
