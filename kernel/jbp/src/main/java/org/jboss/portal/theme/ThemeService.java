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

import org.jboss.portal.theme.metadata.PortalThemeMetaData;

/**
 * The ThemeService is the location where all the available themes are stored and retrieved. <p>The theme server works
 * together with a deployer to register and unregister themes. The portal can access the theme server via the server
 * manager, and query it for available themes.</P>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version <tt>$Revision: 8784 $</tt>
 */
public interface ThemeService extends ThemeServiceInfo
{
   /**
    * Add a theme.
    *
    * @param metaData the meta data about the theme
    */
   void addTheme(RuntimeContext runtimeContext, PortalThemeMetaData metaData) throws ThemeException;

   /**
    * Set the default theme on a global scope.
    *
    * @param themeID the registration id of the theme to be the new default theme
    * @throws ThemeException if the theme with this id is not available in the list of currently registered themes
    */
   void setDefault(ServerRegistrationID themeID) throws ThemeException;

   PortalTheme getDefaultTheme();

   /**
    * Remove the theme from the available themes.
    *
    * @param theme the theme to be removed
    * @throws ThemeException if the theme with this id is not available in the list of currently registered themes
    */
   void removeTheme(PortalTheme theme) throws ThemeException;

   /**
    * Remove all themes that are registered with the provided application. <p>On deployment of a new application, the
    * theme descriptor (if any present) in that application is parsed for themes that are to be registered with the
    * theme server. For each entry in the descriptor, a new theme is registered with the theme server. Uppon
    * undeployment of that same application, all themes must be deregistered. This method is a convenient way to achieve
    * this.
    *
    * @param applicationName the name of the application that hosts the themes to unregister
    * @throws ThemeException if there are no themes registered with this application, or if an error occurs during the
    *                        remove
    */
   void removeThemes(String applicationName) throws ThemeException;
}
