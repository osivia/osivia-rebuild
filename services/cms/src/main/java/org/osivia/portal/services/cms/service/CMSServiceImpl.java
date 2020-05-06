package org.osivia.portal.services.cms.service;

import javax.servlet.http.HttpSession;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.core.context.ControllerContextAdapter;
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
    }

    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException {

        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id);
        return userRepository.getDocument(id.getInternalID());

    }

    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the user repository
     */
    protected InMemoryUserRepository getUserRepository(CMSContext cmsContext, UniversalID id) {
        ControllerContext ctx = ControllerContextAdapter.getControllerContext(cmsContext.getPortalControllerContext());

        HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession(true);

        String repositoryAttributeName = InMemoryUserRepository.SESSION_ATTRIBUTE_NAME + "." + id.getRepositoryName();

        InMemoryUserRepository userRepository = (InMemoryUserRepository) session.getAttribute(repositoryAttributeName);
        if (userRepository == null) {
            if("templates".equals(id.getRepositoryName()))
                userRepository = new TemplatesRepository(id.getRepositoryName());
            if("myspace".equals(id.getRepositoryName()))
                userRepository = new UserWorkspacesRepository(id.getRepositoryName());       
            
            session.setAttribute(repositoryAttributeName, userRepository);
        }
        return userRepository;
    }

    @Override
    public NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id, String navigation) throws CMSException {
        InMemoryUserRepository userRepository = getUserRepository(cmsContext, id);
        return userRepository.getNavigationItem(id.getInternalID());
    }

}
