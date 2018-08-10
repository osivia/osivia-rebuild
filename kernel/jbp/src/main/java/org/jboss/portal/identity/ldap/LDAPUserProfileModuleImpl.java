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
package org.jboss.portal.identity.ldap;

import org.jboss.portal.identity.CachedUserImpl;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.info.ProfileInfo;
import org.jboss.portal.identity.info.PropertyInfo;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPUserProfileModuleImpl extends LDAPUserProfileModule
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPUserProfileModuleImpl.class);

   private UserModule userModule;

   public Object getProperty(User user, String propertyName) throws IdentityException, IllegalArgumentException
   {
      if (user == null)
      {
         throw new IllegalArgumentException("User cannot be null");
      }
      if (propertyName == null)
      {
         throw new IllegalArgumentException("Property name need to have value");
      }

      if (user instanceof CachedUserImpl)
      {
         try
         {
            user = getUserModule().findUserById(user.getId());
         }
         catch(NoSuchUserException e)
         {
            throw new IdentityException("Illegal state - cached user doesn't exist in identity store: ", e);
         }
      }

      LDAPUserImpl ldapUser = null;

      if (user instanceof LDAPUserImpl)
      {
         ldapUser = (LDAPUserImpl)user;
      }
      else
      {
         throw new IllegalArgumentException("This UserProfileModule implementation supports only LDAPUserImpl objects");
      }

      String attributeName = resolveAttributeName(propertyName);
      Object propertyValue = null;

      if (attributeName == null)
      {
         log.error("Proper LDAP attribute mapping not found for such property name: " + propertyName);
         return null;
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         Attributes attrs = ldapContext.getAttributes(ldapUser.getDn());

         Attribute attr = attrs.get(attributeName);

         if (attr != null)
         {
            propertyValue = attr.get();
         }
         else
         {
            log.error("No such attribute ('" + attributeName + "') in entry: " + ldapUser.getDn());
         }
      }
      catch (NamingException e)
      {
         throw new IdentityException("Cannot get user property value.", e);
      }
      finally
      {
         try
         {
            ldapContext.close();
         }
         catch (NamingException e)
         {
            throw new IdentityException("Failed to close LDAP connection", e);
         }
      }

      PropertyInfo pi = getProfileInfo().getPropertyInfo(propertyName);


      if (propertyValue != null && !pi.getType().equals(propertyValue.getClass().getName()))
      {
         log.error("Error on processing property:" + propertyName);
         log.error("Wrong property type retreived from LDAP. Should be: " + pi.getType() + "; and found: " + propertyValue.getClass().getName());
      }

      return propertyValue;

   }

   public void setProperty(User user, String propertyName, Object property) throws IdentityException, IllegalArgumentException
   {
      if (user == null)
      {
         throw new IllegalArgumentException("User cannot be null");
      }
      if (propertyName == null)
      {
         throw new IllegalArgumentException("Property name need to have value");
      }

      if (user instanceof CachedUserImpl)
      {
         try
         {
            user = getUserModule().findUserById(user.getId());
         }
         catch(NoSuchUserException e)
         {
            throw new IdentityException("Illegal state - cached user doesn't exist in identity store: ", e);
         }
      }

      LDAPUserImpl ldapUser = null;

      if (user instanceof LDAPUserImpl)
      {
         ldapUser = (LDAPUserImpl)user;
      }
      else
      {
         throw new IllegalArgumentException("This UserProfileModule implementation support only LDAPUserImpl objects");
      }

      String attributeName = resolveAttributeName(propertyName);

      PropertyInfo pi = getProfileInfo().getPropertyInfo(propertyName);

      if (pi.getAccessMode().equals(PropertyInfo.ACCESS_MODE_READ_ONLY))
      {
         throw new IdentityException("Property has read only access - cannot set: " + propertyName);
      }

      if (property != null && !pi.getType().equals(property.getClass().getName()))
      {
         throw new IdentityException("Wrong property type. Must be: " + pi.getType() + "; and found: " + property.getClass().getName());
      }


      //TODO: check the type and log.info that this can only be a String if not such
      //String propertyValue = property.toString();

      if (attributeName == null)
      {
         log.error("Proper LDAP attribute mapping not found for such property name: " + propertyName);
         return;
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         //TODO: maybe perform a schema check if this attribute is allowed for such entry

         Attributes attrs = new BasicAttributes(true);
         Attribute attr = new BasicAttribute(attributeName);
         attr.add(property);
         attrs.put(attr);

         ldapContext.modifyAttributes(ldapUser.getDn(), DirContext.REPLACE_ATTRIBUTE,attrs);
         fireUserProfileChangedEvent(user.getId(), user.getUserName(), propertyName, property);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Cannot set user property value.", e);
      }
      finally
      {
         try
         {
            ldapContext.close();
         }
         catch (NamingException e)
         {
            throw new IdentityException("Failed to close LDAP connection", e);
         }
      }

   }

   public Map getProperties(User user) throws IdentityException, IllegalArgumentException
   {
      if (user == null)
      {
         throw new IllegalArgumentException("User cannot be null");
      }

      if (user instanceof CachedUserImpl)
      {
         try
         {
            user = getUserModule().findUserById(user.getId());
         }
         catch(NoSuchUserException e)
         {
            throw new IdentityException("Illegal state - cached user doesn't exist in identity store: ", e);
         }
      }

      LDAPUserImpl ldapUser = null;

      if (user instanceof LDAPUserImpl)
      {
         ldapUser = (LDAPUserImpl)user;
      }
      else
      {
         throw new IllegalArgumentException("This UserProfileModule implementation support only LDAPUserImpl objects");
      }

      Map propertyMap = new HashMap();

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         Map mappings = resolveAttributesMappingMap();

         Set props = mappings.keySet();

         Attributes attrs = ldapContext.getAttributes(ldapUser.getDn());

         for (Iterator iterator = props.iterator(); iterator.hasNext();)
         {
            String name = (String)iterator.next();
            String attrName = (String)mappings.get(name);
            Attribute attr = attrs.get(attrName);

            if (attr != null)
            {
               propertyMap.put(name,attr.get());
               PropertyInfo pi = getProfileInfo().getPropertyInfo(name);
               if (attr.get() != null && !pi.getType().equals(attr.get().getClass().getName()))
               {
                  log.error("Error on processing property:" + name);
                  log.error("Wrong property type retreived from LDAP. Should be: " + pi.getType() + "; and found: " + attr.get().getClass().getName());
               }
            }
            else
            {
               log.error("No such attribute ('" + attrName + "') in entry: " + ldapUser.getDn());
            }
         }
      }
      catch (NamingException e)
      {
         throw new IdentityException("Cannot get user property value.", e);
      }
      finally
      {
         try
         {
            ldapContext.close();
         }
         catch (NamingException e)
         {
            throw new IdentityException("Failed to close LDAP connection", e);
         }
      }

      return Collections.unmodifiableMap(propertyMap);
   }

   private String resolveAttributeName(String propertyName) throws IdentityException
   {
      //return getIdentityConfiguration().getValue(IdentityConfiguration.GROUP_USER_PROFILE_MAPPINGS, propertyName);

      PropertyInfo pi = getProfileInfo().getPropertyInfo(propertyName);

      if (pi == null)
      {
         throw new IdentityException("Cannot find profile information about property: " + propertyName);
      }

      String mapping = pi.getMappingLDAPValue();
      if (mapping == null)
      {
         throw new IdentityException("This property is not mapped as LDAP attribute: " + propertyName);
      }
      return mapping;
   }

   /**
    * Returns a map of mappings - property name/attribute name. 
    * @return
    * @throws IdentityException
    */
   private Map resolveAttributesMappingMap() throws IdentityException
   {
      //Map group = getIdentityConfiguration().getOptions(IdentityConfiguration.GROUP_USER_PROFILE_MAPPINGS);


      Map infos = getProfileInfo().getPropertiesInfo();
      Set keys = infos.keySet();

      Map mappings = new HashMap();
      for (Iterator iterator = keys.iterator(); iterator.hasNext();)
      {
         String key = (String)iterator.next();
         PropertyInfo prop = (PropertyInfo)infos.get(key);
         if (prop.isMappedLDAP())
         {
            mappings.put(prop.getName(), prop.getMappingLDAPValue());
         }
      }
      return mappings;
   }


   /**
    * obtains UserProfile object - if module is used as a Delegate it tries to obtain it from the main one.
    * @return
    * @throws IdentityException
    */
   public ProfileInfo getProfileInfo() throws IdentityException
   {


      if (profileInfo == null)
      {
         //obtain main UserProfileModule
         UserProfileModule module = (UserProfileModule)getIdentityContext().getObject(IdentityContext.TYPE_USER_PROFILE_MODULE);
         if (module == this)
         {
            throw new IdentityException("ProfileInfo not accessible - check configuration");
         }
         else
         {
            setProfileInfo(module.getProfileInfo());
         }
      }
      return profileInfo;
   }

   protected UserModule getUserModule() throws IdentityException
   {
      if (userModule == null)
      {
         try
         {
            this.userModule = (UserModule)getIdentityContext().getObject(IdentityContext.TYPE_USER_MODULE);
         }
         catch (ClassCastException e)
         {
            throw new IdentityException("Not supported object as part of the context - must be UserModule", e);
         }
      }
      return userModule;
   }


}
