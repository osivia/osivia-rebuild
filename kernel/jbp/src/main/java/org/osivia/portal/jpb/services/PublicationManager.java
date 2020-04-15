package org.osivia.portal.jpb.services;



import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.services.Locator;

public class PublicationManager implements IPublicationManager {
    
    private CMSService cmsService;
    
    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }
    

    @Override
    public PortalObjectId getPageTemplate( CMSContext cmsContext, Document doc) throws ControllerException {

        Document space;
        try {
            space = getCMSService().getDocument(cmsContext, doc.getSpaceId());
        } catch (CMSException e) {
           throw new ControllerException(e);
        }
        // String templatePath = (String) space.getProperties().get("osivia.template");
        String templatePath =  space.getId().getRepositoryName() + ":" + "/" + space.getId().getInternalID() +"/" + DefaultCMSPageFactory.getRootPageName();
        PortalObjectId templateId = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);
        return templateId;
        
    }

}
