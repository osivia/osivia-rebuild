/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.portal.identity;

import org.jboss.portal.identity.User;

import java.io.Serializable;

/**
 * Simple POJO to cache user data.
 *
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class CachedUserImpl implements User, Serializable
{

   /** . */
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(CachedUserImpl.class);

   /** . */
   private Object id;

   /** . */
   private String name;

   public CachedUserImpl(Object id, String name)
   {
      this.id = id;
      this.name = name;
   }

   public Object getId()
   {
      return id;
   }

   public String getUserName()
   {
      return name;
   }

   public void updatePassword(String password)
   {
      throw new UnsupportedOperationException("Cached user. Password cannot be updated using this object. Obtain User using UserModule.");
   }

   public boolean validatePassword(String password)
   {
      throw new UnsupportedOperationException("Cached user. Password cannot be validated using this object. Obtain User using UserModule.");
   }
}
