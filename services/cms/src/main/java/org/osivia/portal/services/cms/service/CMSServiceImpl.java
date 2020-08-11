package org.osivia.portal.services.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.InMemoryUserRepository;
import org.osivia.portal.services.cms.repository.TemplatesRepository;
import org.osivia.portal.services.cms.repository.UserWorkspacesRepository;
import org.springframework.stereotype.Service;

import fr.toutatice.portail.cms.producers.api.InternalCMSService;
import fr.toutatice.portail.cms.producers.api.RepositoryListener;

/**
 * CMS service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService, InternalCMSService {

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
    public InMemoryUserRepository getUserRepository(CMSContext cmsContext, String repositoryName) {

        InMemoryUserRepository userRepository;


        if (cmsContext instanceof SuperUserContext) {
            
            userRepository = (InMemoryUserRepository) superUserRepositories.get(repositoryName);
            if (userRepository == null) {
                userRepository = createRepository(repositoryName);
                superUserRepositories.put(repositoryName, userRepository);
            }

        } else {
            ControllerContext ctx = ControllerContextAdapter.getControllerContext(cmsContext.getPortalControllerContext());

            HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession(true);

            String repositoryAttributeName = InMemoryUserRepository.SESSION_ATTRIBUTE_NAME + "." + repositoryName;

            userRepository = (InMemoryUserRepository) session.getAttribute(repositoryAttributeName);
            if (userRepository == null) {
                userRepository = createRepository(repositoryName);

                session.setAttribute(repositoryAttributeName, userRepository);
            }
        }
         
        
        return userRepository;
    }

    protected InMemoryUserRepository createRepository(String repositoryName) {
        
        InMemoryUserRepository userRepository = null;
        
        if ("templates".equals(repositoryName))
            userRepository = new TemplatesRepository(repositoryName);
        if ("myspace".equals(repositoryName))
            userRepository = new UserWorkspacesRepository(repositoryName);
          
        
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
        
    }



}
