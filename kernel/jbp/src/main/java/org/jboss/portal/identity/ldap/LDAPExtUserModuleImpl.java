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

import org.jboss.portal.common.util.Tools;
import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPExtUserModuleImpl extends LDAPUserModuleImpl
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPExtUserModuleImpl.class);

   public User findUserByUserName(String userName) throws IdentityException, IllegalArgumentException, NoSuchUserException
   {
      Context ctx = null;
      try
      {
         log.debug("findUserByUserName(): username = " + userName);

         if (userName == null)
         {
            throw new IdentityException("User name canot be null");
         }

         String filter = getUserSearchFilter();
         log.debug("Search filter: " + filter);

         Object[] filterArgs = {userName};

         List sr = searchUsers(filter, filterArgs);
         if (sr.size() > 1)
         {
            throw new IdentityException("Found more than one user with id: " + userName + "" +
               "Posible data inconsistency");
         }
         SearchResult res = (SearchResult)sr.iterator().next();
         ctx = (Context)res.getObject();
         String dn = ctx.getNameInNamespace();
         User user = createUserInstance(res.getAttributes(), dn);
         ctx.close();
         return user;

      }
      catch (NoSuchElementException e)
      {
         log.debug("No user found with name: " + userName, e);

      }
      catch (NamingException e)
      {
         throw new IdentityException("User search failed.", e);
      }
      finally
      {
         try
         {
            if (ctx != null)
            {
               ctx.close();
            }
         }
         catch (NamingException e)
         {
            throw new IdentityException("Failed to close LDAP connection", e);
         }
      }
      throw new NoSuchUserException("No user found with name: " + userName);
   }

   //findUserById(Object id) from super


   //findUserById(String id) from super 

   public User createUser(String userName, String password) throws IdentityException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("User management is not supported in this implementation of UserModule");
   }

   public User createUser(String userName, String password, String realEmail) throws IdentityException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("User management is not supported in this implementation of UserModule");
   }

   public void removeUser(Object id) throws IdentityException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("User management is not supported in this implementation of UserModule");
   }

   public Set findUsers(int offset, int limit) throws IdentityException, IllegalArgumentException
   {
      return findUsersFilteredByUserName("*", offset, limit);
   }

   public Set findUsersFilteredByUserName(String filter, int offset, int limit) throws IdentityException, IllegalArgumentException
   {
      List uf = new LinkedList();
      try
      {
         log.debug("findUserFilteredByUserName(): filter = " + filter);

         if (filter == null)
         {
            throw new IllegalArgumentException("Null user name filter");
         }

         log.info("Current implementation of findUsersFilteredByUserName returns all users and is not \"offset\" and \"limit\" sensitive ");

         if (filter.length() == 0)
         {
            filter = "*";
         }
         else if (!(filter.length() == 1  && filter.equals("*")))
         {
            filter = "*" + filter + "*";
         }

         String ldap_filter = getUserSearchFilter();

         //* chars are escaped in filterArgs so we must replace it manually
         ldap_filter = ldap_filter.replaceAll("\\{0\\}", filter);
         log.debug("Search filter: " + ldap_filter);

         uf = searchUsers(ldap_filter, null);

         int size = uf.size();
         if (offset == 0 && size <= limit)
         {
            return processUsers(uf);
         }

         Collections.sort(uf, new UserEntryComparator());

         if (offset + limit <= size)
         {
            return processUsers(uf.subList(offset, offset + limit));
         }
         else if (offset >= size)
         {
            return new HashSet();
         }

         return processUsers(uf.subList(offset, size));
      }
      catch (NoSuchElementException e)
      {
         log.debug("No users found", e);
      }
      catch (Throwable e)
      {
         throw new IdentityException("User search failed.", e);
      }

      //won't happen
      return null;
   }

   public int getUserCount() throws IdentityException, IllegalArgumentException
   {
      try
      {
         //search all entries containing "uid" attribute
         String filter = getUserSearchFilter();
         log.debug("Search filter: " + filter);

         //* chars are escaped in filterArgs so we must replace it manually
         filter = filter.replaceAll("\\{0\\}", "*");

         List sr = searchUsers(filter, null);

         return sr.size();
      }
      catch (NoSuchElementException e)
      {
         log.debug("No users found", e);
      }
      catch (Exception e)
      {
         throw new IdentityException("User search failed.", e);
      }
      return 0;
   }




   /**
    * This method should be used by over modules to perform searches. It will allow user module
    * implementation to apply proper filter and search scope from the configuration
    *
    * @param filter that will be concatenated with proper user search filter from the module
    * @return
    */
   public List searchUsers(String filter, Object[] filterArgs) throws NamingException, IdentityException
   {

      LdapContext ldapContext = getConnectionContext().createInitialContext();
      NamingEnumeration results = null;

      try
      {
         SearchControls controls = new SearchControls();
         controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
         controls.setReturningObjFlag(true);
         controls.setTimeLimit(getSearchTimeLimit());

         log.debug("Search filter: " + filter);
         if (log.isDebugEnabled() && filterArgs != null)
         {
            for (int i = 0; i < filterArgs.length; i++)
            {
               Object filterArg = filterArgs[i];
               log.debug("Search filterArg: {" + i + "}: " + filterArg);
            }
         }
         log.debug("Search ctx: " + getUserSearchCtxDNs());

         Set userCtxs = getUserSearchCtxDNs();

         if (userCtxs.size() == 1)
         {
            if (filterArgs == null)
            {
               results = ldapContext.search(getUserSearchCtxDN(), filter, controls);
            }
            else
            {
               results = ldapContext.search(getUserSearchCtxDN(), filter, filterArgs, controls);
            }
            return Tools.toList(results);


         }
         else
         {
            List merged = new LinkedList();

            for (Iterator iterator = userCtxs.iterator(); iterator.hasNext();)
            {
               String userCtx = (String)iterator.next();
               if (filterArgs == null)
               {
                  results = ldapContext.search(userCtx, filter, controls);
               }
               else
               {
                  results = ldapContext.search(userCtx, filter, filterArgs, controls);
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


   protected Set getUserSearchCtxDNs() throws IdentityException
   {
      Set searchCtx =  getIdentityConfiguration().getValues(IdentityConfiguration.GROUP_COMMON, IdentityConfiguration.USER_CONTEXT_DN);
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
