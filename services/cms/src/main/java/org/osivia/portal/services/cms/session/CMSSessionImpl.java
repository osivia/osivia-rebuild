package org.osivia.portal.services.cms.session;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

public class CMSSessionImpl implements CMSSession{

    private CMSServiceImpl cmsService;
    private CMSContext cmsContext;
    
    
    public CMSSessionImpl(CMSServiceImpl cmsService, CMSContext cmsContext) {
        super();
        this.cmsService = cmsService;
        this.cmsContext = cmsContext;

    }

    @Override
    public Document getDocument(UniversalID id) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, id.getRepositoryName())).getDocument( id.getInternalID());
    }

    @Override
    public Personnalization getPersonnalization(UniversalID id) throws CMSException {
        return  ((UserRepository) cmsService.getUserRepository(cmsContext, id.getRepositoryName())).getPersonnalization(id.getInternalID());
   }

    @Override
    public Result executeRequest(Request request) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, request.getRepositoryName())).executeRequest(request);
    }

    @Override
    public NavigationItem getNavigationItem(UniversalID id) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, id.getRepositoryName())).getNavigationItem(id.getInternalID());
    }



}
