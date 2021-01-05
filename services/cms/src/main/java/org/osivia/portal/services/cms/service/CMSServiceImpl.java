package org.osivia.portal.services.cms.service;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.repository.test.InMemoryFactory;
import org.osivia.portal.services.cms.repository.test.InMemoryUserRepository;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService {

    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
        repositoryFactory = new InMemoryFactory();

    }

    InMemoryFactory repositoryFactory;

    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException {
       UserRepository userRepository =  (UserRepository) getUserRepository(cmsContext, id.getRepositoryName());
        Document document = userRepository.getDocument(id.getInternalID());
        return document;
    }


    @Override
    public NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id) throws CMSException {
        UserRepository userRepository =  (UserRepository) getUserRepository(cmsContext, id.getRepositoryName());
        return userRepository.getNavigationItem(id.getInternalID());
    }


    @Override
    public void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
          repositoryFactory.addListener(cmsContext, repositoryName, listener);
    }


    @Override
    public NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        return repositoryFactory.getUserRepository(cmsContext, repositoryName);
    }


    @Override
    public Result executeRequest(CMSContext cmsContext, Request request) throws CMSException {
        UserRepository userRepository =  (UserRepository) getUserRepository(cmsContext, request.getRepositoryName());
        return userRepository.executeRequest(request);
    }


}
