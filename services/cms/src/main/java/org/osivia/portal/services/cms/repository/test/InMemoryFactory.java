package org.osivia.portal.services.cms.repository.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.SuperUserContext;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserRepository;

/**
 * A factory for creating InMemory objects.
 */

public class InMemoryFactory {

    /** The super user repositories. */
    private Map<SharedRepositoryKey, InMemoryUserRepository> superUserRepositories;


    public InMemoryFactory() {
        super();
        superUserRepositories = new ConcurrentHashMap<SharedRepositoryKey, InMemoryUserRepository>();
    }

    /**
     * Creates a new InMemory object.
     *
     * @param repositoryKey the repository key
     * @param publishRepository the publish repository
     * @return the in memory user repository
     */
    InMemoryUserRepository createRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {

        InMemoryUserRepository userRepository = null;

        if ("templates".equals(repositoryKey.getRepositoryName())) {
            userRepository = new TemplatesRepository(repositoryKey);
        }
        if ("myspace".equals(repositoryKey.getRepositoryName())) {
            userRepository = new UserWorkspacesRepository(repositoryKey);
        }
        if ("sites".equals(repositoryKey.getRepositoryName())) {
            userRepository = new SiteRepository(repositoryKey, publishRepository);
        }

        return userRepository;
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

            String repositoryAttributeName = InMemoryUserRepository.SESSION_ATTRIBUTE_NAME + "." + repositoryName + "." + cmsContext.isPreview();

            userRepository = (InMemoryUserRepository) session.getAttribute(repositoryAttributeName);
            if (userRepository == null) {
                userRepository = createRepository(repositoryKey, publishRepository);

                session.setAttribute(repositoryAttributeName, userRepository);
            }
        }


        return userRepository;
    }

    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @return the user repository
     */
    public NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) {
        // for previewed respositories, ensure online repository has been loaded
        // just for synchronicity of the 2 shared repositories initialisation

        if (supportsPreview(repositoryName)) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(false);
            InMemoryUserRepository publishRespository = getOrCreateUserRepository(cmsContext, repositoryName, null);
            cmsContext.setPreview(true);
            getOrCreateUserRepository(cmsContext, repositoryName, publishRespository);
            cmsContext.setPreview(savedPreview);
        }

        return (NativeRepository) getOrCreateUserRepository(cmsContext, repositoryName, null);
    }

    /**
     * Supports preview.
     *
     * @param repositoryName the repository name
     * @return true, if successful
     */
    protected boolean supportsPreview(String repositoryName) {
        if ("sites".equals(repositoryName)) {
            return true;
        }

        return false;
    }

    /**
     * Adds the listener.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @param listener the listener
     */
    public void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {

        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);
        userRepository.addListener(listener);

        if (userRepository.supportPreview()) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(true);
            userRepository = (InMemoryUserRepository) getUserRepository(cmsContext, repositoryName);
            userRepository.addListener(listener);
            cmsContext.setPreview(savedPreview);
        }

    }

}
