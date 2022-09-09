package org.osivia.portal.services.cms.repository.memory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.RepositoryFactory;
import org.osivia.portal.api.cms.repository.UserRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.services.cms.service.RuntimeBeanBuilder;



/**
 * A factory for creating InMemory objects.
 */

public class DefaultRepositoryFactory implements RepositoryFactory{

    /** The super user repositories. */
    private Map<SharedRepositoryKey, BaseUserRepository> staticRepositories;

    private Map<SharedRepositoryKey, SharedRepository>  sharedRepositories;
    


    public DefaultRepositoryFactory() {
        super();
        staticRepositories = new ConcurrentHashMap<SharedRepositoryKey, BaseUserRepository>();
        sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    }


    public Map<SharedRepositoryKey, SharedRepository> getSharedRepositories() {
        return sharedRepositories;
    }
    
    
    public UniversalID getDefaultPortal() {
        return new UniversalID(System.getProperty("osivia.portal.default"));
    }
    
    protected static final Log logger = LogFactory.getLog(DefaultRepositoryFactory.class);
    
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
            
            logger.debug("create repository" + userId + "className" + className);
            
            userRepository = createDynamicUserRepository(repositoryKey, publishRepository, userId, className);
            
            logger.debug("userRepository" + userRepository);
            
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return userRepository;
    }

    public List<NativeRepository> getRepositories( CMSContext cmsContext)  {

        List<NativeRepository> repositories =new ArrayList<>();
        
        Properties properties = System.getProperties();
        for (Object propertyName : properties.keySet()) {
            if (propertyName instanceof String) {
                String sPropertyName = (String) propertyName;
                if (sPropertyName.startsWith(RuntimeBeanBuilder.OSIVIA_CMS_REPOSITORY_PREFIX) && sPropertyName.endsWith(RuntimeBeanBuilder.OSIVIA_CMS_REPOSITORY_SUFFIX)) {
                    String repositoryName = sPropertyName.substring(RuntimeBeanBuilder.OSIVIA_CMS_REPOSITORY_PREFIX.length(), sPropertyName.indexOf(RuntimeBeanBuilder.OSIVIA_CMS_REPOSITORY_SUFFIX));

                    repositories.add(getUserRepository( cmsContext,  repositoryName));
                }
            }
        }

        return repositories;
        
        
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
                if (paramTypes.length == 3) {

                    // Convert the String arguments into the parameters' types.
                    Object[] convertedArgs = new Object[3];
                    convertedArgs[0] = this;
                    convertedArgs[1] = repositoryKey;
                    convertedArgs[2] = userId;
                    // Instantiate the object with the converted arguments.
                    return (BaseUserRepository) ctor.newInstance(convertedArgs);
                }
                
                // If the parity matches, let's use it.
                if (paramTypes.length == 4) {

                    // Convert the String arguments into the parameters' types.
                    Object[] convertedArgs = new Object[4];
                    convertedArgs[0] = this;
                    convertedArgs[1] = repositoryKey;
                    convertedArgs[2] = publishRepository;
                    convertedArgs[3] = userId;
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

        if (cmsContext.isSuperUserMode() ) {

            userRepository = (BaseUserRepository) staticRepositories.get(repositoryKey);
            if (userRepository == null) {
                userRepository = createRepository(repositoryKey, publishRepository, BaseUserRepository.SUPERUSER_NAME);
                staticRepositories.put(repositoryKey, userRepository);
            }

        } else {
            HttpServletRequest request = cmsContext.getPortalControllerContext().getHttpServletRequest();
            
            String userName = request.getRemoteUser();

            HttpSession session = request.getSession(true);
            
            if( session == null) {
                logger.error("request = " + request.toString() + "***"+request.isRequestedSessionIdValid()+"***"+ userName);
            }
            


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

        CMSContext adaptedContext = new CMSContext(cmsContext.getPortalControllerContext());
        adaptedContext.setLocale(cmsContext.getlocale());
        adaptedContext.setPreview(cmsContext.isPreview());
        adaptedContext.setSuperUserMode(cmsContext.isSuperUserMode());
        
        if (supportsPreview(repositoryName)) {

            adaptedContext.setPreview(false);
            BaseUserRepository publishRespository = getOrCreateUserRepository(adaptedContext, repositoryName, null);
            adaptedContext.setPreview(true);
            getOrCreateUserRepository(adaptedContext, repositoryName, publishRespository);
        }   else    {
            adaptedContext.setPreview(false);
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

        logger.debug("addListener repositoryName" + repositoryName);
        
        
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
     * Remove the listener.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @param listener the listener
     */
    public void removeListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {

        logger.debug("addListener repositoryName" + repositoryName);
        
        
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);


        removeListenerForEachLanguage(cmsContext, repositoryName, listener);
        
        if (userRepository.supportPreview()) {
            boolean savedPreview = cmsContext.isPreview();
            cmsContext.setPreview(true);
            removeListenerForEachLanguage(cmsContext, repositoryName, listener);
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
    
    
    
    /**
     * Remove the listener for each language.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @param listener the listener
     */
    public void removeListenerForEachLanguage(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
        Locale savedLocale = cmsContext.getlocale();
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);
        for( Locale locale: userRepository.getLocales())    {
            cmsContext.setLocale(locale);
            UserRepository localeRepository = (UserRepository) getUserRepository(cmsContext, repositoryName);
            if( localeRepository != null)
                localeRepository.removeListener(listener);
        }
        cmsContext.setLocale(savedLocale);
    }

}
