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

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.Role;
import org.jboss.portal.common.util.Tools;

import java.security.NoSuchAlgorithmException;
import java.util.Comparator;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPUserImpl implements User
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPUserImpl.class);

   //this is to enable user act like a fasade to identity modules calls
   IdentityContext identityContext;

   private UserProfileModule userProfileModule;

   private String dn;

   private String userName;

   //In ldap implementation it acts as a userName
   private String id;

   //private String password;

   //private String realEmail;

   LDAPUserModule userModule;

   /**
    * internal
    */
   private LDAPUserImpl()
   {

   }

   /**
    * Creates a ldap user implementation instance
    *
    * @param dn
    * @param context
    * @throws IdentityException
    */
   protected LDAPUserImpl(String dn, IdentityContext context, String id) throws IdentityException
   {
      if (dn == null)
      {
         throw new IdentityException("Cannot create LDAPUserImpl without DN");
      }
      this.dn = dn;

      if (context == null)
      {
         throw new IllegalArgumentException("IdentityContext can't be null");
      }

//      if (password == null)
//      {
//         throw new IllegalArgumentException("Password can't be null");
//      }

      if (id == null)
      {
         throw new IllegalArgumentException("Id can't be null");
      }

      this.identityContext = context;
      //this.realEmail = email;
      this.id = id;
      
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof User))
      {
         return super.equals(obj);
      }


      User u = (User)obj;
      if (u.getId().toString().equals(getId().toString()))
      {
         return true;
      }
      return false;
   }

   public int hashCode()
   {
      return id.hashCode()*13 + 5;
   }

   public void updatePassword(String password) throws IdentityException
   {
      if (password == null)
      {
         throw new IllegalArgumentException("Password is null");
      }
      try
      {
         getUserModule().updatePassword(this, password);
      }
      catch (IdentityException e)
      {
         log.debug("Password update failure: " + e);
         throw new IdentityException("Password update failure: " + e);
      }
   }

   public boolean validatePassword(String password)
   {
      if (password == null)
      {
         throw new IllegalArgumentException("Password is null");
      }
      try
      {
         return getUserModule().validatePassword(this,password);
      }
      catch (IdentityException e)
      {
         log.debug("Password validation failure: " + e);
      }
      return false;
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

   public UserProfileModule getUserProfileModule()
   {
      return userProfileModule;
   }

   public void setUserProfileModule(UserProfileModule userProfileModule)
   {
      this.userProfileModule = userProfileModule;
   }


   public String getUserName()
   {
      return this.userName;
   }

   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   private IdentityConfiguration getIdentityConfiguration() throws IdentityException
   {
      return (IdentityConfiguration)identityContext.getObject(IdentityContext.TYPE_IDENTITY_CONFIGURATION);
   }

   protected LDAPUserModule getUserModule() throws IdentityException
   {

      if (userModule == null)
      {
         try
         {
            this.userModule = (LDAPUserModule)identityContext.getObject(IdentityContext.TYPE_USER_MODULE);
         }
         catch (ClassCastException e)
         {
            throw new IdentityException("Not supported object as part of the context - must be LDAPUserModule", e);
         }
      }
      return userModule;
   }

   public static class LDAPUserComparator implements Comparator
   {


      public int compare(Object o1, Object o2)
      {
         User u1 = (User)o1;
         User u2 = (User)o2;

         String name1 = u1.getUserName();
         String name2 = u2.getUserName();

         return name1.compareToIgnoreCase(name2);
      }
   }

}
