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
package org.jboss.portal.server.deployment;

import org.jboss.portal.web.command.CommandServlet;
import org.jboss.web.WebApplication;
import org.xml.sax.EntityResolver;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PortalWebTomcat5App extends PortalWebApp
{

   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
   private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

   private final Object standardContext;
   private final WebApplication webApp;

   public PortalWebTomcat5App(WebApplication webApp, MBeanServer server, EntityResolver jbossAppEntityResolver) throws CannotCreatePortletWebAppException
   {
      try
      {
         ObjectName name = (ObjectName)webApp.getAppData();

         //
         this.webApp = webApp;
         this.standardContext = server.getAttribute(name, "managedResource");

         //
         String contextPath = PortalWebTomcat4App.getContextPath(standardContext);
         ServletContext servletContext = PortalWebTomcat4App.getServletContext(standardContext);
         ClassLoader loader = getClassLoader(server, name);
         URL url = webApp.getURL();

         //
         init(servletContext, url, loader, contextPath, jbossAppEntityResolver);
      }
      catch (Exception e)
      {
         CannotCreatePortletWebAppException ex = null;
         if (e instanceof CannotCreatePortletWebAppException)
         {
            ex = (CannotCreatePortletWebAppException)e;
         }
         else
         {
            ex = new CannotCreatePortletWebAppException(e);
         }
         throw ex;
      }
   }

   public void instrument() throws Exception
   {
      // Get class loader
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      // Get some class
      Class containerItf = cl.loadClass("org.apache.catalina.Container");

      // Inject the command servlet only one time, so we look if it exist already
      Method findChildMethod = containerItf.getMethod("findChild", new Class[]{String.class});
      Object commandServlet = findChildMethod.invoke(standardContext, new Object[]{"CommandServlet"});
      if (commandServlet == null)
      {
         setRunAsListenerAssociation(webApp.getMetaData());
         try
         {
            addCommandServlet(cl);
         }
         finally
         {
            setRunAsListenerAssociation(null);
         }
      }
   }

   private void setRunAsListenerAssociation(Object o)
   {
      try
      {
         Class clazz = Thread.currentThread().getContextClassLoader().loadClass("org.jboss.web.tomcat.security.RunAsListener");

         //
         log.debug("RunAsListener detected");
         Field field = clazz.getDeclaredField("metaDataLocal");
         ThreadLocal threadLocal = (ThreadLocal)field.get(null);
         threadLocal.set(o);
      }
      catch (ClassNotFoundException e)
      {
         log.debug("RunAsListener not detected");
      }
      catch (IllegalAccessException e)
      {
         log.error("Cannot set the RunAsListener threadlocal because of a security manager", e);
      }
      catch (Exception e)
      {
         log.error("Unexpected exception");
      }
   }

   private void addCommandServlet(ClassLoader cl) throws Exception
   {
      //
      Class containerItf = cl.loadClass("org.apache.catalina.Container");
      Class standardContextClass = cl.loadClass("org.apache.catalina.core.StandardContext");
      Class containerBaseClass = cl.loadClass("org.apache.catalina.core.ContainerBase");
      Class contextItf = cl.loadClass("org.apache.catalina.Context");

      // Ask the context to create a wrapper
      Method createWrapperMethod = standardContextClass.getMethod("createWrapper", EMPTY_CLASS_ARRAY);
      Object wrapper = createWrapperMethod.invoke(standardContext, EMPTY_OBJECT_ARRAY);
      Class wrapperClass = wrapper.getClass();
      Object[] wrapperArray = (Object[])Array.newInstance(containerItf, 1);
      wrapperArray[0] = wrapper;

      Method setServletNameMethod = wrapperClass.getMethod("setServletName", new Class[]{String.class});
      setServletNameMethod.invoke(wrapper, new Object[]{"CommandServlet"});

      Method setServletClassMethod = wrapperClass.getMethod("setServletClass", new Class[]{String.class});
      setServletClassMethod.invoke(wrapper, new Object[]{CommandServlet.class.getName()});

      Method setLoadOnStartupMethod = wrapperClass.getMethod("setLoadOnStartup", new Class[]{int.class});
      setLoadOnStartupMethod.invoke(wrapper, new Object[]{new Integer(0)});

      Method addChildMethod = containerBaseClass.getMethod("addChild", new Class[]{containerItf});
      addChildMethod.invoke(standardContext, new Object[]{wrapper});

      Method addServletMapping = contextItf.getMethod("addServletMapping", new Class[]{String.class, String.class});
      addServletMapping.invoke(standardContext, new Object[]{"/jbossportlet", "CommandServlet"});

      Method loadOnStartupMethod = standardContextClass.getMethod("loadOnStartup", new Class[]{wrapperArray.getClass()});
      loadOnStartupMethod.invoke(standardContext, new Object[]{wrapperArray});
   }

   private ClassLoader getClassLoader(MBeanServer server, ObjectName name) throws Exception
   {
      Object loader = server.getAttribute(name, "loader");
      Method getClassLoaderMethod = loader.getClass().getMethod("getClassLoader", new Class[0]);
      return (ClassLoader)getClassLoaderMethod.invoke(loader, new Object[0]);
   }
}
