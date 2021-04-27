package org.osivia.portal.core.content;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.container.persistent.DefaultCMSPageFactory;
import org.osivia.portal.api.cms.model.Templateable;
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
            
            
            NavigationItem navigation;
            String pagePath = null;
            

            try {
                 navigation = getCMSService().getCMSSession(cmsContext).getNavigationItem(docId);
            } catch( CMSException e) {
                navigation = null;
            }
            
            if( navigation != null) {
            
                Document space = getCMSService().getCMSSession(cmsContext).getDocument( navigation.getSpaceId());
                
                 
                String templatePath = getPageTemplate(cmsContext, doc, navigation).toString(PortalObjectPath.CANONICAL_FORMAT);
    
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
                    //parentID = getCMSService().getDefaultPortal(cmsContext);
                    parentID = doc.getSpaceId();
                }
                
                 pagePath = getDynamicService().startDynamicPage(portalCtx, parentID.getRepositoryName()+":/"+parentID.getInternalID(), pageDynamicID,
                        displayNames, templatePath, properties, parameters, null);
                
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
            


            if (!(doc instanceof Templateable)) {

                Map<String, String> windowProperties = new HashMap<String, String>();

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
                    
                
                
                getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", "virtual", instance, windowProperties);

            }


            pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);


        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return pageId;

    }


}
