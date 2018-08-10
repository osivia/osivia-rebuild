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

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class RoleUpdatedEvent extends IdentityEvent
{
   /** . */
   private final Object roleId;

   /** . */
   private final String roleName;

   /** . */
   private String displayName;


   public RoleUpdatedEvent(Object roleId, String roleName, String displayName)
   {

      this.displayName = displayName;
      if (roleId == null)
      {
         throw new IllegalArgumentException();
      }
      if (roleName == null)
      {
         throw new IllegalArgumentException();
      }
      if (displayName == null)
      {
         throw new IllegalArgumentException();
      }


      this.roleId = roleId;
      this.roleName = roleName;
      this.displayName = displayName;
      
   }

   public Object getRoleId()
   {
      return roleId;
   }

   public String getRoleName()
   {
      return roleName;
   }

   public String getDisplayName()
   {
      return displayName;
   }

   public String toString()
   {
      return "RoleUpdatedEvent[roleId=" + roleId + ",roleName=" + roleName + ",displayName" + displayName + "]";
   }

}
