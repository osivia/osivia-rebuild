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
package org.jboss.portal.theme.render;

import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeServiceInfo;

/**
 * Provide a context for theme related services.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ThemeContext
{

   /** . */
   private PortalTheme theme;

   /** . */
   private ThemeServiceInfo themeServiceInfo;

   public ThemeContext(PortalTheme theme, ThemeServiceInfo themeServiceInfo)
   {
      this.theme = theme;
      this.themeServiceInfo = themeServiceInfo;
   }

   /**
    * Returns the render context theme, it may be null if no theme was set.
    *
    * @return the theme
    */
   public PortalTheme getTheme()
   {
      return theme;
   }

   /**
    * Returns a theme matching the provided theme name or null if not found.
    *
    * @param themeName the theme name
    * @return the theme
    */
   public PortalTheme getTheme(String themeName)
   {
      return themeServiceInfo.getTheme(themeName, false);
   }
}
