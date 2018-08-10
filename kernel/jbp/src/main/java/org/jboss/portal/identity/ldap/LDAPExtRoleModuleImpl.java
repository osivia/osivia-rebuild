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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
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
public class LDAPExtRoleModuleImpl extends LDAPRoleModuleImpl
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPExtRoleModuleImpl.class);

   public Role findRoleByName(String name) throws IdentityException, IllegalArgumentException
   {
      try
      {
         log.debug("findRoleByName(): name = " + name);

         if (name == null)
         {
            throw new IdentityException("Role name canot be null");
         }


         String filter = getRoleSearchFilter();
         log.debug("Search filter: " + filter);


         Object[] filterArgs = {name};
         
         List sr = searchRoles(filter, filterArgs);
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
         DirContext ctx  = (DirContext)res.getObject();
         Role role = createRoleInstance(res.getAttributes(),ctx.getNameInNamespace());
         ctx.close();
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

         String searchFilter = getRoleSearchFilter();

         for (int i = 0; i < names.length; i++)
         {
            String name = names[i];

            String namedFilter = searchFilter.replaceAll("\\{0\\}", name);
            filter.append(namedFilter);
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

   public Role createRole(String name, String displayName) throws IdentityException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("Role management is not supported in this implementation of RoleModule");
   }

   public void removeRole(Object id) throws IdentityException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("Role management is not supported in this implementation of RoleModule");
   }

   public int getRolesCount() throws IdentityException
   {
      try
      {
         //search all entries
         String filter = getRoleSearchFilter();
         //* chars are escaped in filterArgs so we must replace it manually
         filter = filter.replaceAll("\\{0\\}", "*");
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
         //search all entries 
         String filter = getRoleSearchFilter();
         //* chars are escaped in filterArgs so we must replace it manually
         filter = filter.replaceAll("\\{0\\}", "*");
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
    * This method should be used by other modules to perform searches. It will allow role module
    * implementation to apply proper filter and search scope from the configuration
    *
    * @param filter that will be concatenated with proper user search filter from the module
    * @return
    */
   public List searchRoles(String filter, Object[] filterArgs) throws NamingException, IdentityException
   {

      LdapContext ldapContext = getConnectionContext().createInitialContext();
      NamingEnumeration results = null;
      
      try
      {
         SearchControls controls = new SearchControls();
         controls.setSearchScope(getSearchScope());
         controls.setReturningObjFlag(true);
         controls.setTimeLimit(getSearchTimeLimit());

         String[] retAttr = {getRidAttributeID(), getDisplayNameAttributeID()};
         controls.setReturningAttributes(retAttr);

         //
         filter = filter.replaceAll("\\\\", "\\\\\\\\");

         log.debug("Search filter: " + filter);
         if (log.isDebugEnabled() && filterArgs != null)
         {
            for (int i = 0; i < filterArgs.length; i++)
            {
               Object filterArg = filterArgs[i];
               log.debug("Search filterArg: {" + i + "}: " + filterArg);
            }
         }
         log.debug("Search ctx: " + getRoleSearchCtxDNs());

         Set roleCtxs = getRoleSearchCtxDNs();

         if (roleCtxs.size() == 1)
         {

            if (filterArgs == null)
            {
               results = ldapContext.search(getRoleCtxDN(), filter, controls);
            }
            else
            {
               results = ldapContext.search(getRoleCtxDN(), filter, filterArgs, controls);
            }
            return Tools.toList(results);


         }
         else
         {
            List merged = new LinkedList();

            for (Iterator iterator = roleCtxs.iterator(); iterator.hasNext();)
            {
               String roleCtx = (String)iterator.next();
               
               if (filterArgs == null)
               {
                  results = ldapContext.search(roleCtx, filter, controls);
               }
               else
               {
                  results = ldapContext.search(roleCtx, filter, filterArgs, controls);
               }
               merged.addAll(Tools.toList(results));
               results.close();
            }

            return merged;
         }
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


   protected Set getRoleSearchCtxDNs() throws IdentityException
   {
      Set searchCtx =  getIdentityConfiguration().getValues(IdentityConfiguration.GROUP_COMMON, IdentityConfiguration.ROLE_CONTEXT_DN);
      if (searchCtx == null || searchCtx.size() == 0)
      {
         throw new IdentityException(IdentityConfiguration.USER_CONTEXT_DN + " missing in configuration");
      }
      else
      {
         return searchCtx;
      }
   }
}
