package org.osivia.portal.api.common.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.context.ApplicationContext;

public class Locator {

    private static ApplicationContext APPLICATION_CONTEXT;


    public static void registerApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }


    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }



    public static <T> T getService(Class<T> requiredType) throws IllegalArgumentException {
        return getService(null, requiredType);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getService(String name, Class<T> requiredType) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InvocationHandler invocationHandler = new LocatorInvocationHandler(name, requiredType);
        Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{requiredType}, invocationHandler);

        T result;
        if (requiredType.isAssignableFrom(proxy.getClass())) {
            result = (T) proxy;
        } else {
            throw new IllegalArgumentException();
        }

        return result;
    }

}
