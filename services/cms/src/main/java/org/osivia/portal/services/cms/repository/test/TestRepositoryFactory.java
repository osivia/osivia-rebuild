package org.osivia.portal.services.cms.repository.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.SuperUserContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.UserRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.context.ControllerContextAdapter;

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

    
    public UniversalID getDefaultPortal() {
        return new UniversalID("sites:ID_SITE_A");
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


        String className = System.getProperty("osivia.cms.repository." + repositoryKey.getRepositoryName() + ".className");
        try {
            userRepository = createDynamicUserRepository(repositoryKey, publishRepository, userId, className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return userRepository;
    }


    /**
     * Creates a new TestRepository object.
     *
     * @param repositoryKey the repository key
     * @param publishRepository the publish repository
     * @param userId the user id
     * @param className the class name
     * @return the base user repository
     * @throws ClassNotFoundException the class not found exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    
    protected BaseUserRepository createDynamicUserRepository(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userId, String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        
        if( className != null)  {
            Class clazz = Class.forName(className);
            
            // Search for an "appropriate" constructor.
            for (Constructor<?> ctor : clazz.getConstructors()) {
                Class<?>[] paramTypes = ctor.getParameterTypes();

                // If the arity matches, let's use it.
                if (paramTypes.length == 2) {

                    // Convert the String arguments into the parameters' types.
                    Object[] convertedArgs = new Object[2];
                    convertedArgs[0] = repositoryKey;
                    convertedArgs[1] = userId;
                    // Instantiate the object with the converted arguments.
                    return (BaseUserRepository) ctor.newInstance(convertedArgs);
                }
                
                // If the parity matches, let's use it.
                if (paramTypes.length == 3) {

                    // Convert the String arguments into the parameters' types.
                    Object[] convertedArgs = new Object[3];
                    convertedArgs[0] = repositoryKey;
                    convertedArgs[1] = publishRepository;
                    convertedArgs[2] = userId;
                    // Instantiate the object with the converted arguments.
                    return (BaseUserRepository) ctor.newInstance(convertedArgs);
                }
                
            }                
        }
        
        return null;
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

        NativeRepository repository = (NativeRepository) getOrCreateUserRepository(cmsContext, repositoryName, null);
        

        ((BaseUserRepository) repository).setPortalContext(cmsContext.getPortalControllerContext());
        
        return repository;
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
