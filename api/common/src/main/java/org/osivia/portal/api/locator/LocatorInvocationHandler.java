package org.osivia.portal.api.locator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;


public class LocatorInvocationHandler implements InvocationHandler {


    private final String name;

    private final Class<?> requiredType;


    public LocatorInvocationHandler(String name, Class<?> requiredType) {
        super();
        this.name = name;
        this.requiredType = requiredType;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ApplicationContext applicationContext = Locator.getApplicationContext();

        Object bean;
        if (StringUtils.isEmpty(this.name)) {
            bean = applicationContext.getBean(this.requiredType);
        } else {
            bean = applicationContext.getBean(this.name, this.requiredType);
        }

        ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
        Object result;
        try {
            Thread.currentThread().setContextClassLoader(bean.getClass().getClassLoader());

            result = method.invoke(bean, args);
        } finally {
            Thread.currentThread().setContextClassLoader(savedClassLoader);
        }

        return result;
    }

}
