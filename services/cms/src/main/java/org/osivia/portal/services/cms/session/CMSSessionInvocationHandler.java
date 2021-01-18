package org.osivia.portal.services.cms.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.springframework.context.ApplicationContext;


public class CMSSessionInvocationHandler implements InvocationHandler {

    CMSSession cmsSession;
    

    public CMSSessionInvocationHandler(CMSSession cmsSession) {
        super();
        this.cmsSession = cmsSession;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
        Object result;
        try {
            Thread.currentThread().setContextClassLoader(cmsSession.getClass().getClassLoader());

            result = method.invoke(cmsSession, args);
        } catch( InvocationTargetException e)   {
            throw e.getCause();
        } finally {
            Thread.currentThread().setContextClassLoader(savedClassLoader);
        }

        return result;
    }

}
