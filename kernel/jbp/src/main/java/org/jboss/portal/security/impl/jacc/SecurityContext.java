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
package org.jboss.portal.security.impl.jacc;

import java.security.Permission;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/** @author <a href="mailto:sshah@redhat.com">Sohil Shah</a> */
public class SecurityContext
{
   private Permissions uncheckedPermissions = null;
   private HashMap rolePermissions = null;

   /**
    *
    *
    */
   SecurityContext()
   {
      this.uncheckedPermissions = new Permissions();
      this.rolePermissions = new HashMap();
   }

   /**
    * @param roleName
    * @param permission
    */
   void addToRole(String roleName, Permission permission)
   {
      Permissions perms = (Permissions)this.rolePermissions.get(roleName);
      if (perms == null)
      {
         perms = new Permissions();
         this.rolePermissions.put(roleName, perms);
      }
      perms.add(permission);
   }


   /** @param permission  */
   void addToUncheckedPolicy(Permission permission)
   {
      this.uncheckedPermissions.add(permission);
   }

   /**
    * @param domain
    * @param permission
    * @return
    */
   boolean implies(ProtectionDomain domain, Permission permission)
   {
      boolean implied = false;

      // Next see if this matches an unchecked permission
      if (uncheckedPermissions.implies(permission))
      {
         return true;
      }

      // Check principal to role permissions
      Principal[] principals = domain.getPrincipals();
      int length = principals != null ? principals.length : 0;
      ArrayList princpalNames = new ArrayList();
      for (int n = 0; n < length; n++)
      {
         Principal p = principals[n];
         if (p instanceof Group)
         {
            Group g = (Group)p;
            Enumeration iter = g.members();
            while (iter.hasMoreElements())
            {
               p = (Principal)iter.nextElement();
               String name = p.getName();
               princpalNames.add(name);
            }
         }
         else
         {
            String name = p.getName();
            princpalNames.add(name);
         }
      }
      if (princpalNames.size() > 0)
      {
         for (int n = 0; implied == false && n < princpalNames.size(); n++)
         {
            String name = (String)princpalNames.get(n);
            Permissions perms = (Permissions)rolePermissions.get(name);

            if (perms == null)
            {
               continue;
            }
            implied = perms.implies(permission);
         }
      }

      return implied;
   }
}
