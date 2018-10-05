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

/**
 * Constant definitions for themes and layouts.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 */
public final class ThemeConstants
{

   private ThemeConstants()
   {
   }

   // Portal property keys

   /** Key name to access the selected layout for the portal or page (via the portal or page properties) */
   public static final String PORTAL_PROP_LAYOUT = "layout.id";

   /** Key name to access the selected theme for the portal or page (via the portal or page properties) */
   public static final String PORTAL_PROP_THEME = "theme.id";

   /** Key name to access the selected render set for the portal or page (via the portal or page properties) */
   public static final String PORTAL_PROP_RENDERSET = "theme.renderSetId";

   /**
    * Key name to access the selected window renderer for the portal, page, or window (via the portal or page
    * properties)
    */
   public static final String PORTAL_PROP_WINDOW_RENDERER = "theme.windowRendererId";

   /**
    * Key name to access the selected decoration renderer for the portal, page, or window (via the portal or page
    * properties)
    */
   public static final String PORTAL_PROP_DECORATION_RENDERER = "theme.decorationRendererId";

   /**
    * Key name to access the selected portlet renderer for the portal, page, or window (via the portal or page
    * properties)
    */
   public static final String PORTAL_PROP_PORTLET_RENDERER = "theme.portletRendererId";

   /**
    *
    */
   public static final String PORTAL_PROP_ORDER = "theme.order";

   /**
    *
    */
   public static final String PORTAL_PROP_REGION = "theme.region";

}
