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
 * Type safe enumeration of allowed orientations.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 */
public final class Orientation
{

   /** Vertical. */
   public static final Orientation VERTICAL = new Orientation("vertical");

   /** Horizontal. */
   public static final Orientation HORIZONTAL = new Orientation("horizontal");

   /** The default value which is vertical. */
   public static final Orientation DEFAULT = VERTICAL;

   /** The literal value. */
   private final String value;

   private Orientation(String value)
   {
      if (value == null)
      {
         throw new IllegalArgumentException();
      }
      this.value = value;
   }

   public String toString()
   {
      return value;
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o instanceof Orientation)
      {
         Orientation that = (Orientation)o;
         return this.value.equals(that.value);
      }
      return false;
   }

   public int hashCode()
   {
      return value.hashCode();
   }

   /**
    * Parse a string representation of an orientation into a defined type.
    *
    * @param orientation the string representation of the orientation
    * @return the defined type for the string
    * @throws IllegalArgumentException if the provided orientation String is invalid
    */
   public static Orientation parse(String orientation)
   {
      if (orientation != null)
      {
         if (orientation.equalsIgnoreCase(VERTICAL.toString()))
         {
            return VERTICAL;
         }
         if (orientation.equalsIgnoreCase(HORIZONTAL.toString()))
         {
            return HORIZONTAL;
         }
      }

      //
      throw new IllegalArgumentException("Invalid orientation: " + orientation);
   }
}
