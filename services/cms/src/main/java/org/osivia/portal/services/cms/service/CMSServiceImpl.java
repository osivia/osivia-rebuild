package org.osivia.portal.services.cms.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.sessions.CMSSessionRecycle;
import org.osivia.portal.core.sessions.ICMSSessionStorage;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.repository.test.TestRepositoryFactory;
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

    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
        repositoryFactory = new TestRepositoryFactory();

    }

    TestRepositoryFactory repositoryFactory;

    /**
     * {@inheritDoc}
     */


    public Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException {
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, id.getRepositoryName());
        return userRepository.getDocument(id.getInternalID());

    }


    @Override
    public void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener) {
        repositoryFactory.addListener(cmsContext, repositoryName, listener);
    }


    @Override
    public NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        return repositoryFactory.getUserRepository(cmsContext, repositoryName);
    }


    public Result executeRequest(CMSContext cmsContext, Request request) throws CMSException {
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, request.getRepositoryName());
        return userRepository.executeRequest(request);
    }


    public Personnalization getPersonnalization(CMSContext cmsContext, UniversalID id) throws CMSException {
        UserRepository userRepository = (UserRepository) getUserRepository(cmsContext, id.getRepositoryName());
        return userRepository.getPersonnalization(id.getInternalID());
    }


    @Override
    public CMSSession getCMSSession(CMSContext cmsContext) throws CMSException {

        CMSSessionRecycle proxy = (CMSSessionRecycle) sessionStorage.getSession(cmsContext);

        if (proxy == null) {
            // create new proxy
            System.out.println("create cms session");
            CMSSession cmsSession = new CMSSessionImpl(this);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InvocationHandler invocationHandler = new CMSSessionInvocationHandler(cmsSession);
            proxy = (CMSSessionRecycle) Proxy.newProxyInstance(classLoader, new Class[]{CMSSession.class, CMSSessionRecycle.class}, invocationHandler);
            proxy.setCMSContext(cmsContext);
            sessionStorage.storeSession(proxy);
         }


        return (CMSSession) proxy;


    }


}
