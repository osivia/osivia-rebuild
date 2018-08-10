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
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.service.RoleModuleService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class LDAPRoleModule extends RoleModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPRoleModule.class);

   private LDAPConnectionContext connectionContext;

   
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

   public void updateDisplayName(LDAPRoleImpl ldapr, String name) throws IdentityException
   {
      String attributeName = getDisplayNameAttributeID();
      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         //TODO: maybe perform a schema check if this attribute is allowed for such entry

         Attributes attrs = new BasicAttributes(true);
         Attribute attr = new BasicAttribute(attributeName);
         attr.add(name);
         attrs.put(attr);

         ldapContext.modifyAttributes(ldapr.getDn(), DirContext.REPLACE_ATTRIBUTE,attrs);

         fireRoleUpdatedEvent(ldapr.getId(), ldapr.getName(), name);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Cannot set role displayName value.", e);
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



   public LDAPRoleImpl createRoleInstance(Attributes attrs, String dn) throws IdentityException
   {
      LDAPRoleImpl ldapr = null;
      try
      {

         //log.debug("Attributes: " + attrs);

         //role name
         Attribute uida = attrs.get(getRidAttributeID());
         if (uida == null)
         {
            throw new IdentityException("LDAP entry doesn't contain proper attribute:" + getRidAttributeID());
         }
         //ldapr = new LDAPRoleImpl(uida.getID().concat("=").concat((String)uida.get()) + "," + getContainerDN(), identityContext);
         Attribute display = attrs.get(getDisplayNameAttributeID());
         if (display == null)
         {
            throw new IdentityException("LDAP entry doesn't contain proper attribute:" + getDisplayNameAttributeID());
         }
         ldapr = new LDAPRoleImpl(dn, getIdentityContext(), dn, uida.get().toString(), display.get().toString());
         //ldapr.setDisplayName(display.get().toString());



         log.debug("role uid: " + ldapr.getId());
         log.debug("role dn: " + ldapr.getDn());


      }
      catch (NamingException e)
      {
         throw new IdentityException("Couldn't create LDAPRoleImpl object from ldap entry (SearchResult)", e);
      }

      return ldapr;
   }

   /**
    * method not belonging to UserModule interface - ldap specific.
    *
    */
   public Role findRoleByDN(String dn) throws IdentityException, IllegalArgumentException
   {

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findRoleByDN(): DN = " + dn);

         if (dn == null)
         {
            throw new IdentityException("Role dn canot be null");
         }


         Attributes attrs = ldapContext.getAttributes(dn);

         if (attrs == null)
         {
            throw new IdentityException("Can't find role entry with DN: " + dn);
         }

         return createRoleInstance(attrs, dn);

      }
      catch (NoSuchElementException e)
      {
         log.debug("No role found with dn: " + dn, e);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Role search failed.", e);
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
    * This method should be used by over modules to perform searches. It will allow role module
    * implementation to apply proper filter and search scope from the configuration
    *
    * @param filter that will be concatenated with proper role search filter from the module
    * @return
    */
   public abstract List searchRoles(String filter, Object[] filterArgs) throws NamingException, IdentityException;

   //**************************
   //*** Getter and Setters
   //**************************

   protected String getRidAttributeID() throws IdentityException
   {
      String rid = getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_RID_ATTRIBUTE_ID);
      if (rid == null)
      {
         return "cn";
      }
      return rid;
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
      String cont = getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_CONTAINER_DN);
      if (cont == null)
      {
         throw new IdentityException("Configuration option missing: " + IdentityConfiguration.ROLE_CONTAINER_DN);
      }
      return cont;
   }

   protected String getDisplayNameAttributeID() throws IdentityException
   {
      String display = getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_DISPLAY_NAME_ATTRIBUTE_ID);
      if (display == null)
      {
         return getRidAttributeID();
      }
      return display;
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
            log.info(IdentityConfiguration.SEARCH_TIME_LIMIT + "wrong value:" + e);
         }
      }
      return searchTimeout;
   }

   protected int getSearchScope() throws IdentityException
   {
      int searchScope = SearchControls.ONELEVEL_SCOPE;
      String scope =  getIdentityConfiguration().getValue(IdentityConfiguration.SEARCH_SCOPE);
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

   protected String getRoleCtxDN() throws IdentityException
   {
      String roleCtx = getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_CONTEXT_DN);
      if (roleCtx == null)
      {
         throw new IdentityException("Configuration option missing: " + IdentityConfiguration.ROLE_CONTEXT_DN);   
      }
      return roleCtx;
   }

   protected String getRoleSearchFilter() throws IdentityException
   {
      String searchFilter =  getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_SEARCH_FILTER);
      if (searchFilter == null)
      {
         throw new IdentityException(IdentityConfiguration.ROLE_SEARCH_FILTER + " missing in configuration");
      }
      else
      {
         return searchFilter;
      }
   }

  /* protected int getRoleRecurtion() throws IdentityException
   {
      int recurtion = 0;
      String rr =  getIdentityConfiguration().getValue(IdentityConfiguration.ROLE_RECURSION);
      if (rr != null)
      {
         try
         {
            recurtion = Integer.parseInt(rr);
         }
         catch (NumberFormatException e)
         {
            log.warn(IdentityConfiguration.ROLE_RECURSION + " wrong value - disabling recurtion:" + e);
         }
      }
      return recurtion;
   }*/


   public void setConnectionContext(LDAPConnectionContext connectionContext)
   {
      this.connectionContext = connectionContext;
   }
}
