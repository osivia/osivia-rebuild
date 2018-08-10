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
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.ldap.helper.LDAPTools;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * Simple implementation of UserModule for LDAP support. Search of users is limited to one place * containerField -  DN
 * of entry containing users (like ou=People,dc=example,dc=com). It's where users will be added using createUser()
 * method. Under this DN users will be searched using ONELEVEL_SCOPE * uidAttributeID - attribute that stores user id.
 * Default value is "uid"
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPUserModuleImpl extends LDAPUserModule 
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPUserModuleImpl.class);

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


         String filter = "(".concat(getUidAttributeID()).concat("=").concat(userName).concat(")");
         log.debug("Search filter: " + filter);

         List sr = searchUsers(filter, null);
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

   public User findUserById(Object id) throws IdentityException, IllegalArgumentException, NoSuchUserException
   {
      if (id == null)
      {
         throw new IdentityException("Cannot search user with null id");
      }
      if (!(id instanceof String))
      {
         throw new IdentityException("Only String id is suppoted");
      }
      return findUserByDN(id.toString());

   }

   public User findUserById(String id) throws IdentityException, IllegalArgumentException, NoSuchUserException
   {
      return findUserByDN(id);
   }

   

   
   public User createUser(String userName, String password) throws IdentityException, IllegalArgumentException
   {

      if (userName == null)
      {
         throw new IdentityException("User name cannot be null");
      }
      /*if (realEmail == null)
      {
         throw new IdentityException("User email cannot be null");
      }*/
      if (password == null)
      {
         throw new IdentityException("User password cannot be null");
      }

      log.debug("Creating user: " + userName);


      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         //
         LdapContext ctx = (LdapContext)ldapContext.lookup(getContainerDN());

         //We store new entry using set of attributes. This should give more flexibility then
         //extending user object from ContextDir - configure what objectClass place there
         Attributes attrs = new BasicAttributes(true);

         //create attribute using provided configuration
         Map attributesToAdd = getAttributesToAdd();

         //attributes
         for (Iterator it1 = attributesToAdd.keySet().iterator(); it1.hasNext();)
         {
            String attributeName = (String)it1.next();
            if (getUidAttributeID().equals(attributeName))
            {
               continue;
            }
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

         if (!isSetPasswordAfterUserCreate())
         {
            attrs.put(getPasswordAttributeId(), password);
         }

         String validUserName = LDAPTools.encodeRfc2253Name(userName);

         String dn = getUidAttributeID().concat("=").concat(validUserName);

         log.debug("creating ldap entry for: " + dn + "; " + attrs);
         ctx.createSubcontext(dn, attrs);


      }
      catch (Exception e)
      {
         throw new IdentityException("Failed to create user", e);
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

      LDAPUserImpl u =  (LDAPUserImpl)findUserByUserName(userName);

      if (isSetPasswordAfterUserCreate())
      {
         updatePassword(u, password);
      }


      fireUserCreatedEvent(u.getId(), u.getUserName());
      return u;
   }



   //TODO: remove user assignments before?
   public void removeUser(Object id) throws IdentityException, IllegalArgumentException
   {

      LDAPUserImpl ldapu = (LDAPUserImpl)findUserById(id);

      String userName = ldapu.getUserName();

      if (ldapu == null)
      {
         throw new IdentityException("Cannot find user for removal");
      }

      if (ldapu.getDn() == null)
      {
         throw new IdentityException("Cannot obtain DN of user");
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("removing entry: " + ldapu.getDn());
         ldapContext.unbind(ldapu.getDn());
      }
      catch (Exception e)
      {
         throw new IdentityException("Failed to remove user: ", e);
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


      //user was successfull removed so fire events
      fireUserDestroyedEvent(id, userName);
   }


   public Set findUsers(int offset, int limit) throws IdentityException, IllegalArgumentException
   {

      return findUsersFilteredByUserName("*",offset, limit);

   }

   //TODO:implement something to use offset and limit - sort asc and
   //TODO: and testcase this...
   public Set findUsersFilteredByUserName(String filter, int offset, int limit) throws IdentityException, IllegalArgumentException
   {
      if (filter == null)
      {
         throw new IllegalArgumentException("Null user name filter");
      }

      //log.info("Current implementation of findUsersFilteredByUserName returns all users and is not \"offset\" and \"limit\" sensitive ");

      if (limit == 0)
      {
         throw new IdentityException("Search limit shouldn't be set to 0");
      }

      List uf = new LinkedList();
      Enumeration results = null;


      if (filter.length() == 0)
      {
         filter = "*";
      }
      else if (!(filter.length() == 1  && filter.equals("*")))
      {
         filter = "*" + filter + "*";
      }

      try
      {
         //search all entries containing "uid" attribute
         String ldap_filter = "(".concat(getUidAttributeID()).concat("=").concat(filter).concat(")");
         log.debug("Search filter: " + filter);



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
         String filter = "(".concat(getUidAttributeID()).concat("=").concat("*").concat(")");
         log.debug("Search filter: " + filter);


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
      SearchControls controls = new SearchControls();
      controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
      controls.setReturningObjFlag(true);
      controls.setTimeLimit(getSearchTimeLimit());

      log.debug("Search filter: " + filter);

      LdapContext ldapContext = getConnectionContext().createInitialContext();
      NamingEnumeration results = null;
      try
      {

         if (filterArgs == null)
         {
            results = ldapContext.search(getContainerDN(), filter, controls);
            return Tools.toList(results);
         }
         else
         {
            results = ldapContext.search(getContainerDN(), filter, filterArgs, controls);
            return Tools.toList(results);
         }
      }
      finally
      {
         if (results != null)
         {
            results.close();
         }
         if (ldapContext != null)
         {
            ldapContext.close();
         }

      }
   }


   protected Set processUsers(Collection users) throws Exception
   {
      Set ui = new HashSet();
      for (Iterator iterator = users.iterator(); iterator.hasNext();)
      {
         SearchResult res = (SearchResult)iterator.next();
         Context ctx = (Context)res.getObject();
         String dn = ctx.getNameInNamespace();
         ui.add(createUserInstance(res.getAttributes(), dn));
         ctx.close();
      }
      return ui;
   }

   /**
    * Comparator for users entries
    */
   protected class UserEntryComparator implements Comparator
   {


      public int compare(Object o1, Object o2)
      {
         try
         {
            SearchResult u1 = (SearchResult)o1;
            SearchResult u2 = (SearchResult)o2;

            Attribute uida1 = u1.getAttributes().get(getUidAttributeID());
            Attribute uida2 = u2.getAttributes().get(getUidAttributeID());

            String name1 = uida1.get().toString();
            String name2 = uida2.get().toString();

            return name1.compareToIgnoreCase(name2);
         }
         catch(Throwable e)
         {
            //none
         }
         return 0;
      }
   }


}


