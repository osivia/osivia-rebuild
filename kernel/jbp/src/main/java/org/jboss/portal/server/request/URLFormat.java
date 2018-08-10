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
package org.jboss.portal.server.request;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class URLFormat
{

   /** . */
   public static final int RELATIVE_MASK = 0x01;

   /** . */
   public static final int SERVLET_ENCODED_MASK = 0x02;

   /** . */
   private static final URLFormat[] formats = new URLFormat[]
      {
         new URLFormat(false, false),
         new URLFormat(true, false),
         new URLFormat(false, true),
         new URLFormat(true, true),
      };

   /** . */
   private final int mask;

   /** . */
   private final boolean relative;

   /** . */
   private final boolean servletEncoded;

   private URLFormat(boolean relative, boolean encoded)
   {
      this.mask = (relative ? RELATIVE_MASK : 0) | (encoded ? SERVLET_ENCODED_MASK : 0);
      this.relative = relative;
      this.servletEncoded = encoded;
   }

   public boolean isRelative()
   {
      return relative;
   }

   public boolean isServletEncoded()
   {
      return servletEncoded;
   }

   public int getMask()
   {
      return mask;
   }

   public static URLFormat newInstance(int mask)
   {
      return formats[mask];
   }

   public static URLFormat newInstance(boolean relative, boolean encoded)
   {
      int mask = (relative ? RELATIVE_MASK : 0) | (encoded ? SERVLET_ENCODED_MASK : 0);
      return formats[mask];
   }
}
