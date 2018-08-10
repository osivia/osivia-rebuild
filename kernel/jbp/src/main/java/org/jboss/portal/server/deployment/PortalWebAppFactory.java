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

import org.apache.log4j.Logger;
import org.jboss.web.WebApplication;
import org.xml.sax.EntityResolver;

import javax.management.MBeanServer;
import java.lang.reflect.Method;

/**
 * Creates the JBossWebApp according to the JBossWeb found.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PortalWebAppFactory
{

   private static Logger log = Logger.getLogger(PortalWebAppFactory.class);

   private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

   private static final int UNKNOWN = 0;
   private static final int TOMCAT4 = 1;
   private static final int TOMCAT5 = 2;
   private static final int TOMCAT6 = 3;

   private final MBeanServer server;

   public PortalWebAppFactory(MBeanServer server)
   {
      this.server = server;
   }

   /**
    * Create a portal web application.
    *
    * @return the portal web app or null if it cannot be created.
    */
   public PortalWebApp create(WebApplication webApp, EntityResolver jbossAppEntityResolver) throws CannotCreatePortletWebAppException
   {
      int version = getVersion();
      switch (version)
      {
         case TOMCAT4:
            return new PortalWebTomcat4App(webApp, jbossAppEntityResolver);
         case TOMCAT5:
            return new PortalWebTomcat5App(webApp, server, jbossAppEntityResolver);
         case TOMCAT6:
            return new PortalWebTomcat6App(webApp, server, jbossAppEntityResolver);
         default:
            throw new CannotCreatePortletWebAppException("JBossWeb cannot handle it : " + version);
      }
   }

   /**
    * Recognize the jbossweb container and returns its version.
    *
    * @return the jbossweb detected version or <code>UNKNOWN</code>
    */
   private static int getVersion()
   {
      try
      {
         // Get the classloader
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         Class serverInfoClass = cl.loadClass("org.apache.catalina.util.ServerInfo");
         Method getServerInfoMethod = serverInfoClass.getMethod("getServerInfo", EMPTY_CLASS_ARRAY);
         String result = (String)getServerInfoMethod.invoke(null, EMPTY_OBJECT_ARRAY);
         if (result != null)
         {
            if (result.startsWith("Apache Tomcat/6"))
            {
               return TOMCAT6;
            }
            else if (result.startsWith("Apache Tomcat/5"))
            {
               return TOMCAT5;
            }
            else if (result.startsWith("Apache Tomcat/4"))
            {
               return TOMCAT4;
            }
            else if (result.startsWith("JBoss Web Server/1"))
            {
               return TOMCAT5;
            }
            else if (result.startsWith("JBossWeb/2"))
            {
               return TOMCAT6;
            }
            else
            {
               log.error("Cannot handle tomcat version: " + result);
            }
         }
      }
      catch (ClassNotFoundException e)
      {
         log.error("Cannot getPortalObjectContext catalina ServerInfo class");
      }
      catch (NoSuchMethodException e)
      {
         log.error("Cannot invoke ServerInfo.getServerInfo()", e);
      }
      catch (Exception e)
      {
         log.error("Unexpected error", e);
      }
      return UNKNOWN;
   }
}
