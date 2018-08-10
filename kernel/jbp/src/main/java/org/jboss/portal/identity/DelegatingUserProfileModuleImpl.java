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

import org.jboss.portal.identity.service.UserProfileModuleService;
import org.jboss.portal.identity.info.PropertyInfo;
import org.jboss.portal.identity.info.ProfileInfo;
import org.jboss.portal.identity.ldap.LDAPUserImpl;

import org.jboss.portal.identity.User;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.CachedUserImpl;


import javax.naming.InitialContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class DelegatingUserProfileModuleImpl extends UserProfileModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(DelegatingUserProfileModuleImpl.class);

   private String ldapModuleJNDIName;

   private String dbModuleJNDIName;

   private UserProfileModule LDAPModule;

   private UserProfileModule dbModule;

   public void start() throws Exception
   {
      super.start();

      if(getProfileInfo() == null)
      {
         throw new IdentityException("No profile information found. Check the configuration.");
      }

      //check if we don't delegate to ourselves:
      if (getJNDIName().equals(getDbModuleJNDIName()))
      {
         throw new IdentityException("Cannot delegate to itself - correct dbModuleJNDIName option");
      }
      if (getJNDIName().equals(getLdapModuleJNDIName()))
      {
         throw new IdentityException("Cannot delegate to itself - correct ldapModuleJNDIName option");
      }
   }

   public Object getProperty(User user, String propertyName) throws IdentityException, IllegalArgumentException
   {
      if (log.isDebugEnabled()) log.debug("getProperty: " + propertyName);
      try
      {
         PropertyInfo property = getProfileInfo().getPropertyInfo(propertyName);
         if (property == null)
         {
            throw new IdentityException("Such property name is not supported: " + propertyName);
         }

         if (user instanceof CachedUserImpl)
         {
            user = obtainUser(user);
         }

         if (property.isMappedLDAP() && isLDAPSupported() && user instanceof LDAPUserImpl)
         {
            log.debug("Delegating to LDAP module");

            return getLDAPModule().getProperty(user, propertyName);
         }
         else if (property.isMappedDB())
         {
            log.debug("Delegating to DB module");
            return getDBModule().getProperty(user, propertyName);
         }
         throw new IdentityException("Cannot process property - incorrect profile or module configuration");
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot resolve property: " + propertyName, e);
      }
   }

   public void setProperty(User user, String name, Object propertyValue) throws IdentityException, IllegalArgumentException
   {
      if (log.isDebugEnabled()) log.debug("setProperty: " + name + "/" + propertyValue);
      try
      {
         PropertyInfo property = getProfileInfo().getPropertyInfo(name);
         if (property == null)
         {
            throw new IdentityException("Such property name is not supported: " + name);
         }

         if (user instanceof CachedUserImpl)
         {
            user = obtainUser(user);
         }

         if (property.isMappedLDAP() && isLDAPSupported() && user instanceof LDAPUserImpl)
         {
            log.debug("Delegating to LDAP module");
            getLDAPModule().setProperty(user, name, propertyValue);
            return;
         }
         else if (property.isMappedDB())
         {
            log.debug("Delegating to DB module");

            getDBModule().setProperty(user, name, propertyValue);
            fireUserProfileChangedEvent(user.getId(), user.getUserName(), name, propertyValue);
            return;
         }
         throw new IdentityException("Cannot process property - incorrect profile or module configuration");
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot resolve property: " + name, e);
      }
   }

   public Map getProperties(User user) throws IdentityException, IllegalArgumentException
   {
      if (log.isDebugEnabled()) log.debug("getProperties");//: " + name + "/" + propertyValue)
      try
      {
         if (user instanceof CachedUserImpl)
         {
            user = obtainUser(user);
         }

         if (user instanceof LDAPUserImpl && isLDAPSupported())
         {
            log.debug("handling LDAP user implementation");

            Map props = new HashMap();

            UserProfileModule dbModule = null;
            UserProfileModule ldapModule = null;

            // First get props from databas - its ok if it fails as database support is not required for LDAP configuration
            try
            {
               dbModule = getDBModule();
               props.putAll(dbModule.getProperties(user));
            }
            catch (Exception e)
            {
               //
            }

            ldapModule = getLDAPModule();
            props.putAll(ldapModule.getProperties(user));


            return Collections.unmodifiableMap(props);
         }
         }
      catch (Exception e)
      {
         throw new IdentityException("Cannot resolve properties: ", e);
      }
      return null;
   }

   private User obtainUser(User user) throws IdentityException
   {
      UserModule um = (UserModule)getIdentityContext().getObject(IdentityContext.TYPE_USER_MODULE);
      return um.findUserById(user.getId());
   }


   public ProfileInfo getProfileInfo() throws IdentityException
   {
      return profileInfo;
   }

   public String getLdapModuleJNDIName()
   {
      return ldapModuleJNDIName;
   }

   public void setLdapModuleJNDIName(String ldapModuleJNDIName)
   {
      this.ldapModuleJNDIName = ldapModuleJNDIName;
   }

   public String getDbModuleJNDIName()
   {
      return dbModuleJNDIName;
   }

   public void setDbModuleJNDIName(String dbModuleJNDIName)
   {
      this.dbModuleJNDIName = dbModuleJNDIName;
   }

   public boolean isLDAPSupported()
   {
      return getLdapModuleJNDIName()!=null;
   }

   public boolean isDBSupported()
   {
      return getDbModuleJNDIName()!=null;
   }

   protected UserProfileModule getDBModule() throws Exception
   {

      //TODO: to tired to clean this at the moment
      if (dbModule == null)
      {
         dbModule = (UserProfileModule)new InitialContext().lookup(getDbModuleJNDIName());
         if (dbModule == null)
         {
            throw new IdentityException("Couldn't obtain DB UserProfileModule");
         }
      }
      return dbModule;
   }

   protected UserProfileModule getLDAPModule() throws Exception
   {
      if (LDAPModule == null)
      {
         LDAPModule = (UserProfileModule)new InitialContext().lookup(getLdapModuleJNDIName());
         if (LDAPModule == null)
         {
            throw new IdentityException("Couldn't obtain LDAP UserProfileModule");
         }
      }
      return LDAPModule;
   }


   public void setLDAPModule(UserProfileModule LDAPModule)
   {
      this.LDAPModule = LDAPModule;
   }

   public void setDbModule(UserProfileModule dbModule)
   {
      this.dbModule = dbModule;
   }
}
