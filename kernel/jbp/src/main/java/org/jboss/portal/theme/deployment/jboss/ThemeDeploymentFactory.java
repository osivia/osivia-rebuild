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
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.AbstractDeploymentFactory;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.portal.theme.ThemeService;

import javax.management.MBeanServer;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create a new Theme Deployer.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class ThemeDeploymentFactory extends AbstractDeploymentFactory
{
   protected static final Pattern URL_PATTERN = Pattern.compile(".*-themes\\.xml");

   private ThemeService themeService;

   public boolean acceptFile(URL url)
   {
      String urlAsFile = url.getFile();
      Matcher matcher = URL_PATTERN.matcher(urlAsFile);
      return matcher.matches();
   }

   public Deployment newInstance(URL url, PortalWebApp pwa, MBeanServer mbeanServer) throws DeploymentException
   {
      return new ThemeDeployment(url, pwa, mbeanServer, this);
   }

   public ThemeService getThemeService()
   {
      return themeService;
   }

   public void setThemeService(ThemeService themeService)
   {
      this.themeService = themeService;
   }
}
