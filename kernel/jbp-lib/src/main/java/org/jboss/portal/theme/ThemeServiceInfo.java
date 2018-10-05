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
 * Read only part of the theme service interface.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public interface ThemeServiceInfo
{
   /**
    * Get a reference to a theme.
    *
    * @param themeID       the registration id of the theme to retrieve
    * @param defaultOnNull true, when the server should return the default theme, in case the requested is not found
    * @return the requested theme, null, or the default theme if <code>defaultOnNull</code> was provided as true
    * @throws IllegalArgumentException if the themeID is null
    */
   PortalTheme getTheme(ServerRegistrationID themeID, boolean defaultOnNull);

   /**
    * Get a reference to a theme.
    *
    * @param name          the name of the theme to retrieve
    * @param defaultOnNull true, when the server should return the default theme, in case the requested is not found
    * @return the requested theme, null, or the default theme if <code>defaultOnNull</code> was provided as true
    * @throws IllegalArgumentException if the themeID is null
    */
   PortalTheme getTheme(String name, boolean defaultOnNull);

   /**
    * @param themeId
    * @return
    */
   PortalTheme getThemeById(String themeId);

   /**
    * Get a Collection of all registered themes.
    *
    * @return a Collection of all registered themes
    */
   Collection getThemes();

   /**
    * Get a Collection of all the registered theme's names
    *
    * @return a collection of theme names
    */
   Collection getThemeNames();
}
