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

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DynaMergeBehavior
{

   public static boolean mergeForRegion(Boolean page, Boolean region)
   {
      return Boolean.TRUE.equals(page) & !Boolean.FALSE.equals(region);
   }

   public static DynaRenderOptions mergeForRegion(DynaRenderOptions page, DynaRenderOptions region)
   {
      boolean dnd = mergeForRegion(page.getDnD(), region.getDnD());
      boolean partialRefresh = mergeForRegion(page.getPartialRefresh(), region.getPartialRefresh());
      return DynaRenderOptions.getOptions(dnd, partialRefresh);
   }

   public static boolean mergeForWindow(Boolean region, Boolean window)
   {
      return Boolean.TRUE.equals(region) & !Boolean.FALSE.equals(window);
   }

   public static DynaRenderOptions mergeForWindow(DynaRenderOptions region, DynaRenderOptions window)
   {
      boolean dnd = mergeForWindow(region.getDnD(), window.getDnD());
      boolean partialRefresh = mergeForWindow(region.getPartialRefresh(), window.getPartialRefresh());
      return DynaRenderOptions.getOptions(dnd, partialRefresh);
   }

}
