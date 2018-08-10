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

package org.jboss.portal.identity.event;

import java.util.Set;
import java.util.Iterator;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class MembershipChangedEvent extends IdentityEvent
{
   private final Set userIds;

   private final Set roleIds;

   private final String representation;

   public MembershipChangedEvent(Set userIds, Set roleIds)
   {
      if (userIds == null)
      {
         throw new IllegalArgumentException();
      }

      if (roleIds == null)
      {
         throw new IllegalArgumentException();
      }

      if (roleIds.size() > 1 && userIds.size() > 1)
      {
         throw new IllegalStateException("Either roleIds or userIds must contain only one element");
      }

      this.userIds = userIds;
      this.roleIds = roleIds;

      StringBuilder sb = new StringBuilder();

      sb.append("MembershipChangedEvent[userIds=");
      for (Iterator iterator = userIds.iterator(); iterator.hasNext();)
      {
         Object o = iterator.next();
         if (iterator.hasNext())
         {
            sb.append(o.toString() + ", ");
         }
         else
         {
            sb.append(o.toString());
         }
      }
      representation = sb.toString();
   }

   public Set getUserIds()
   {
      return userIds;
   }

   public Set getRoleIds()
   {
      return roleIds;
   }

   public String toString()
   {
      return representation;
   }
}
