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
import org.jboss.mx.util.MBeanProxyExt;
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

   // Attributes ----------------------------------------------------

   static
   {
      try
      {
         serviceMixinInfo = JavaBeanModelMBeanBuilder.build(ServiceMixin.class, Object.class);
      }
      catch (Exception e)
      {
         throw new Error(e);
      }
   }

   /** . */
   private static final ModelMBeanInfo serviceMixinInfo;

   /** . */
   private static final Logger log = Logger.getLogger(JBossServiceModelMBean.class);

   /** . */
   public static final String PORTAL_KERNEL_NO_PROXIES = "portal.kernel.no_proxies";

   // Constructors --------------------------------------------------

   /** . */
   private final boolean replaceProxies;

   /** . */
   private final ServiceMixin serviceMixin;

   /** . */
   private final POJOInjector injector;

   /** . */
   private final boolean createExists;

   /** . */
   private final boolean startExists;

   /** . */
   private final boolean stopExists;

   /** . */
   private final boolean destroyExists;

   /** . */
   private final boolean getStateExists;

   /** . */
   private final boolean getStateStringExists;

   /** . */
   private final boolean getManagedResourceExists;

   public JBossServiceModelMBean(Object resource) throws MBeanException
   {
      this(resource, null, null);
   }

   public JBossServiceModelMBean(Object resource, Element config, String version) throws MBeanException
   {
      try
      {
         setManagedResource(resource, "ObjectReference");

         //
         boolean pojo = !(resource instanceof ServiceMBeanSupport);

         // Build
         ModelMBeanInfo info = JavaBeanModelMBeanBuilder.build(resource.getClass(), pojo ? Object.class : ServiceMBeanSupport.class);

         //
         this.replaceProxies = "true".equals(System.getProperty(PORTAL_KERNEL_NO_PROXIES, "true"));
         this.injector = new POJOInjector();
         this.serviceMixin = new ServiceMixin(resource);

         //
         List mmois = Tools.toList(info.getOperations());
         List mmais = Tools.toList(info.getAttributes());

         //
         boolean createExists = false;
         boolean startExists = false;
         boolean stopExists = false;
         boolean destroyExists = false;
         boolean getStateExists = false;
         boolean getStateStringExists = false;
         boolean getManagedResourceExists = false;

         //
         for (int i = 0; i < mmois.size(); i++)
         {
            ModelMBeanOperationInfo mmoi = (ModelMBeanOperationInfo)mmois.get(i);
            if (mmoi.getSignature().length == 0)
            {
               if (mmoi.getName().equals("create"))
               {
                  createExists = true;
               }
               else if (mmoi.getName().equals("start"))
               {
                  startExists = true;
               }
               else if (mmoi.getName().equals("stop"))
               {
                  stopExists = true;
               }
               else if (mmoi.getName().equals("destroy"))
               {
                  destroyExists = true;
               }
               else if (mmoi.getName().equals("getState"))
               {
                  getStateExists = true;
               }
               else if (mmoi.getName().equals("getStateString"))
               {
                  getStateStringExists = true;
               }
               else if (mmoi.getName().equals("getManagedResource"))
               {
                  getManagedResourceExists = true;
               }
            }
         }

         //
         this.createExists = createExists;
         this.startExists = startExists;
         this.stopExists = stopExists;
         this.destroyExists = destroyExists;
         this.getStateExists = getStateExists;
         this.getStateStringExists = getStateStringExists;
         this.getManagedResourceExists = getManagedResourceExists;

         //
         if (!createExists)
         {
            mmois.add(serviceMixinInfo.getOperation("create"));
         }
         if (!startExists)
         {
            mmois.add(serviceMixinInfo.getOperation("start"));
         }
         if (!stopExists)
         {
            mmois.add(serviceMixinInfo.getOperation("stop"));
         }
         if (!destroyExists)
         {
            mmois.add(serviceMixinInfo.getOperation("destroy"));
         }
         if (!getStateExists)
         {
            mmois.add(serviceMixinInfo.getOperation("getState"));
            mmais.add(serviceMixinInfo.getAttribute("State"));
         }
         if (!getStateStringExists)
         {
            mmois.add(serviceMixinInfo.getOperation("getStateString"));
            mmais.add(serviceMixinInfo.getAttribute("StateString"));
         }
         if (!getManagedResourceExists)
         {
            mmois.add(serviceMixinInfo.getOperation("getManagedResource"));
            mmais.add(serviceMixinInfo.getAttribute("ManagedResource"));
         }

         //
         info = new ModelMBeanInfoSupport(
            info.getClassName(),
            info.getDescription(),
            (ModelMBeanAttributeInfo[])mmais.toArray(new ModelMBeanAttributeInfo[mmais.size()]),
            (ModelMBeanConstructorInfo[])info.getConstructors(),
            (ModelMBeanOperationInfo[])mmois.toArray(new ModelMBeanOperationInfo[mmois.size()]),
            (ModelMBeanNotificationInfo[])info.getNotifications());

         //
         setModelMBeanInfo(info);
      }
      catch (InstanceNotFoundException e)
      {
         throw new MBeanException(e);
      }
      catch (InvalidTargetObjectTypeException e)
      {
         throw new MBeanException(e, "Unsupported resource type: " + resourceType);
      }
      catch (Exception e)
      {
         throw new MBeanException(e);
      }
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
      super.initDispatchers();

      //
      for (Iterator i = attributeContextMap.values().iterator(); i.hasNext();)
      {
         InvocationContext ctx = (InvocationContext)i.next();
         if ("State".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return new Integer(serviceMixin.getState());
               }
            });
         }
         else if ("StateString".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return serviceMixin.getStateString();
               }
            });
         }
         else if ("ManagedResource".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return serviceMixin.getManagedResource();
               }
            });
         }
         else if (replaceProxies)
         {
            // Retrieve original dispatcher
            final Interceptor dispatcher = ctx.getDispatcher();

            //
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  if (InvocationContext.OP_SETATTRIBUTE.equals(invocation.getType()))
                  {
                     Object value = invocation.getArgs()[0];
                     if (value != null && Proxy.isProxyClass(value.getClass()))
                     {
                        Object handler = Proxy.getInvocationHandler(value);
                        if (handler instanceof MBeanProxyExt)
                        {
                           MBeanProxyExt pojoHandler = (MBeanProxyExt)handler;
                           POJOInjection injection = new POJOInjection(pojoHandler, dispatcher, invocation);
                           injector.addInjection(injection);
                        }
                        else
                        {
                           dispatcher.invoke(invocation);
                        }
                     }
                     else
                     {
                        dispatcher.invoke(invocation);
                     }

                     //
                     return null;
                  }
                  else
                  {
                     return dispatcher.invoke(invocation);
                  }
               }
            });
         }
      }

      //
      for (Iterator i = operationContextMap.values().iterator(); i.hasNext();)
      {
         InvocationContext ctx = (InvocationContext)i.next();
         if ("create".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  serviceMixin.create();
                  return null;
               }
            });
         }
         else if ("start".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  serviceMixin.start();
                  return null;
               }
            });
         }
         else if ("stop".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  serviceMixin.stop();
                  return null;
               }
            });
         }
         else if ("destroy".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  serviceMixin.destroy();
                  return null;
               }
            });
         }
         else if ("getState".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return new Integer(serviceMixin.getState());
               }
            });
         }
         else if ("getStateString".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return serviceMixin.getStateString();
               }
            });
         }
         else if ("getManagedResource".equals(ctx.getName()))
         {
            ctx.setDispatcher(new AbstractInterceptor()
            {
               public Object invoke(Invocation invocation) throws Throwable
               {
                  return serviceMixin.getManagedResource();
               }
            });
         }
      }
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
      name = super.preRegister(server, name);

      //
      server.addNotificationListener(JMXConstants.MBEAN_SERVER_DELEGATE, injector, null, null);

      //
      return name;
   }

   public void postRegister(Boolean done)
   {
      super.postRegister(done);
   }

   public void preDeregister() throws Exception
   {
      getServer().removeNotificationListener(JMXConstants.MBEAN_SERVER_DELEGATE, injector);

      //
      super.preDeregister();
   }

   public void postDeregister()
   {
      super.postDeregister();
   }
}
