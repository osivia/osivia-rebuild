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

import org.jboss.portal.common.util.Tools;
import org.jboss.portal.identity.CachedUserImpl;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPStaticRoleMembershipModuleImpl extends LDAPMembershipModule
{

   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPStaticRoleMembershipModuleImpl.class);

   public Set getRoles(User user) throws IdentityException
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
         throw new IllegalArgumentException("UserMembershipModuleImpl supports only LDAPUserImpl objects");
      }

      Set roles = new HashSet();

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findRoles(): role = " + ldapUser.getDn());

         if (ldapUser.getUserName() == null)
         {
            throw new IdentityException("Role name canot be null");
         }

         //obtain Role entry attributes from directory
         Attributes attrs = ldapContext.getAttributes(ldapUser.getDn(), new String[] {getMemberAttributeID()});

         //log.debug("User attributes: " + attrs);
         if (attrs == null )
         {
            throw new IdentityException("Cannot find User with DN: " + ldapUser.getDn());
         }

         Attribute memberOfAttribute = attrs.get(getMemberAttributeID());

         //if there are no members
         if (memberOfAttribute == null)
         {
            return roles;
         }

         //iterate over user names belonging to this role
         NamingEnumeration values = memberOfAttribute.getAll();
         while (values.hasMoreElements())
         {
            String value = values.nextElement().toString();
            String name = value;

            try
            {
               //if user is pointed as DN get only it's name
               if (isUidAttributeIsDN())
               {
                  roles.add(getRoleModule().findRoleByDN(name));
               }
               else
               {
                  roles.add(getRoleModule().findRoleByName(name));
               }
            }
            catch(IdentityException ie)
            {
               log.error("Failed to find role: " + name + "/" + value, ie);

            }
         }
      }
      catch (NamingException e)
      {
         throw new IdentityException("Resolving User Roles failed.", e);
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

      return roles;

   }

   public Set getUsers(Role role) throws IdentityException
   {
      return getUsers(role, null);
   }

   public Set getUsers(Role role, String userNameFilter) throws IdentityException
   {
      if (role == null)
      {
         throw new IllegalArgumentException("Role cannot be null");
      }

      LDAPRoleImpl ldapRole = null;

      if (role instanceof LDAPRoleImpl)
      {
         ldapRole = (LDAPRoleImpl)role;
      }
      else
      {
         throw new IllegalArgumentException("UserMembershipModuleImpl supports only LDAPRoleImpl objects");
      }

      //throw new UnsupportedOperationException("Not yet implemented");

      Set users = new HashSet();
      try
      {

         log.debug("getUsers(): role DN = " + ldapRole.getDn());

         String memberOfName = "";

            if (isUidAttributeIsDN())
         {
            memberOfName = ldapRole.getDn();
         }
         else
         {
            memberOfName = ldapRole.getName();
         }

         String filter = getMemberAttributeID().concat("=").concat(memberOfName);
         log.debug("Search filter: " + filter);


         //NamingEnumeration results = getConnectionContext().createInitialContext().search(getUserContainerDN(), filter, controls);
         List sr = getUserModule().searchUsers(filter, null);


         for (Iterator iterator = sr.iterator(); iterator.hasNext();)
         {
            SearchResult res = (SearchResult)iterator.next();
            DirContext ctx = (DirContext)res.getObject();

            //TODO: this part isn't efficient - check the condition without creating instance
            User user = getUserModule().createUserInstance(res.getAttributes(),ctx.getNameInNamespace());
            if (userNameFilter != null && userNameFilter.length() != 0 && !user.getUserName().matches(".*" + userNameFilter + ".*"))
            {
               ctx.close();
               continue;
            }
            users.add(user);
            ctx.close();
         }



      }
      catch (Exception e)
      {
         log.debug("Failed to resolve role users: " + ldapRole.getId().toString(), e);
      }

      return users;

   }

   public void assignUsers(Role role, Set users) throws IdentityException
   {
      if (role == null)
      {
         throw new IllegalArgumentException("Role cannot be null");
      }

      LDAPRoleImpl ldapRole = null;

      if (role instanceof LDAPRoleImpl)
      {
         ldapRole = (LDAPRoleImpl)role;
      }
      else
      {
         throw new IllegalArgumentException("UserMembershipModuleImpl supports only LDAPRoleImpl objects");
      }


      //First build a list of user DNs to add
      List userDNsToAdd = new LinkedList();

      for (Iterator iterator = users.iterator(); iterator.hasNext();)
      {
         try
         {
            User user = (User)iterator.next();

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

            LDAPUserImpl ldapUser = (LDAPUserImpl)user;

            userDNsToAdd.add(ldapUser.getDn());
         }
         catch(ClassCastException e)
         {
            throw new IdentityException("Can add only LDAPUserImpl objects", e);
         }
      }

      String memberOfName=null;

      //Find all the users that currently contain role as member (need to remove role from some of them)
      if (isUidAttributeIsDN())
      {
         memberOfName = ldapRole.getDn();
      }
      else
      {
         memberOfName = ldapRole.getName();
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         String filter = getMemberAttributeID().concat("=").concat(memberOfName);
         log.debug("Search filter: " + filter);

         List sr = getUserModule().searchUsers(filter, null);
         //iterate over users that contain a role
         for (Iterator iterator = sr.iterator(); iterator.hasNext();)
         {
            SearchResult res = (SearchResult)iterator.next();
            DirContext ctx = (DirContext)res.getObject();
            String userDN = ctx.getNameInNamespace();
            ctx.close();
            //if user is one which we want to add
            if (userDNsToAdd.contains(userDN))
            {
               //we do nothing but mark this user as added
               userDNsToAdd.remove(userDN);
               continue;
            }
            //if it's not on the list we need to remove role from it
            else
            {
               //obtain Role entry attributes from directory
               Attributes attrs = ldapContext.getAttributes(userDN, new String[] {getMemberAttributeID()});

               //log.debug("Role attributes: " + attrs);
               if (attrs == null)
               {
                  throw new IdentityException("Cannot find User with DN: " + userDN);
               }

               Attribute attr = attrs.get(getMemberAttributeID());

               attr.remove(memberOfName);

               //and replace attributes
               Attributes newAttrs = new BasicAttributes(true);
               //newAttrs.put(getMemberAttributeID(), attr);
               newAttrs.put(attr);

               ldapContext.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, newAttrs);

               //and mark this role as done
               userDNsToAdd.remove(userDN);
            }
         }

         //now iterate over roles that left to process
         for (Iterator iterator = userDNsToAdd.iterator(); iterator.hasNext();)
         {
            String userDN = (String)iterator.next();

            //changes to make
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
               new BasicAttribute(getMemberAttributeID(), memberOfName));
            // Perform the requested modifications on the named object
            ldapContext.modifyAttributes(userDN, mods);
         }

         fireMembershipChangedEvent(role, users);

         //and that should be all...
      }
      catch (NamingException e)
      {
         throw new IdentityException("Failed to assign users", e);
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

   public void assignRoles(User user, Set roles) throws IdentityException
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
         throw new IllegalArgumentException("UserMembershipModuleImpl supports only LDAPUserImpl objects");
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findRoles(): user = " + ldapUser.getDn());

         if (ldapUser.getUserName() == null)
         {
            throw new IdentityException("User name canot be null");
         }

         //construct new member attribute values
         Attributes attrs = new BasicAttributes(true);

         Attribute member = new BasicAttribute(getMemberAttributeID());
         for (Iterator iterator = roles.iterator(); iterator.hasNext();)
         {
            try
            {
               LDAPRoleImpl role = (LDAPRoleImpl)iterator.next();
               if (isUidAttributeIsDN())
               {
                  member.add(role.getDn());
               }
               else
               {
                  member.add(role.getName());
               }
            }
            catch (ClassCastException e)
            {
               throw new IdentityException("Only can add LDAPRoleImpl objects", e);
            }
         }
         attrs.put(member);

         ldapContext.modifyAttributes(ldapUser.getDn(), DirContext.REPLACE_ATTRIBUTE, attrs);

         fireMembershipChangedEvent(user, roles);
      }
      catch (NamingException e)
      {
         throw new IdentityException("Failed to change Role members", e);
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

   public Set findRoleMembers(String roleName, int offset, int limit, String userNameFilter) throws IdentityException
   {

      Role role = getRoleModule().findRoleByName(roleName);
      //if exception was thrown - propagate it, if not....
      if (role != null)
      {
         Set users = getUsers(role, userNameFilter);



         int size = users.size();

         if (offset == 0 && size <= limit)
         {
            return users;
         }

         Collections.sort(Tools.toList(users.iterator()), new LDAPUserImpl.LDAPUserComparator());

         if (offset + limit <= size)
         {
            return Tools.toSet(Tools.toList(users.iterator()).subList(offset, offset + limit).iterator());
         }
         else if (offset >= size)
         {
            return new HashSet();
         }


         return Tools.toSet(Tools.toList(users.iterator()).subList(offset, size).iterator());
      }
      else
      {
         throw new IdentityException("Role not found with roleName: " + roleName );
      }
   }
}
