/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.jems.as.system;

import org.apache.log4j.Logger;
import org.jboss.mx.interceptor.AbstractInterceptor;
import org.jboss.mx.interceptor.Interceptor;
import org.jboss.mx.modelmbean.ModelMBeanInvoker;
import org.jboss.mx.server.Invocation;
import org.jboss.mx.server.InvocationContext;

import org.jboss.portal.common.mx.JavaBeanModelMBeanBuilder;
import org.jboss.portal.common.util.Tools;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossServiceModelMBean extends ModelMBeanInvoker
{

  

   public JBossServiceModelMBean(Object resource) throws MBeanException
   {
      this(resource, null, null);
   }

   public JBossServiceModelMBean(Object resource, Element config, String version) throws MBeanException
   {
     
   }

   // ModelMBean implementation -------------------------------------

   public boolean isSupportedResourceType(Object resource, String resourceType)
   {
      return "ObjectReference".equals(resourceType);
   }

   public MBeanInfo getMBeanInfo()
   {
      return info;
   }

   //

   protected void initDispatchers()
   {
   
   }

   private static class ServiceMixin extends ServiceMBeanSupport
   {

      /** . */
      private final Object resource;

      public ServiceMixin(Object resource)
      {
         this.resource = resource;
      }

      protected void createService() throws Exception
      {
         execute("create");
      }

      protected void startService() throws Exception
      {
         execute("start");
      }

      protected void stopService() throws Exception
      {
         execute("stop");
      }

      protected void destroyService() throws Exception
      {
         execute("destroy");
      }

      public Object getManagedResource()
      {
         return resource;
      }

      private void execute(String lifecycle) throws Exception
      {
         Method m = null;
         try
         {
            m = resource.getClass().getMethod(lifecycle, new Class[0]);
         }
         catch (NoSuchMethodException ignore)
         {
         }

         //
         if (m != null)
         {
            try
            {
               m.invoke(resource, new Object[0]);
            }
            catch (IllegalAccessException e)
            {
               throw new Error(e);
            }
            catch (InvocationTargetException e)
            {
               Throwable t = e.getTargetException();
               if (t instanceof Exception)
               {
                  throw (Exception)t;
               }
               else if (t instanceof Error)
               {
                  throw (Error)t;
               }
               else
               {
                  throw new Error(t);
               }
            }
         }
      }
   }

   // MBeanRegistration implementation *********************************************************************************


   public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
   {
return null;
   }

   public void postRegister(Boolean done)
   {
      super.postRegister(done);
   }

   public void preDeregister() throws Exception
   {
   }

   public void postDeregister()
   {
    }
}
