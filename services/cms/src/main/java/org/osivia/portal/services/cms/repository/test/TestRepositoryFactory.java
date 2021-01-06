package org.osivia.portal.services.cms.repository.test;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.SuperUserContext;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.services.cms.repository.BaseUserRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserRepository;

/**
 * A factory for creating InMemory objects.
 */

public class TestRepositoryFactory {

    /** The super user repositories. */
    private Map<SharedRepositoryKey, BaseUserRepository> superUserRepositories;


    public TestRepositoryFactory() {
        super();
        superUserRepositories = new ConcurrentHashMap<SharedRepositoryKey, BaseUserRepository>();
    }

    /**
     * Creates a new InMemory object.
     *
     * @param repositoryKey the repository key
     * @param publishRepository the publish repository
     * @return the in memory user repository
     */
    BaseUserRepository createRepository(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userId) {

        BaseUserRepository userRepository = null;

        if ("templates".equals(repositoryKey.getRepositoryName())) {
            userRepository = new TemplatesRepository(repositoryKey, userId);
        }
        if ("myspace".equals(repositoryKey.getRepositoryName())) {
            userRepository = new UserWorkspacesRepository(repositoryKey, userId);
        }
        if ("sites".equals(repositoryKey.getRepositoryName())) {
            userRepository = new SiteRepository(repositoryKey, publishRepository, userId);
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
    public BaseUserRepository getOrCreateUserRepository(CMSContext cmsContext, String repositoryName, BaseUserRepository publishRepository) {

        BaseUserRepository userRepository;

        SharedRepositoryKey repositoryKey = new SharedRepositoryKey(repositoryName, cmsContext.isPreview(), cmsContext.getlocale());

        if (cmsContext instanceof SuperUserContext) {

            userRepository = (BaseUserRepository) superUserRepositories.get(repositoryKey);
            if (userRepository == null) {
                userRepository = createRepository(repositoryKey, publishRepository, "superuser");
                superUserRepositories.put(repositoryKey, userRepository);
            }

        } else {
            HttpServletRequest request = cmsContext.getPortalControllerContext().getHttpServletRequest();
            
            String userName = request.getRemoteUser();

            HttpSession session = request.getSession(true);

            String repositoryAttributeName = BaseUserRepository.SESSION_ATTRIBUTE_NAME + "." + repositoryName + "." + repositoryKey.isPreview() + "." + repositoryKey.getLocale().toString();

            userRepository = (BaseUserRepository) session.getAttribute(repositoryAttributeName);
            if (userRepository == null || (!StringUtils.equals(userRepository.getUserName(), userName))) {
                userRepository = createRepository(repositoryKey, publishRepository, userName);

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
            BaseUserRepository publishRespository = getOrCreateUserRepository(cmsContext, repositoryName, null);
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


        addListenerForEachLanguage(cmsContext, repositoryName, listener);
        
        if (userRepository.supportPreview()) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(true);
            addListenerForEachLanguage(cmsContext, repositoryName, listener);
            cmsContext.setPreview(savedPreview);
        }
    }
    
    /**
     * Adds the listener for each language.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @param listener the listener
     */
    public void addListenerForEachLanguage(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
        Locale savedLocale = cmsContext.getlocale();
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);
        for( Locale locale: userRepository.getLocales())    {
            cmsContext.setLocale(locale);
            UserRepository localeRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);
            if( localeRepository != null)
                localeRepository.addListener(listener);
        }
        cmsContext.setLocale(savedLocale);
    }

}
