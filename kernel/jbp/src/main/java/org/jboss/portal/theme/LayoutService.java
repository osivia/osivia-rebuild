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

import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.RenderSetMetaData;

/**
 * TODO: A description of this class.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 8784 $</tt>
 */
public interface LayoutService extends LayoutServiceInfo
{

   /**
    * Set the default layout (on a global level).
    *
    * @param name the name of the layout to set as default
    * @throws LayoutException
    */
   void setDefaultLayoutName(String name) throws LayoutException;

   PortalLayout getDefaultLayout();

   /** Add a layout. */
   void addLayout(RuntimeContext runtimeContext, PortalLayoutMetaData layoutMD) throws LayoutException;

   /**
    * Remove all layouts that are hosted in the provided application.
    *
    * @param appID the name of the application that hosts the layout(s) to be removed
    * @throws LayoutException
    */
   void removeLayouts(String appID) throws LayoutException;

   /** Register a renderSet with this service */
   void addRenderSet(RuntimeContext runtimeContext, RenderSetMetaData renderSet) throws LayoutException;

   public void setDefaultRenderSetName(String name);

   public String getDefaultRenderSetName();

   /**
    * Remove all rendersets that are hosted in the provided application.
    *
    * @param appID the name of the application that hosts the render set(s) to be removed
    * @throws LayoutException
    */
   void removeRenderSets(String appID) throws LayoutException;
}
