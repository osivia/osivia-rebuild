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
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.container.persistent.DefaultCMSPageFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PublicationManager implements IPublicationManager {

    private CMSService cmsService;

    private IDynamicService dynamicService;
    
    @Autowired
    private IPreviewModeService previewModeService;

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

    protected PortalObjectId getPageTemplate(CMSContext cmsContext, Document doc, NavigationItem navigation) throws ControllerException {

        Document space;
        try {
            space = getCMSService().getDocument(cmsContext, navigation.getSpaceId());


            String spaceTemplateID =  space.getId().getInternalID();
            if( cmsContext.isPreview()) {
                spaceTemplateID += "_preview";
            }
            
            String spacePath = space.getId().getRepositoryName() + ":" + "/" + spaceTemplateID + "/" + DefaultCMSPageFactory.getRootPageName();

            String templateRelativePath = "";

            Document templateDoc = null;

            // Find first page
            while (!navigation.isRoot()) {
                Document navDoc = getCMSService().getDocument(cmsContext, navigation.getDocumentId());
                if (navDoc instanceof Templateable) {
                    templateDoc = navDoc;
                    break;
                }
                navigation = navigation.getParent();
            }

            if (templateDoc != null) {

                NavigationItem nav = getCMSService().getNavigationItem(cmsContext, templateDoc.getId());

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
    public PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID docId) throws ControllerException {


        PortalObjectId pageId = null;

        try {
            CMSContext cmsContext = new CMSContext(portalCtx);
            cmsContext.setPreview(getPreviewModeService().isPreviewing(portalCtx, docId));
            

            Document doc = getCMSService().getDocument(cmsContext, docId);

            NavigationItem navigation = getCMSService().getNavigationItem(cmsContext, docId);
            Document space = getCMSService().getDocument(cmsContext, navigation.getSpaceId());
            
             
            String templatePath = getPageTemplate(cmsContext, doc, navigation).toString(PortalObjectPath.CANONICAL_FORMAT);

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");
            properties.put("osivia.contentId", docId.toString());
            properties.put("osivia.navigationId", navigation.getDocumentId().toString());
            properties.put("osivia.spaceId", navigation.getSpaceId().toString());
            properties.put("osivia.content.preview", BooleanUtils.toStringTrueFalse(doc.isPreview()));            


            Map<String, String> parameters = new HashMap<String, String>();

            Map<Locale, String> displayNames = new HashMap<Locale, String>();
            String displayName = space.getTitle();
            if (StringUtils.isNotEmpty(displayName)) {
                displayNames.put(Locale.FRENCH, displayName);
            }

            // TODO : get current portal
            
            String pageDynamicID =  "space_" + navigation.getSpaceId().getInternalID();
            if( cmsContext.isPreview()) {
                pageDynamicID += "_preview";
            }
            
             
            String pagePath = getDynamicService().startDynamicPage(portalCtx, "templates:/portalA", pageDynamicID,
                    displayNames, templatePath, properties, parameters);

            if (!(doc instanceof Templateable)) {

                Map<String, String> windowProperties = new HashMap<String, String>();

                windowProperties.put(Constants.WINDOW_PROP_URI, doc.getId().toString());
                windowProperties.put("osivia.hideTitle", "1");
                getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", "virtual", "ContentInstance", windowProperties);

            }


            pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);


        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return pageId;

    }


}
