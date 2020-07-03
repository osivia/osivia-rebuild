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
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.services.cms.model.DocumentImpl;
import org.osivia.portal.services.cms.repository.InMemoryUserRepository;
import org.osivia.portal.services.cms.repository.TemplatesRepository;
import org.osivia.portal.services.cms.repository.UserWorkspacesRepository;
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
        superUserRepositories = new ConcurrentHashMap<String, InMemoryUserRepository>();
    }

    /** The super user repositories. */
    private Map<String, InMemoryUserRepository> superUserRepositories;
    



    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException {

        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id);
        DocumentImpl document = userRepository.getDocument(id.getInternalID());
        
        return document;

    }

    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the user repository
     */
    protected InMemoryUserRepository getUserRepository(CMSContext cmsContext, UniversalID id) {

        InMemoryUserRepository userRepository;


        if (cmsContext instanceof SuperUserContext) {
            
            userRepository = (InMemoryUserRepository) superUserRepositories.get(id.getRepositoryName());
            if (userRepository == null) {
                userRepository = createRepository(id, userRepository);
                superUserRepositories.put(id.getRepositoryName(), userRepository);
            }

        } else {

            ControllerContext ctx = ControllerContextAdapter.getControllerContext(cmsContext.getPortalControllerContext());

            HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession(true);

            String repositoryAttributeName = InMemoryUserRepository.SESSION_ATTRIBUTE_NAME + "." + id.getRepositoryName();

            userRepository = (InMemoryUserRepository) session.getAttribute(repositoryAttributeName);
            if (userRepository == null) {
                userRepository = createRepository(id, userRepository);

                session.setAttribute(repositoryAttributeName, userRepository);
            }
        }
        return userRepository;
    }

    protected InMemoryUserRepository createRepository(UniversalID id, InMemoryUserRepository userRepository) {
        
        if ("templates".equals(id.getRepositoryName()))
            userRepository = new TemplatesRepository(id.getRepositoryName());
        if ("myspace".equals(id.getRepositoryName()))
            userRepository = new UserWorkspacesRepository(id.getRepositoryName());
        
        

        
        
        return userRepository;
    }

    @Override
    public NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id, String navigation) throws CMSException {
        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id);
        return userRepository.getNavigationItem(id.getInternalID());
    }

    @Override
    public NavigationItem getContentPrimaryNavigationItem(CMSContext cmsContext, UniversalID id) throws CMSException {
        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id);
        return userRepository.getContentPrimaryNavigationItem(id.getInternalID());
    }

}
