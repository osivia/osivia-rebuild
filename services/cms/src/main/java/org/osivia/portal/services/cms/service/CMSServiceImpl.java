package org.osivia.portal.services.cms.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.SuperUserContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.user.InMemoryUserRepository;
import org.osivia.portal.services.cms.repository.user.SiteRepository;
import org.osivia.portal.services.cms.repository.user.TemplatesRepository;
import org.osivia.portal.services.cms.repository.user.UserWorkspacesRepository;
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
        superUserRepositories = new ConcurrentHashMap<SharedRepositoryKey, InMemoryUserRepository>();


    }

    /** The super user repositories. */
    private Map<SharedRepositoryKey, InMemoryUserRepository> superUserRepositories;
 

    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException {

        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id.getRepositoryName());
        NuxeoMockDocumentImpl document = userRepository.getDocument(id.getInternalID());
        
        return document;

    }

    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the user repository
     */
    public InMemoryUserRepository getOrCreateUserRepository(CMSContext cmsContext, String repositoryName, InMemoryUserRepository publishRepository) {

        InMemoryUserRepository userRepository;

        SharedRepositoryKey repositoryKey = new SharedRepositoryKey(repositoryName, cmsContext.isPreview());

        if (cmsContext instanceof SuperUserContext) {
            
            userRepository = (InMemoryUserRepository) superUserRepositories.get(repositoryKey);
            if (userRepository == null) {
                userRepository = createRepository(repositoryKey, publishRepository);
                superUserRepositories.put(repositoryKey, userRepository);
            }

        } else {
            ControllerContext ctx = ControllerContextAdapter.getControllerContext(cmsContext.getPortalControllerContext());

            HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession(true);

            String repositoryAttributeName = InMemoryUserRepository.SESSION_ATTRIBUTE_NAME + "." + repositoryName+"."+ cmsContext.isPreview();

            userRepository = (InMemoryUserRepository) session.getAttribute(repositoryAttributeName);
            if (userRepository == null) {
                userRepository = createRepository(repositoryKey, publishRepository);

                session.setAttribute(repositoryAttributeName, userRepository);
            }
        }
         
        
        return userRepository;
    }
    
    public InMemoryUserRepository getUserRepository(CMSContext cmsContext, String repositoryName) {
        // for previewed respositories, ensure online repository has been loaded
        // just for synchronicity of the 2 shared repositories initialisation
        
        if( supportsPreview(repositoryName)) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(false);
            InMemoryUserRepository publishRespository = getOrCreateUserRepository(cmsContext, repositoryName, null);
            cmsContext.setPreview(true);
            getOrCreateUserRepository(cmsContext, repositoryName, publishRespository);
            cmsContext.setPreview(savedPreview);
        }
        
        return getOrCreateUserRepository(cmsContext, repositoryName, null);
    }

    protected InMemoryUserRepository createRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {
        
        InMemoryUserRepository userRepository = null;
        
        if ("templates".equals(repositoryKey.getRepositoryName()))  {
            userRepository = new TemplatesRepository(repositoryKey);
        }
        if ("myspace".equals(repositoryKey.getRepositoryName()))    {
            userRepository = new UserWorkspacesRepository(repositoryKey);
        }
        if ("sites".equals(repositoryKey.getRepositoryName()))  {
            userRepository = new SiteRepository(repositoryKey, publishRepository);   
        }
        
        return userRepository;
    }

    @Override
    public NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id) throws CMSException {
        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id.getRepositoryName());
        return userRepository.getNavigationItem(id.getInternalID());
    }

    @Override
    public void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
        InMemoryUserRepository userRepository = getUserRepository(cmsContext, repositoryName);
        userRepository.addListener(listener);
        
        if( supportsPreview(repositoryName)) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(true);
            userRepository = getUserRepository(cmsContext, repositoryName);
            userRepository.addListener(listener);
            cmsContext.setPreview(savedPreview);
        }
        
        
    }

    protected boolean supportsPreview( String repositoryName) {
        if ("sites".equals(repositoryName))  {
            return true;  
        }
        
        return false;
     }
   

}
