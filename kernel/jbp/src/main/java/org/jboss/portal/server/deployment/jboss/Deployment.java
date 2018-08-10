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
package org.jboss.portal.server.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;

import org.jboss.portal.server.deployment.PortalWebApp;

import javax.management.MBeanServer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class Deployment
{

   /** The logger. */
   protected final Logger log = Logger.getLogger(getClass());

   /** The deployment URL. */
   protected final URL url;

   /** The optional web app abstraction. */
   protected final PortalWebApp pwa;

   /** The JMX server. */
   protected final MBeanServer mbeanServer;

   /**
    * Create a deployment that is not nested within a web application archive.
    *
    * @param url         the deployment URL
    * @param mbeanServer the JMX mbean server
    */
   public Deployment(URL url, MBeanServer mbeanServer)
   {
      this.url = url;
      this.pwa = null;
      this.mbeanServer = mbeanServer;
   }

   /**
    * Create a deployment nested within a web application archive
    *
    * @param url         the deployment URL
    * @param pwa         the web application
    * @param mbeanServer the JMX mbean server
    */
   public Deployment(URL url, PortalWebApp pwa, MBeanServer mbeanServer)
   {
      this.url = url;
      this.pwa = pwa;
      this.mbeanServer = mbeanServer;
   }

   public void create() throws DeploymentException
   {
   }

   public void start() throws DeploymentException
   {
   }

   public void stop() throws DeploymentException
   {
   }

   public void destroy() throws DeploymentException
   {
   }

   /** Find the URL of the WEB-INF directory in the war file. */
   public static URL findWEBINFURL(URL warURL) throws DeploymentException
   {
      try
      {
         if ("file".equals(warURL.getProtocol()))
         {
            File f = new File(warURL.getFile());
            f = new File(f, "WEB-INF");
            // this dir doesn't exist on exploded deployments and would miss the trailing slash then [JBPORTAL-1648]
            if (f.exists())
            {
               return f.toURL();
            }
            else
            {
               return new URL(warURL + "WEB-INF/");
            }
         }
         else
         {
            throw new DeploymentException("Deployment URL not found " + warURL);
         }
      }
      catch (MalformedURLException e)
      {
         throw new DeploymentException(e);
      }
   }


}
