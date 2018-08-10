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
 * URL information that does not belong to the url value itself but rather to the context it represents for this URL.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class URLContext
{

   /** Mask for secure bit. */
   public static final int SEC_MASK = 0x01;

   /** Mask for authenticated bit. */
   public static final int AUTH_MASK = 0x02;

   /** . */
   private final int mask;

   /** . */
   private final boolean secure;

   /** . */
   private final boolean authenticated;

   /** . */
   private static final URLContext[] contexts = new URLContext[]
      {
         new URLContext(false, false),
         new URLContext(true, false),
         new URLContext(false, true),
         new URLContext(true, true),
      };

   private URLContext(boolean secure, boolean authenticated)
   {
      this.mask = (secure ? SEC_MASK : 0) | (authenticated ? AUTH_MASK : 0);
      this.secure = secure;
      this.authenticated = authenticated;
   }

   public boolean isSecure()
   {
      return secure;
   }

   public boolean isAuthenticated()
   {
      return authenticated;
   }

   public URLContext withSecured(Boolean wantSecure)
   {
      if (wantSecure == null)
      {
         return this;
      }
      else if (Boolean.TRUE == wantSecure)
      {
         return asSecured();
      }
      else
      {
         return asNonSecured();
      }
   }

   public URLContext asSecured()
   {
      int newMask = mask | SEC_MASK;
      return contexts[newMask];
   }

   public URLContext asNonSecured()
   {
      int newMask = mask & ~SEC_MASK;
      return contexts[newMask];
   }

   public URLContext withAuthenticated(Boolean wantAuthenticated)
   {
      if (wantAuthenticated == null)
      {
         return this;
      }
      else if (Boolean.TRUE == wantAuthenticated)
      {
         return asAuthenticated();
      }
      else
      {
         return asNonAuthenticated();
      }
   }

   public URLContext asAuthenticated()
   {
      int newMask = mask | AUTH_MASK;
      return contexts[newMask];
   }

   public URLContext asNonAuthenticated()
   {
      int newMask = mask & ~AUTH_MASK;
      return contexts[newMask];
   }

   public int getMask()
   {
      return mask;
   }

   public static URLContext newInstance(int mask)
   {
      return contexts[mask];
   }

   public static URLContext newInstance(boolean secure, boolean authenticated)
   {
      int mask = (secure ? SEC_MASK : 0) | (authenticated ? AUTH_MASK : 0);
      return contexts[mask];
   }
}
