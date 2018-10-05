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
package org.jboss.portal.theme;

import java.util.Collection;

/**
 * A portal theme is a collection of tags that will be injected into a layout to govern the look and feel of a portal
 * page. <p>Themes are links to css, javascript and image/resource files. A theme's css needs to cooperate with the
 * markup in the portal layout, the markup produced by the portlets, and the markup produced by the render set.</p>
 * <p>Any theme that the portal will pick up needs to be defined in a theme descriptor. The theme desciptor is an xml
 * file in WEB-INF/ with the file name *-themes.xml</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.ObjectRenderer
 * @see org.jboss.portal.theme.PortalLayout
 */
public abstract class PortalTheme
{

   private ThemeInfo info;
   private ThemeServiceInfo serviceInfo;

   public PortalTheme()
   {
   }

   public abstract Collection getElements();

   /**
    * Initialize the theme with a reference to the theme service and the theme meta data
    *
    * @param serviceInfo a theme service reference
    * @param info        the meta data of the theme to render
    */
   public void init(ThemeServiceInfo serviceInfo, ThemeInfo info)
   {
      this.info = info;
      this.serviceInfo = serviceInfo;
   }

   /** Destroy the theme. Here is your chance to clean up */
   public void destroy()
   {
      info = null;
      serviceInfo = null;
   }

   /** @return the theme meta data */
   public ThemeInfo getThemeInfo()
   {
      return info;
   }

   /** @return the theme service info */
   public ThemeServiceInfo getServiceInfo()
   {
      return serviceInfo;
   }
}
