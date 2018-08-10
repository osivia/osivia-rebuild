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

import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.service.UserModuleService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.io.UnsupportedEncodingException;

/**
 * Abstract LDAPUserModule that should be extended to provide compabitibility across identity modules
 *
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class LDAPUserModule extends UserModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPUserModule.class);

   private LDAPConnectionContext connectionContext;

   private UserProfileModule userProfileModule;


   public void start() throws Exception
   {
      if (getConnectionJNDIName() == null)
      {
         throw new IdentityException("Cannot obtain ldap connection context JNDI name");
      }

      try
      {
         connectionContext = (LDAPConnectionContext)new InitialContext().lookup(getConnectionJNDIName());
      }
      catch (NamingException e)
      {
         log.error("Couldn't obtain connection context");
      }

      super.start();    //To change body of overridden methods use File | Settings | File Templates.
   }

   public void updatePassword(LDAPUserImpl ldapu, String password) throws IdentityException
   {
      if ((password == null || password.length() == 0) && !isAllowEmptyPasswords())
      {
         throw new IdentityException("Cannot update password with empty value - please set proper option to allow this");
      }

      String attributeName = getPasswordAttributeId();

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      String passwordString = password;

      if (getEnclosePasswordWith() != null)
      {
         String enc = getEnclosePasswordWith();
         passwordString = enc + passwordString + enc;
      }

      byte[] encodedPassword = null;

      if (getPasswordEncoding() != null && passwordString != null)
      {
         try
         {
            encodedPassword = passwordString.getBytes(getPasswordEncoding());
         }
         catch (UnsupportedEncodingException e)
         {
            throw new IdentityException("Error while encoding password with configured setting: " + getPasswordEncoding(),
               e);
         }
      }

      



      try
      {
         //TODO: maybe perform a schema check if this attribute is allowed for such entry

         Attributes attrs = new BasicAttributes(true);
         Attribute attr = new BasicAttribute(attributeName);
         if (encodedPassword != null)
         {

            attr.add(encodedPassword);
         }
         else
         {
            attr.add(passwordString);
         }
         attrs.put(attr);

         if(getUpdatePasswordAttributeValues() != null && getUpdatePasswordAttributeValues().size() > 0)
         {
            Map<String, Set<String>>  attributesToAdd = getUpdatePasswordAttributeValues();
            for (Map.Entry<String, Set<String>> entry : attributesToAdd.entrySet())
            {
               Attribute additionalAttr = new BasicAttribute(entry.getKey());
               for (String val : entry.getValue())
               {
                  additionalAttr.add(val);
               }
               attrs.put(additionalAttr);
            }

         }

         ldapContext.modifyAttributes(ldapu.getDn(), DirContext.REPLACE_ATTRIBUTE,attrs);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Cannot set user password value.", e);
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



   public boolean validatePassword(LDAPUserImpl ldapu, String password) throws IdentityException
   {

      // Depending on configuration reject authentication with empty password
      if ((password == null || password.length() == 0) & !isAllowEmptyPasswords())
      {
         return false;
      }

      //will use user DN to bind checking the password by default
      String principal = ldapu.getDn();

      //if  principalDNPreffix or principalDNSuffix is set will use them to construct principal name
      if (getPrincipalPreffix() != null || getPrincipalSuffix() != null)
      {
         String preffix = getPrincipalPreffix();
         if (preffix == null)
         {
            preffix = "";
         }
         String suffix = getPrincipalSuffix();
         if (suffix == null)
         {
            suffix = "";
         }

         principal = preffix + ldapu.getUserName() + suffix;
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {

         Hashtable env = ldapContext.getEnvironment();

         env.put(Context.SECURITY_PRINCIPAL, principal);
         env.put(Context.SECURITY_CREDENTIALS, password);

         InitialContext ctx = new InitialLdapContext(env, null);
         
         if (ctx != null)
         {
            ctx.close();
            return true;
         }

      }
      catch (NamingException e)
      {
         //
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
      return false;
   }


   public LDAPUserImpl createUserInstance(Attributes attrs, String dn) throws IdentityException
   {
      LDAPUserImpl ldapu = null;
      try
      {
         //log.debug("Search result attributes: " + attrs);


         Attribute uida = attrs.get(getUidAttributeID());
         if (uida == null)
         {
            throw new IdentityException("LDAP entry doesn't contain proper attribute:" + getUidAttributeID());
         }

         //ldapu = new LDAPUserImpl(dn,getIdentityContext(), uida.get().toString());

         //make DN as user ID 
         ldapu = new LDAPUserImpl(dn,getIdentityContext(), dn);

         if (isUserNameToLowerCase())
         {
            ldapu.setUserName(uida.get().toString().toLowerCase());
         }
         else
         {
            ldapu.setUserName(uida.get().toString());
         }

         log.debug("user uid: " + ldapu.getId());
         log.debug("user dn: " + ldapu.getDn());


      }
      catch (Exception e)
      {
         throw new IdentityException("Couldn't create LDAPUserImpl object from ldap entry (SearchResult)", e);
      }

      return ldapu;
   }

   /**
    * method not belonging to UserModule interface - ldap specific.
    *
    */
   public User findUserByDN(String dn) throws IdentityException, IllegalArgumentException, NoSuchUserException
   {
      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findUserByDN(): DN = " + dn);

         if (dn == null)
         {
            throw new IdentityException("User dn canot be null");
         }

         Attributes attrs = ldapContext.getAttributes(dn);

         if (attrs == null)
         {
            throw new IdentityException("Can't find user entry with DN: " + dn);
         }

         return createUserInstance(attrs, dn);

      }
      catch (NoSuchElementException e)
      {
         log.debug("No user found with dn: " + dn, e);
      }
      catch (NamingException e)
      {
         throw new IdentityException("User search failed.", e);
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
      return null;

   }

   /**
    * This method should be used by over modules to perform searches. It will allow user module
    * implementation to apply proper filter and search scope from the configuration
    *
    * @param filter that will be concatenated with proper user search filter from the module
    * @param filterArgs
    * @return
    */
   public abstract List searchUsers(String filter, Object[] filterArgs) throws NamingException, IdentityException;
   

   //**************************
   //*** Getter and Setters
   //**************************

   protected UserProfileModule getUserProfileModule() throws IdentityException
   {
      if (userProfileModule == null)
      {
         this.userProfileModule = (UserProfileModule)getIdentityContext().getObject(IdentityContext.TYPE_USER_PROFILE_MODULE);
      }
      return userProfileModule;
   }

   protected String getUidAttributeID() throws IdentityException
   {
      String uid = getIdentityConfiguration().getValue(IdentityConfiguration.USER_UID_ATTRIBUTE_ID);
      if (uid == null)
      {
         return "uid";
      }
      return uid;
   }

   protected LDAPConnectionContext getConnectionContext() throws IdentityException
   {
      if (connectionContext == null)
      {
         //this.connectionContext = (LDAPConnectionContext)getIdentityContext().getObject(IdentityContext.TYPE_CONNECTION_CONTEXT);
         throw new IdentityException("No LDAPConnectionContext available");
      }
      return connectionContext;
   }

   protected String getContainerDN() throws IdentityException
   {
      String cont = getIdentityConfiguration().getValue(IdentityConfiguration.USER_CONTAINER_DN);
      if (cont == null)
      {
         throw new IdentityException("Configuration option missing: " + IdentityConfiguration.USER_CONTAINER_DN);
      }
      return cont;
   }

   protected String getPrincipalPreffix() throws IdentityException
   {
      return getIdentityConfiguration().getValue(IdentityConfiguration.USER_PRINCIPAL_PREFIX);
      //return p;
   }

   protected String getPrincipalSuffix() throws IdentityException
   {
      return getIdentityConfiguration().getValue(IdentityConfiguration.USER_PRINCIPAL_SUFFIX);
      //return p;
   }

   protected String getPasswordAttributeId() throws IdentityException
   {
      String passwd =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_PASSWORD_ATTRIBUTE_ID);
      if (passwd == null)
      {
         return "userPassword";
      }
      else
      {
         return passwd;
      }
   }

   protected String getUserSearchFilter() throws IdentityException
   {
      String searchFilter =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_SEARCH_FILTER);
      if (searchFilter == null)
      {
         throw new IdentityException(IdentityConfiguration.USER_SEARCH_FILTER + " missing in configuration");
      }
      else
      {
         return searchFilter;
      }
   }

   protected String getUserSearchCtxDN() throws IdentityException
   {
      String searchCtx =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_CONTEXT_DN);
      if (searchCtx == null)
      {
         throw new IdentityException(IdentityConfiguration.USER_CONTEXT_DN + " missing in configuration");
      }
      else
      {
         return searchCtx;
      }
   }

   protected int getSearchTimeLimit() throws IdentityException
   {
      int searchTimeout = 10000;
      String limit = getIdentityConfiguration().getValue(IdentityConfiguration.SEARCH_TIME_LIMIT);
      if (limit != null)
      {
         try
         {
            searchTimeout =  Integer.parseInt(limit);
         }
         catch (NumberFormatException e)
         {
            log.warn(IdentityConfiguration.SEARCH_TIME_LIMIT + "wrong value falling back to defaults:" + e);
         }
      }
      return searchTimeout;
   }

   protected int getSearchScope() throws IdentityException
   {
      int searchScope = SearchControls.ONELEVEL_SCOPE;
      String scope =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_CONTEXT_DN);
      if (scope != null)
      {
         if ("OBJECT_SCOPE".equalsIgnoreCase(scope))
            searchScope = SearchControls.OBJECT_SCOPE;
         else if ("ONELEVEL_SCOPE".equalsIgnoreCase(scope))
            searchScope = SearchControls.ONELEVEL_SCOPE;
         else if ("SUBTREE_SCOPE".equalsIgnoreCase(scope))
            searchScope = SearchControls.SUBTREE_SCOPE;
      }
      return searchScope;
   }


   protected boolean isAllowEmptyPasswords()
   {

      String allowEmptyPasswords = getIdentityConfiguration().getValue(IdentityConfiguration.USER_ALLOW_EMPTY_PASSWORDS);
      if (allowEmptyPasswords != null && allowEmptyPasswords.equalsIgnoreCase("true"))
      {
            return Boolean.TRUE.booleanValue();
      }
      return Boolean.FALSE.booleanValue();
   }

   protected boolean isUserNameToLowerCase()
   {

      String userNameToLowerCase = getIdentityConfiguration().getValue(IdentityConfiguration.USER_USER_NAME_TO_LOWER_CASE);
      if (userNameToLowerCase != null && userNameToLowerCase.equalsIgnoreCase("true"))
      {
            return Boolean.TRUE.booleanValue();
      }
      return Boolean.FALSE.booleanValue();
   }

   protected boolean isSetPasswordAfterUserCreate()
   {
      String userNameToLowerCase = getIdentityConfiguration().getValue(IdentityConfiguration.USER_SET_PASSWORD_AFTER_USER_CREATE);
      if (userNameToLowerCase != null && userNameToLowerCase.equalsIgnoreCase("true"))
      {
         return Boolean.TRUE.booleanValue();
      }
      return Boolean.FALSE.booleanValue();
   }


   protected Map getAttributesToAdd() throws IdentityException
   {
      Map attributesToAdd = getIdentityConfiguration().getOptions(IdentityConfiguration.GROUP_USER_CREATE_ATTRIBUTES);
      if (attributesToAdd == null)
      {
         throw new IdentityException(IdentityConfiguration.GROUP_USER_CREATE_ATTRIBUTES + " missing in configuration");
      }
      return attributesToAdd;
   }

   public void setConnectionContext(LDAPConnectionContext connectionContext)
   {
      this.connectionContext = connectionContext;
   }

   protected String getPasswordEncoding() throws IdentityException
   {
      String encoding =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_PASSWORD_ENCODING);
      return encoding;
   }

   protected Map getUpdatePasswordAttributeValues() throws IdentityException
   {
      Map attributesToAdd = getIdentityConfiguration().getOptions(IdentityConfiguration.USER_PASSWORD_UPDATE_ATTRIBUTES);
      return attributesToAdd;
   }

   protected String getEnclosePasswordWith() throws IdentityException
   {
      String enc =  getIdentityConfiguration().getValue(IdentityConfiguration.USER_PASSWORD_ENCLOSE_WITH);
      return enc;
   }
   
}
