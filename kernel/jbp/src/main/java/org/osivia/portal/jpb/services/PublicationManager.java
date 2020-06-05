package org.osivia.portal.jpb.services;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locator.Locator;

public class PublicationManager implements IPublicationManager {

    private CMSService cmsService;

    private IDynamicService dynamicService;

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


    protected PortalObjectId getPageTemplate(CMSContext cmsContext, Document doc) throws ControllerException {

        Document space;
        try {
            space = getCMSService().getDocument(cmsContext, doc.getSpaceId());
        } catch (CMSException e) {
            throw new ControllerException(e);
        }

        String templatePath = space.getId().getRepositoryName() + ":" + "/" + space.getId().getInternalID() + "/" + DefaultCMSPageFactory.getRootPageName();
        PortalObjectId templateId = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);
        return templateId;

    }


    @Override
    public PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID docId) throws ControllerException {


        PortalObjectId pageId = null;

        try {
            CMSContext cmsContext = new CMSContext(portalCtx);

            Document doc = getCMSService().getDocument(cmsContext, docId);
            Document space = getCMSService().getDocument(cmsContext, doc.getSpaceId());


            String templatePath = getPageTemplate(cmsContext, doc).toString(PortalObjectPath.CANONICAL_FORMAT);

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");
            properties.put("osivia.contentId", docId.toString());           
            Map<String, String> parameters = new HashMap<String, String>();

            Map<Locale, String> displayNames = new HashMap<Locale, String>();
            String displayName = space.getTitle();
            if (StringUtils.isNotEmpty(displayName)) {
                displayNames.put(Locale.FRENCH, displayName);
            }

            //TODO : get current portal
            String pagePath = getDynamicService().startDynamicPage(portalCtx, "templates:/portalA", "space_" + space.getSpaceId().getInternalID(), displayNames,
                    templatePath, properties, parameters);
            
            if( ! (doc instanceof Templateable ))    {
                
                Map<String, String> windowProperties = new HashMap<String, String>();

                windowProperties.put(Constants.WINDOW_PROP_URI, doc.getId().toString());
                getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", "virtual", "ContentInstance", windowProperties);
                
            }
             

            pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);
            
            
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return pageId;

    }

}
