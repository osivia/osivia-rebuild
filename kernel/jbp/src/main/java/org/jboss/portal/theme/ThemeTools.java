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
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ThemeTools
{

   /**
    * Compare 2 windows.
    *
    * @param orderString1
    * @param id1
    * @param orderString2
    * @param id2
    * @return
    */
   public static int compareWindowOrder(
      String orderString1,
      String id1,
      String orderString2,
      String id2)
   {
      if (id1 == null)
      {
         throw new IllegalArgumentException("No null window id accepted for window 1");
      }
      if (id2 == null)
      {
         throw new IllegalArgumentException("No null window id accepted for window 2");
      }

      //
      if (orderString1 == null)
      {
         orderString1 = "";
      }
      if (orderString2 == null)
      {
         orderString1 = "";
      }

      int order1 = 0;
      int order2 = 0;

      //
      try
      {
         order1 = Integer.parseInt(orderString1);

         //
         try
         {
            order2 = Integer.parseInt(orderString2);
         }
         catch (NumberFormatException e)
         {
            // We have window2>window1
            order2 = order1 + 1;
         }
      }
      catch (NumberFormatException e1)
      {
         try
         {
            Integer.parseInt(orderString2);

            // We have order2=0 and order1=1 and thus window1>window2
            order1 = 1;
         }
         catch (NumberFormatException e2)
         {
            // Orders have the same value of zero that will lead to the comparison of their ids
         }
      }

      // If order are the same we compare the window ids
      if (order1 == order2)
      {
         return orderString1.compareTo(orderString2);
      }
      else
      {
         return order1 - order2;
      }
   }
}
