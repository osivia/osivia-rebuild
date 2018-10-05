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
package org.jboss.portal.theme.page;

import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A region on a page. <p>A region wraps one or more portlets to allow them to act as one unit inside the layout of a
 * page.</p>
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class Region implements RegionRendererContext
{

   /** . */
   private final String name;

   /** . */
   private final PageResult page;

   /** . */
   final List windows;

   /** . */
   private Map properties;

   /**
    * Create a region with default orientation (how the portlet windows are arranged) and the region name as the css id
    * selector
    *
    * @param name the name of the region to create
    */
   public Region(PageResult page, String name)
   {
      this.page = page;
      this.name = name;
      this.windows = new ArrayList();
      this.properties = new HashMap();
   }

   public void setProperty(String name, String value)
   {
      properties.put(name, value);
   }

   /** @return if there are any portlet windows in this region */
   public boolean isEmpty()
   {
      return windows.isEmpty();
   }

   /**
    * Add a window to the region
    *
    * @param windowContext the context of the window signaling where the window should go in the region
    */
   void addWindowContext(WindowContext windowContext)
   {
      windows.add(windowContext);
      Collections.sort(windows);
   }

   public String toString()
   {
      return "Region[name" + name + "]";
   }

   // RegionRenderContext implementation *******************************************************************************

   public String getId()
   {
      return name;
   }

   public String getProperty(String name)
   {
      return (String)properties.get(name);
   }

   public Map getProperties()
   {
      return properties;
   }

   public Collection getWindows()
   {
      if (windows.isEmpty())
      {
         return Collections.EMPTY_LIST;
      }
      return Collections.unmodifiableList(windows);
   }

   public Orientation getOrientation()
   {
      return null;
   }

   public String getCSSId()
   {
      return null;
   }
}
