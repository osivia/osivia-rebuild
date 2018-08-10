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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.jboss.portal.common.util.Tools;
import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPRoleModuleImpl extends LDAPRoleModule
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPRoleModuleImpl.class);

   public Role findRoleByName(String name) throws IdentityException, IllegalArgumentException
   {

      DirContext ctx = null;

      try
      {
         log.debug("findRoleByName(): name = " + name);

         if (name == null)
         {
            throw new IdentityException("Role name canot be null");
         }


         String filter = "(".concat(getRidAttributeID()).concat("=").concat(name).concat(")");
         log.debug("Search filter: " + filter);

         List sr = searchRoles(filter, null);
         if (sr.size() > 1)
         {
            throw new IdentityException("Found more than one role with id: " + name + "" +
               "Posible data inconsistency");
         }
         if (sr.size() == 0)
         {
            throw new IdentityException("No such role " + name);
         }
         SearchResult res = (SearchResult)sr.iterator().next();
         ctx  = (DirContext)res.getObject();
         Role role = createRoleInstance(res.getAttributes(),ctx.getNameInNamespace());
         return role;

      }
      catch (NoSuchElementException e)
      {
         log.debug("No role found with name: " + name, e);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Role search failed.", e);
      }
      finally
      {
         if (ctx != null)
         {
            try
            {
               ctx.close();
            }
            catch (NamingException e)
            {
               throw new IdentityException("Failed to close LDAP connection", e);
            }
         }
      }
      throw new IdentityException("No role found with name: " + name);
   }

   public Set findRolesByNames(String[] names) throws IdentityException, IllegalArgumentException
   {
      if (names == null)
      {
         throw new IllegalArgumentException("null argument");
      }

      Set roles = new HashSet();
      try
      {
         //construct a filter with all role names
         StringBuffer filter = new StringBuffer("(| ");
         for (int i = 0; i < names.length; i++)
         {
            String name = names[i];
            filter.append("(")
                  .append(getRidAttributeID())
                  .append("=")
                  .append(name)
                  .append(") ");
         }                  
         filter.append(")");

         List sr = searchRoles(filter.toString(), null);
         log.debug("Roles found: " + sr.size());
         for (Iterator iterator = sr.iterator(); iterator.hasNext();)
         {
            SearchResult res = (SearchResult)iterator.next();
            DirContext ctx  = (DirContext)res.getObject();
            roles.add(createRoleInstance(res.getAttributes(),ctx.getNameInNamespace()));
            ctx.close();
         }
      }
      catch (Exception e)
      {
         throw new IdentityException("Can't retreive roles", e);
      }

      return roles;

   }

   public Role findRoleById(Object id) throws IdentityException, IllegalArgumentException
   {
      if (id == null)
      {
         throw new IdentityException("Cannot search role with null id");
      }
      if (!(id instanceof String))
      {
         throw new IdentityException("Only String id is suppoted");
      }
      return findRoleById((String)id);
   }

   public Role findRoleById(String id) throws IdentityException, IllegalArgumentException
   {
      return findRoleByDN(id);
   }

   public Role createRole(String name, String displayName) throws IdentityException, IllegalArgumentException
   {
      if (name == null)
      {
         throw new IdentityException("Role name cannot be null");
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      LdapContext ctx = null;

      try
      {
         //
         ctx = (LdapContext)ldapContext.lookup(getContainerDN());

         //We store new entry using set of attributes. This should give more flexibility then
         //extending user object from ContextDir - configure what objectClass place there
         Attributes attrs = new BasicAttributes(true);

         //add attribute using provided configuration
         Map attributesToAdd = getAttributesToAdd();

         //attribute
         for (Iterator it1 = attributesToAdd.keySet().iterator(); it1.hasNext();)
         {
            String attributeName = (String)it1.next();
            log.debug("adding attribute: " + attributeName);
            Attribute attr = new BasicAttribute(attributeName);
            Set attributeValues = (Set)attributesToAdd.get(attributeName);

            //values
            for (Iterator it2 = attributeValues.iterator(); it2.hasNext();)
            {
               String attrValue = (String)it2.next();
               log.debug("adding attribute value: " + attrValue);
               attr.add(attrValue);
            }
            attrs.put(attr);
         }

         //role name
         attrs.put(getRidAttributeID(), name);

         //display name
         if (!getDisplayNameAttributeID().equals(getRidAttributeID()))
         {
            attrs.put(getDisplayNameAttributeID(), displayName);
         }

         String dn = getRidAttributeID().concat("=").concat(name);

         log.debug("creating ldap entry for: " + dn + "; " + attrs);
         ctx.createSubcontext(dn, attrs);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Failed to create role", e);
      }
      finally
      {
         try
         {
            ldapContext.close();
            ctx.close();
         }
         catch (NamingException e)
         {
            throw new IdentityException("Failed to close LDAP connection", e);
         }
      }

      Role resultRole =  findRoleByName(name);

      fireRoleCreatedEvent(resultRole.getId(), resultRole.getName());

      return resultRole;
   }

   //TODO: remove role assignments before?
   public void removeRole(Object id) throws IdentityException, IllegalArgumentException
   {
      LDAPRoleImpl ldapr = (LDAPRoleImpl)findRoleById(id);

      if (ldapr == null)
      {
         throw new IdentityException("Cannot find role for removal");
      }

      if (ldapr.getDn() == null)
      {
         throw new IdentityException("Cannot obtain DN of role");
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("removing entry: " + ldapr.getDn());
         ldapContext.unbind(ldapr.getDn());

         fireRoleDestroyedEvent(ldapr.getId(), ldapr.getName());
      }
      catch (Exception e)
      {
         throw new IdentityException("Failed to remove role: ", e);
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

   public int getRolesCount() throws IdentityException
   {
      try
      {
         //search all entries containing "cn" attribute
         String filter = getRidAttributeID().concat("=").concat("*");
         log.debug("Search filter: " + filter);

         List sr = searchRoles(filter, null);

         return sr.size();

      }
      catch (NoSuchElementException e)
      {
         log.debug("No roles found", e);
      }
      catch (Exception e)
      {
         throw new IdentityException("Role search failed.", e);
      }
      return 0;
   }

   public Set findRoles() throws IdentityException
   {
      Set rf = new HashSet();
      try
      {
         //search all entries containing "cn" attribute
         String filter = "(".concat(getRidAttributeID()).concat("=").concat("*").concat(")");
         log.debug("Search filter: " + filter);

         List results = searchRoles(filter, null);
         Iterator iter = results.iterator();
         while (iter.hasNext())
         {
            SearchResult res = (SearchResult)iter.next();
            DirContext ctx  = (DirContext)res.getObject();
            rf.add(createRoleInstance(res.getAttributes(),ctx.getNameInNamespace()));
            ctx.close();
         }
      }
      catch (NoSuchElementException e)
      {
         log.debug("No roles found", e);
      }
      catch (Exception e)
      {
         throw new IdentityException("Role search failed.", e);
      }
      return rf;
   }

   /**
    * This method should be used by over modules to perform searches. It will allow role module
    * implementation to apply proper filter and search scope from the configuration
    *
    * @param filter that will be concatenated with proper role search filter from the module
    * @return
    */
   public List searchRoles(String filter, Object[] filterArgs) throws NamingException, IdentityException
   {

      LdapContext ldapContext = getConnectionContext().createInitialContext();
      NamingEnumeration results = null;
      try
      {
         SearchControls controls = new SearchControls();
         controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
         controls.setReturningObjFlag(true);

         //String filter = getUidAttributeID().concat("=").concat(userName);
         filter = filter.replaceAll("\\\\", "\\\\\\\\");
         log.debug("Search filter: " + filter);


         if (filterArgs == null)
         {
            results = ldapContext.search(getContainerDN(), filter, controls);
         }
         else
         {
            results = ldapContext.search(getContainerDN(), filter, filterArgs, controls);
         }
         return Tools.toList(results);
      }
      finally
      {
         if (results != null)
         {
            results.close();
         }
         ldapContext.close();
      }
   }

   private Map getAttributesToAdd() throws IdentityException
   {
      Map attributesToAdd = getIdentityConfiguration().getOptions(IdentityConfiguration.GROUP_ROLE_CREATE_ATTRIBUTES);
      if (attributesToAdd == null)
      {
         throw new IdentityException(IdentityConfiguration.GROUP_ROLE_CREATE_ATTRIBUTES + " missing in configuration");
      }
      return attributesToAdd;
   }



}
