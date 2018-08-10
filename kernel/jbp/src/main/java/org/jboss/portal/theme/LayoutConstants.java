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
 * Constants for the layout functionality.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 */
public final class LayoutConstants
{
   private LayoutConstants()
   {
   }

   /** Attribute used to store and retrieve the current page */
   public static final String ATTR_PAGE = "PAGE";

   public static final String ATTR_RENDERCONTEXT = "RENDERCONTEXT";

   /** currently not used */
   public static final String ATTR_LAYOUT = "_layout";
   public static final String PARAM_LAYOUT_URI = "layoutURI";
   public static final String PARAM_LAYOUT_STATE = "layoutState";
   public static final String ATTR_RENDERSET = "_renderSet";
   public static final String ATTR_PORTLET_WINDOW_NAME = "windowName";
   public static final String ATTR_LAYOUTSERVER = "LAYOUTSERVER";
   public static final String ATTR_INFO = "_info";
   public static final String ATTR_RESULTS = "_windowresults";
}
