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
package org.jboss.portal.theme.impl.render.dynamic;

import java.util.Map;

/**
 * Encapsulate dyna render options.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DynaRenderOptions
{

   /** Drag and drop. */
   public static final String DND_ENABLED = "theme.dyna.dnd_enabled";

   /** Partial refresh. */
   public static final String PARTIAL_REFRESH_ENABLED = "theme.dyna.partial_refresh_enabled";

   /** Blocking HTTP GET (to do). */
   public static final String BLOCKING_HTTP_GET_ENABLED = "theme.dyna.blocking_http_get_enabled";

   /** Flyweight pattern. */
   private static final DynaRenderOptions[] FLYWEIGHTS =
      {
         new DynaRenderOptions(null, null),
         new DynaRenderOptions(Boolean.TRUE, null),
         new DynaRenderOptions(Boolean.FALSE, null),
         null,
         new DynaRenderOptions(null, Boolean.TRUE),
         new DynaRenderOptions(Boolean.TRUE, Boolean.TRUE),
         new DynaRenderOptions(Boolean.FALSE, Boolean.TRUE),
         null,
         new DynaRenderOptions(null, Boolean.FALSE),
         new DynaRenderOptions(Boolean.TRUE, Boolean.FALSE),
         new DynaRenderOptions(Boolean.FALSE, Boolean.FALSE),
      };

   public static DynaRenderOptions getOptions(boolean dnd, boolean partialRefresh)
   {
      return getOptions(Boolean.valueOf(dnd), Boolean.valueOf(partialRefresh));
   }

   public static DynaRenderOptions getOptions(Boolean dnd, Boolean partialRefresh)
   {
      int index = dnd == null ? 0 : dnd.booleanValue() ? 1 : 2;
      index += partialRefresh == null ? 0 : partialRefresh.booleanValue() ? 4 : 8;
      return FLYWEIGHTS[index];
   }

   public static DynaRenderOptions getOptions(String dndValue, String partialRefreshValue)
   {
      Boolean dnd = dndValue == null ? null : Boolean.valueOf(dndValue);
      Boolean partialRefresh = partialRefreshValue == null ? null : Boolean.valueOf(partialRefreshValue);
      return getOptions(dnd, partialRefresh);
   }

   public static DynaRenderOptions getOptions(Map propertyMap)
   {
      String dndValue = (String)propertyMap.get(DND_ENABLED);
      String partialRefreshValue = (String)propertyMap.get(PARTIAL_REFRESH_ENABLED);
      return getOptions(dndValue, partialRefreshValue);
   }

   /** . */
   public static final DynaRenderOptions NO_AJAX = DynaRenderOptions.getOptions(Boolean.FALSE, Boolean.FALSE);

   /** . */
   public static final DynaRenderOptions AJAX = DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.TRUE);


   /**
    * Set the options on the specifed property map.
    *
    * @param propertyMap the map to alter
    */
   public void setOptions(Map propertyMap)
   {
      if (dnd != null)
      {
         propertyMap.put(DND_ENABLED, dnd.toString());
      }
      else
      {
         propertyMap.remove(DND_ENABLED);
      }
      if (partialRefresh != null)
      {
         propertyMap.put(PARTIAL_REFRESH_ENABLED, partialRefresh.toString());
      }
      else
      {
         propertyMap.remove(PARTIAL_REFRESH_ENABLED);
      }
   }

   /** . */
   private final Boolean dnd;

   /** . */
   private final Boolean partialRefresh;

   private DynaRenderOptions(Boolean dnd, Boolean partialRefresh)
   {
      this.dnd = dnd;
      this.partialRefresh = partialRefresh;
   }

   public Boolean getDnD()
   {
      return dnd;
   }

   public boolean isDnDEnabled()
   {
      return dnd != null && dnd.booleanValue();
   }

   public Boolean getPartialRefresh()
   {
      return partialRefresh;
   }

   public boolean isPartialRefreshEnabled()
   {
      return partialRefresh != null && partialRefresh.booleanValue();
   }
}
