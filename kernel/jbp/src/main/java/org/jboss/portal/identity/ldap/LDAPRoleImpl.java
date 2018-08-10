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
package org.jboss.portal.identity.ldap;

import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.User;

import java.util.Comparator;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPRoleImpl implements Role
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPRoleImpl.class);

   private IdentityContext identityContext;

   private String dn;

   private String name;

   private String id;

   private String displayName;

   private LDAPRoleModule roleModule;

   private LDAPRoleImpl()
   {

   }

   protected LDAPRoleImpl(String dn, IdentityContext context, String id, String name, String displayName)
   {
      if (dn == null)
      {
         throw new IllegalArgumentException("LDAPRoleImpl need to be aware of its DN");
      }


      if (context == null)
      {
         throw new IllegalArgumentException("IdentityContext can't be null");
      }

      if (id == null)
      {
         throw new IllegalArgumentException("Id can't be null");
      }

      if (name == null)
      {
         throw new IllegalArgumentException("Name can't be null");
      }

      if (displayName == null)
      {
         throw new IllegalArgumentException("DisplayName can't be null");
      }

      this.identityContext = context;
      this.id = id;
      this.name = name;
      this.displayName = displayName;
      this.dn = dn;
   }


   public boolean equals(Object obj)
   {
      if (!(obj instanceof Role))
      {
         return super.equals(obj);
      }


      Role r = (Role)obj;
      if (r.getId().toString().equals(getId().toString()))
      {
         return true;
      }
      return false;
   }

   public int hashCode()
   {
      return id.hashCode() * 13 + 5;
   }

   public String getName()
   {
      return this.name;
   }

   public String getDisplayName()
   {
      return displayName;
   }

   public void setDisplayName(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("DisplayName is null");
      }
      try
      {
         getRoleModule().updateDisplayName(this, name);
         this.displayName = name;
      }
      catch (IdentityException e)
      {
         log.debug("Unable to update role displayName: ", e);
      }
   }

   
//   public Set getUsers()
//   {
//      try
//      {
//         MembershipModule mm = (MembershipModule)identityContext.getObject(IdentityContext.TYPE_MEMBERSHIP_MODULE);
//         return mm.getUsers(this);
//      }
//      catch (IdentityException e)
//      {
//         log.error("Unable to delegate method to MembershipModule: ", e);
//      }
//      return null;
//   }

   protected LDAPRoleModule getRoleModule() throws IdentityException
   {

      if (roleModule == null)
      {
         try
         {
            this.roleModule = (LDAPRoleModule)identityContext.getObject(IdentityContext.TYPE_ROLE_MODULE);
         }
         catch (ClassCastException e)
         {
            throw new IdentityException("Not supported object as part of the context", e);
         }
      }
      return roleModule;
   }


   //**************************
   //*** Getter and Setters
   //**************************
   public String getDn()
   {
      return dn;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public Object getId()
   {
      return id;
   }

   public static class LDAPRoleComparator implements Comparator
   {


      public int compare(Object o1, Object o2)
      {
         Role r1 = (Role)o1;
         Role r2 = (Role)o2;

         String name1 = r1.getName();
         String name2 = r2.getName();

         return name1.compareToIgnoreCase(name2);
      }
   }
}
