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

import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Interface to represent the read only information of a rendered portal page. <p>The page result allows access to all
 * the information needed to generate the final markup.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class PageResult implements PageRendererContext
{

   /** . */
   protected final Map results;

   /** . */
   protected final Map windowContexts;

   /** . */
   protected String pageName;

   /** . */
   protected Map properties;

   /** . */
   protected String layoutState;

   /** . */
   final Map regions;

   public PageResult(String pageName, Map properties)
   {
      this.pageName = pageName;
      this.properties = properties == null ? new HashMap() : properties;

      this.results = new HashMap(5);
      this.windowContexts = new HashMap(5);
      this.regions = new HashMap(5);
   }

   public PageResult(String pageName)
   {
      this(pageName, new HashMap());
   }

   /**
    * Get a reference to the region object for the provided region name.
    *
    * @param regionName the name of the region to get
    * @return the region for the provided region name
    */
   public Region getRegion2(String regionName)
   {
      return (Region)regions.get(regionName);
   }

   /**
    * Get the name of the requested page.
    *
    * @return the name of the page that is being rendered
    */
   public String getPageName()
   {
      return pageName;
   }

   /**
    * Get the state string of the layout. <p>The state is used to further sub select a layout uri. One layout can
    * contain a separate layout uri per state. The state has to match to string version of one of the allowed window
    * states.<p>
    *
    * @return a string representing the current window state to potentially further select a more specific layout uri
    */
   public String getLayoutState()
   {
      return layoutState;
   }

   public void setLayoutState(String layoutState)
   {
      this.layoutState = layoutState;
   }

   /**
    * Get a Set of all window ids that are contained in this page.
    *
    * @return a set of all window ids on this page
    */
   public Set getWindowIds()
   {
      return windowContexts.keySet();
   }

   public WindowRendererContext getWindow(String windowId)
   {
      return getWindowContext(windowId);
   }

   /**
    * Get the <code>WindowContext</code> for the provided window id.
    *
    * @param windowId the window id identifying the portlet to get the context for
    * @return the window context for the provided window id
    */
   public WindowContext getWindowContext(String windowId)
   {
      return (WindowContext)windowContexts.get(windowId);
   }

   /**
    * Get a map of all <code>PortletContext</code>s on the page keyed by window id
    *
    * @return a map of all portlet on the page
    */
   public Map getWindowContextMap()
   {
      return windowContexts;
   }

   public void addWindowContext(WindowContext windowContext)
   {
      windowContexts.put(windowContext.getId(), windowContext);

      //
      Region region = (Region)regions.get(windowContext.getRegionName());

      //
      if (region == null)
      {
         region = new Region(this, windowContext.getRegionName());
         regions.put(region.getId(), region);
      }

      //
      region.addWindowContext(windowContext);
   }

   // PageRenderContext implementation *********************************************************************************

   public String getProperty(String name)
   {
      return (String)properties.get(name);
   }

   public Map getProperties()
   {
      return properties;
   }

   public Collection getRegions()
   {
      return regions.values();
   }

   public RegionRendererContext getRegion(String regionName)
   {
      return (RegionRendererContext)regions.get(regionName);
   }

   public void rebuild()
   {
      // Clear all windows
      for (Iterator i = regions.values().iterator(); i.hasNext();)
      {
         Region region = (Region)i.next();
         region.windows.clear();
      }

      // Readd all windows
      for (Iterator i = windowContexts.values().iterator(); i.hasNext();)
      {
         WindowContext wc = (WindowContext)i.next();

         //
         Region region = (Region)regions.get(wc.getRegionName());

         //
         if (region == null)
         {
            region = new Region(this, wc.getRegionName());
            regions.put(region.getId(), region);
         }

         //
         region.addWindowContext(wc);
      }
   }
}
                      