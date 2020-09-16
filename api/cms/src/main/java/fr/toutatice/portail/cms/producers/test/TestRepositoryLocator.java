package fr.toutatice.portail.cms.producers.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.locator.Locator;
import org.springframework.context.ApplicationContext;

public class TestRepositoryLocator {


    @SuppressWarnings("unchecked")
    public static TestRepository getTemplateRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        CMSService cms = Locator.getApplicationContext().getBean(CMSService.class);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        TestRepository repository =  (TestRepository) cms.getUserRepository(cmsContext, repositoryName);
        InvocationHandler invocationHandler = new TestRepositoryInvocationHandler(repository);
        Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{TestRepository.class}, invocationHandler);
        return (TestRepository) proxy;
    }

}
