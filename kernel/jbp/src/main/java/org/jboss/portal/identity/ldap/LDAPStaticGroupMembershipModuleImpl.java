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
public class LDAPStaticGroupMembershipModuleImpl extends LDAPMembershipModule
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPStaticGroupMembershipModuleImpl.class);
   

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
      try
      {

         log.debug("getRoles(): user DN = " + ldapUser.getDn());

         String memberName = "";

         if (isUidAttributeIsDN())
         {
            memberName = ldapUser.getDn();
         }
         else
         {
            memberName = ldapUser.getUserName();
         }

         
         String filter = getMemberAttributeID().concat("=").concat(memberName);
         log.debug("Search filter: " + filter);

         List sr = getRoleModule().searchRoles(filter, null);


         for (Iterator iterator = sr.iterator(); iterator.hasNext();)
         {
            SearchResult res = (SearchResult)iterator.next();
            DirContext ctx = (DirContext)res.getObject();
            roles.add(getRoleModule().createRoleInstance(res.getAttributes(),ctx.getNameInNamespace()));
            ctx.close();
         }



      }
      catch (Exception e)
      {
         log.debug("Failed to resolve userRoles: " + ldapUser.getId().toString(), e);
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

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findUsers(): role = " + ldapRole.getDn());

         if (ldapRole.getName() == null)
         {
            throw new IdentityException("Role name canot be null");
         }

         //obtain Role entry attributes from directory
         Attributes attrs = ldapContext.getAttributes(ldapRole.getDn(), new String[] {getMemberAttributeID()});

         //log.debug("Role attributes: " + attrs);
         if (attrs == null)
         {
            throw new IdentityException("Cannot find Role with DN: " + ldapRole.getDn());
         }

         //iterate over user names belonging to this role
         Attribute memberAttr = attrs.get(getMemberAttributeID());

         if (memberAttr != null)
         {

            NamingEnumeration values = memberAttr.getAll();
         
            while (values.hasMoreElements())
            {
               String value = values.nextElement().toString();
               String name = value;

               if (userNameFilter != null && userNameFilter.length() != 0 && !name.matches(".*" + userNameFilter + ".*"))
               {
                  continue;
               }

               try
               {
                  //if user is pointed as DN get only it's name
                  if (isUidAttributeIsDN())
                  {
                     users.add(getUserModule().findUserByDN(name));
                  }
                  else
                  {
                     users.add(getUserModule().findUserByUserName(name));
                  }
               }
               catch(IdentityException ie)
               {
                  log.error("Failed to find user: " + name + "/" + value, ie);

               }
            }
         }
      }
      catch (NamingException e)
      {
         throw new IdentityException("Resolving Role Users failed.", e);
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



      if (users.size() == 0 && isMembershipAttributeRequired())
      {
         throw new IdentityException("Cannot assigne 0 users to a role using this membership strategy (because some LDAPs " +
            "require the member field to be set). ");
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {
         log.debug("findUsers(): role = " + ldapRole.getDn());

         if (ldapRole.getName() == null)
         {
            throw new IdentityException("Role name canot be null");
         }

         //construct new member attribute values
         Attributes attrs = new BasicAttributes(true);

         Attribute member = new BasicAttribute(getMemberAttributeID());
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

               if (isUidAttributeIsDN())
               {
                  member.add(ldapUser.getDn());
               }
               else
               {
                  //member.add(user.getId().toString());
                  member.add(ldapUser.getUserName());
               }
            }
            catch (ClassCastException e)
            {
               throw new IdentityException("Can add only LDAPUserImpl objects", e);
            }
         }
         attrs.put(member);

         if (users.size() > 0)
         {
            ldapContext.modifyAttributes(ldapRole.getDn(), DirContext.REPLACE_ATTRIBUTE, attrs);
         }
         else
         {
            ldapContext.modifyAttributes(ldapRole.getDn(), DirContext.REMOVE_ATTRIBUTE, attrs);
         }
         fireMembershipChangedEvent(role, users);
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

      //First build a list of roles DNs to add
      List roleDNsToAdd = new LinkedList();

      for (Iterator iterator = roles.iterator(); iterator.hasNext();)
      {
         try
         {
            LDAPRoleImpl role = (LDAPRoleImpl)iterator.next();
            roleDNsToAdd.add(role.getDn());
         }
         catch(ClassCastException e)
         {
            throw new IdentityException("Only can add LDAPRoleImpl objects", e);
         }
      }

      String memberName=null;

      //Find all the roles that currently contain user as member (need to remove user from some of them)
      if (isUidAttributeIsDN())
      {
         memberName = ldapUser.getDn();
      }
      else
      {
         //memberName = ldapUser.getId().toString();
         memberName = ldapUser.getUserName();
      }

      LdapContext ldapContext = getConnectionContext().createInitialContext();

      try
      {

         String filter = getMemberAttributeID().concat("=").concat(memberName);
         log.debug("Search filter: " + filter);

         List sr = getRoleModule().searchRoles(filter, null);
         //iterate over roles that contain a user
         for (Iterator iterator = sr.iterator(); iterator.hasNext();)
         {
            SearchResult res = (SearchResult)iterator.next();
            DirContext ctx = (DirContext)res.getObject();
            String roleDN = ctx.getNameInNamespace();
            ctx.close();
            
            //if role is one which we want to add
            if (roleDNsToAdd.contains(roleDN))
            {
               //we do nothing but mark this role as added
               roleDNsToAdd.remove(roleDN);
               continue;
            }
            //if it's not on the list we need to remove user from it
            else
            {
               //obtain Role entry attributes from directory
               Attributes attrs = ldapContext.getAttributes(roleDN, new String[] {getMemberAttributeID()});

               //log.debug("Role attributes: " + attrs);
               if (attrs == null)
               {
                  throw new IdentityException("Cannot find Role with DN: " + roleDN);
               }

               Attribute attr = attrs.get(getMemberAttributeID());

               //can't remove the last member (if the attribute is required by schema)
               //TODO: workaround this somehow.... (adding goofy user or admin instead?)
               if (!(attr.size() == 1 && isMembershipAttributeRequired()))
               {
                  //remove user name from the member list
                  attr.remove(memberName);

                  //and replace attributes
                  Attributes newAttrs = new BasicAttributes(true);
                  //newAttrs.put(getMemberAttributeID(), attr);
                  newAttrs.put(attr);
                  ldapContext.modifyAttributes(roleDN, DirContext.REPLACE_ATTRIBUTE, newAttrs);
               }
               else
               {
                  log.error("Couldn't remove user from role as it was the last member - possibly required field in ldap");
               }

               //and mark this role as done
               roleDNsToAdd.remove(roleDN);
            }
         }

         //now iterate over roles that left to process
         for (Iterator iterator = roleDNsToAdd.iterator(); iterator.hasNext();)
         {
            String roleDN = (String)iterator.next();

            //changes to make
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
               new BasicAttribute(getMemberAttributeID(), memberName));
            // Perform the requested modifications on the named object
            ldapContext.modifyAttributes(roleDN, mods);
         }

         fireMembershipChangedEvent(user, roles);

         //and that should be all...
      }
      catch (NamingException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
      //throw new UnsupportedOperationException("Not yet implemented");
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
