package fr.toutatice.portail.cms.producers.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;


public class TestRepositoryInvocationHandler implements InvocationHandler {

    AdvancedRepository templatesRepository;
    

    public TestRepositoryInvocationHandler(AdvancedRepository templatesRepository) {
        super();
        this.templatesRepository = templatesRepository;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
        Object result;
        try {
            Thread.currentThread().setContextClassLoader(templatesRepository.getClass().getClassLoader());

            result = method.invoke(templatesRepository, args);
        } finally {
            Thread.currentThread().setContextClassLoader(savedClassLoader);
        }

        return result;
    }

}
