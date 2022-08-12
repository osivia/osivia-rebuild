package org.osivia.portal.services.cms.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.repository.UserRepository;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.sessions.CMSSessionRecycle;
import org.osivia.portal.core.sessions.ICMSSessionStorage;
import org.osivia.portal.services.cms.repository.test.DefaultRepositoryFactory;
import org.osivia.portal.services.cms.session.CMSSessionImpl;
import org.osivia.portal.services.cms.session.CMSSessionInvocationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService {

    @Autowired
    ICMSSessionStorage sessionStorage;
    
    @Autowired
    private RuntimeBeanBuilder builder;

    
    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(CMSServiceImpl.class);
    
    
    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
        repositoryFactory = new DefaultRepositoryFactory();
        

    }

    DefaultRepositoryFactory repositoryFactory;



    @Override
    public void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
        repositoryFactory.addListener(cmsContext, repositoryName, listener);
    }


    @Override
    public NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        
        /*
        if( log.isDebugEnabled())   {
            if( cmsContext.getPortalControllerContext() != null)
                log.debug("getUserRepository request "+ cmsContext.getPortalControllerContext().getHttpServletRequest());
            else
                log.debug("getUserRepository portalControllerContext = null ");
            
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            
            for(int i=8; i< Math.min(stack.length, 15); i++) {
                log.debug("-"+stack[i].getClassName()+"."+stack[i].getMethodName());
            }
        }
        */
        
        return repositoryFactory.getUserRepository(cmsContext, repositoryName);
    }


    @Override
    public List<NativeRepository> getUserRepositories(CMSContext cmsContext) throws CMSException {
       return repositoryFactory.getRepositories(cmsContext);
    }


    @Override
    public CMSSession getCMSSession(CMSContext cmsContext) throws CMSException {

        CMSSessionRecycle proxy = (CMSSessionRecycle) sessionStorage.getSession(cmsContext);

        if (proxy == null) {
            // create new proxy

            CMSSession cmsSession = new CMSSessionImpl(this);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InvocationHandler invocationHandler = new CMSSessionInvocationHandler(cmsSession);
            proxy = (CMSSessionRecycle) Proxy.newProxyInstance(classLoader, new Class[]{CMSSession.class, CMSSessionRecycle.class}, invocationHandler);
            proxy.setCMSContext(cmsContext);
            sessionStorage.storeSession(proxy);
            
            /*
            if( log.isDebugEnabled())   {
                if( cmsContext.getPortalControllerContext() != null)
                    log.debug("getCMSSession request "+ cmsContext.getPortalControllerContext().getHttpServletRequest());
                else
                    log.debug("getCMSSession portalControllerContext = null ");
            }
            */
            
         }


        return (CMSSession) proxy;


    }


    @Override
    public UniversalID getDefaultPortal(CMSContext cmsContext) throws CMSException {
        return repositoryFactory.getDefaultPortal();
    }


}
