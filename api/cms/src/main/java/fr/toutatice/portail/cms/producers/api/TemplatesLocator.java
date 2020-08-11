package fr.toutatice.portail.cms.producers.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.locator.Locator;
import org.springframework.context.ApplicationContext;

public class TemplatesLocator {


    @SuppressWarnings("unchecked")
    public static ITemplatesMemoryRepository getTemplateRepository(CMSContext cmsContext, String repositoryName) throws CMSException {
        InternalCMSService cms = Locator.getApplicationContext().getBean(InternalCMSService.class);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ITemplatesMemoryRepository repository =  (ITemplatesMemoryRepository) cms.getUserRepository(cmsContext, repositoryName);
        InvocationHandler invocationHandler = new TemplatesInvocationHandler(repository);
        Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{ITemplatesMemoryRepository.class}, invocationHandler);

        return (ITemplatesMemoryRepository) proxy;
    }

}
