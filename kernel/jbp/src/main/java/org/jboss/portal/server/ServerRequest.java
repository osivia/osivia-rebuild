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
package org.jboss.portal.server;

import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ServerRequest
{

   /** The locales for the scope of the current request. */
   protected Locale[] locales;

   /** The server used during the request. */
   protected Server server;

   /** . */
   protected ServerInvocationContext invocationContext;

   public ServerRequest(ServerInvocationContext invocationContext)
   {
      this.invocationContext = invocationContext;
   }

   public Server getServer()
   {
      return server;
   }

   public void setServer(Server server)
   {
      this.server = server;
   }

   /**
    * Return the first locale in the locale list or null if the list is empty.
    *
    * @return the first locale in the list
    * @throws IllegalArgumentException if the locales field is null
    */
   public Locale getLocale() throws IllegalStateException
   {
      if (locales == null)
      {
         throw new IllegalStateException("No locales have been defined in this request");
      }
      if (locales.length == 0)
      {
         return null;
      }
      return locales[0];
   }

   /**
    * Return the locales for the scope of the current request. It can be based on the locale decided by the web browser
    * available in the original HTTP request or can be based on the current authenticated user that has decided a
    * specific locale.
    *
    * @return the request locale
    */
   public Locale[] getLocales()
   {
      return locales;
   }

   public void setLocales(Locale[] locales)
   {
      this.locales = locales;
   }
}
