package org.osivia.portal.services.cms.session;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.repository.UserRepository;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.core.sessions.CMSSessionRecycle;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

public class CMSSessionImpl implements CMSSession, CMSSessionRecycle {

    private CMSServiceImpl cmsService;
    private CMSContext cmsContext;
    
    
    

    public CMSSessionImpl(CMSServiceImpl cmsService) {
        super();
        this.cmsService = cmsService;
        
    }
    
    public void setCMSContext(CMSContext cmsContext) {
        // Clone the context
        this.cmsContext = new CMSContext(cmsContext.getPortalControllerContext());
        this.cmsContext.setLocale(cmsContext.getlocale());
        this.cmsContext.setPreview(cmsContext.isPreview());
        this.cmsContext.setSuperUserMode(cmsContext.isSuperUserMode());
    }
    
    public CMSContext getCmsContext() {
        return cmsContext;
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
    public void notifyUpdate(UpdateInformations infos) throws CMSException {
        ((UserRepository) cmsService.getUserRepository(cmsContext, infos.getRepository())).notifyUpdate(infos);
    }
    
    
    @Override
    public void handleUpdate(UpdateInformations infos) throws CMSException {
        ((UserRepository) cmsService.getUserRepository(cmsContext, infos.getRepository())).handleUpdate(infos);
    }
    
    @Override
    public void reload(UniversalID id) throws CMSException {
         ((UserRepository) cmsService.getUserRepository(cmsContext, id.getRepositoryName())).reload(id.getInternalID());
    }

    @Override
    public Result executeRequest(Request request) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, request.getRepositoryName())).executeRequest(request);
    }

    @Override
    public NavigationItem getNavigationItem(UniversalID id) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, id.getRepositoryName())).getNavigationItem(id.getInternalID());
    }

    @Override
    public SpaceCacheBean getSpaceCacheInformations(UniversalID spaceId) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext, spaceId.getRepositoryName())).getSpaceCacheInformations(spaceId.getInternalID());
    }

    @Override
    public boolean isManager( String repositoryName) throws CMSException {
        return ((UserRepository) cmsService.getUserRepository(cmsContext,repositoryName)).isManager();
    }


}
