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
package org.jboss.portal.theme.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ThemeException;
import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.xb.binding.UnmarshallerFactory;

import javax.management.MBeanServer;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Deploy the themes contained in a WebApplication. <p>This deployer scans the archive for /WEB-INF/portal-themes.xml.
 * This file contains the definition of one or more themes. A theme is one, or a set of, css file, and the resources
 * that go with it.</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class ThemeDeployment extends Deployment
{
   private final ThemeDeploymentFactory factory;

   private static Logger log = Logger.getLogger(ThemeDeployment.class);

   public ThemeDeployment(URL url, PortalWebApp pwa, MBeanServer mbeanServer, ThemeDeploymentFactory factory)
   {
      super(url, pwa, mbeanServer);
      this.factory = factory;
   }

   /**
    * Create all the theme resources that are container in this portal web application.
    *
    * @throws DeploymentException if anything goes wrong
    */
   public void create() throws DeploymentException
   {
      if (log.isDebugEnabled())
      {
         log.debug("create new theme(s), found in :  " + pwa.getId());
      }
      InputStream themeStream = null;
      try
      {
         themeStream = IOTools.safeBufferedWrapper(url.openStream());

         RuntimeContext ctx = new RuntimeContext(pwa.getId(), pwa.getServletContext(), pwa.getContextPath(), pwa.getClassLoader());
         List portalThemes = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(themeStream, new PortalThemeMetaDataFactory(), null);
         for (Iterator i = portalThemes.iterator(); i.hasNext();)
         {
            PortalThemeMetaData themeMD = (PortalThemeMetaData)i.next();
            factory.getThemeService().addTheme(ctx, themeMD);
         }
      }
      catch (Exception e)
      {
         throw new DeploymentException(e);
      }
      finally
      {
         IOTools.safeClose(themeStream);
      }
   }

   /**
    * Destroy all the theme resources that are contained in this portal web application.
    *
    * @throws DeploymentException if anything goes wrong
    */
   public void destroy() throws DeploymentException
   {
      if (log.isDebugEnabled())
      {
         log.debug("destroying theme(s), found in :  " + pwa.getId());
      }
      try
      {
         factory.getThemeService().removeThemes(pwa.getId());
         if (log.isDebugEnabled())
         {
            log.debug("done destroying theme(s), found in :  " + pwa.getId());
         }
      }
      catch (ThemeException e)
      {
         throw new DeploymentException(e);
      }
   }

}