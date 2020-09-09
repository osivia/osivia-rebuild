package fr.toutatice.portail.cms.producers.sample.inmemory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.locator.Locator;
import org.springframework.context.ApplicationContext;

public class TemplatesLocator {


    @SuppressWarnings("unchecked")
    public static IPageMemoryRepository getTemplateRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        CMSService cms = Locator.getApplicationContext().getBean(CMSService.class);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        IPageMemoryRepository repository =  (IPageMemoryRepository) cms.getUserRepository(cmsContext, repositoryName);
        InvocationHandler invocationHandler = new TemplatesInvocationHandler(repository);
        Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{IPageMemoryRepository.class}, invocationHandler);
        return (IPageMemoryRepository) proxy;
    }

}
