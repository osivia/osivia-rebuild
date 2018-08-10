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

package org.jboss.portal.identity.auth;

import org.jboss.security.auth.spi.UsernamePasswordLoginModule;
import org.jboss.security.SimpleGroup;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.Role;
import org.jboss.portal.common.transaction.Transactions;

import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import java.security.acl.Group;
import java.security.Principal;
import java.util.Map;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class SynchronizingLoginModule extends UsernamePasswordLoginModule
{

private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(SynchronizingLoginModule.class);

   protected String additionalRole;
   protected String defaultAssignedRole;
   protected String synchronizeIdentity;
   protected String synchronizeRoles;
   protected String userModuleJNDIName;
   protected String roleModuleJNDIName;
   protected String membershipModuleJNDIName;
   protected String userProfileModuleJNDIName;
   protected String preserveRoles;

   private transient SimpleGroup userRoles = new SimpleGroup("Roles");

   private transient String password;


   private UserModule userModule;
   private RoleModule roleModule;
   private MembershipModule membershipModule;
   private UserProfileModule userProfileModule;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);

      password = null;

      userModuleJNDIName = (String)options.get("userModuleJNDIName");
      roleModuleJNDIName = (String)options.get("roleModuleJNDIName");
      membershipModuleJNDIName = (String)options.get("membershipModuleJNDIName");
      userProfileModuleJNDIName = (String)options.get("userProfileModuleJNDIName");
      additionalRole = (String)options.get("additionalRole");
      synchronizeIdentity = (String)options.get("synchronizeIdentity");
      synchronizeRoles = (String)options.get("synchronizeRoles");
      defaultAssignedRole = (String)options.get("defaultAssignedRole");
      preserveRoles = (String)options.get("preserveRoles");

      // Some info
      if (log.isTraceEnabled())
      {
         log.trace("additionalRole = " + additionalRole);
         log.trace("userModuleJNDIName = " + userModuleJNDIName);
         log.trace("roleModuleJNDIName = " + roleModuleJNDIName);
         log.trace("membershipModuleJNDIName = " + membershipModuleJNDIName);
         log.trace("userProfileModuleJNDIName = " + userProfileModuleJNDIName);
         log.trace("synchronizeIdentity = " + synchronizeIdentity);
         log.trace("synchronizeRoles = " + synchronizeRoles);
         log.trace("defaultAssignedRole = " + defaultAssignedRole);
         log.trace("preserveRoles = " + preserveRoles);
      }
   }

   protected UserModule getUserModule() throws Exception
   {
      if (userModule == null)
      {
         userModule = (UserModule)new InitialContext().lookup(userModuleJNDIName);
      }
      if (userModule == null)
      {
         throw new IdentityException("Cannot obtain UserModule using JNDI name:" + userModuleJNDIName);
      }

      return userModule;
   }

   protected RoleModule getRoleModule() throws Exception
   {

      if (roleModule == null)
      {
         roleModule = (RoleModule)new InitialContext().lookup(roleModuleJNDIName);
      }
      if (roleModule == null)
      {
         throw new IdentityException("Cannot obtain RoleModule using JNDI name:" + roleModuleJNDIName);
      }
      return roleModule;
   }

   protected MembershipModule getMembershipModule() throws Exception
   {

      if (membershipModule == null)
      {
         membershipModule = (MembershipModule)new InitialContext().lookup(membershipModuleJNDIName);
      }
      if (membershipModule == null)
      {
         throw new IdentityException("Cannot obtain MembershipModule using JNDI name:" + membershipModuleJNDIName);
      }
      return membershipModule;
   }

   protected UserProfileModule getUserProfileModule() throws Exception
   {

      if (userProfileModule == null)
      {
         userProfileModule = (UserProfileModule)new InitialContext().lookup(userProfileModuleJNDIName);
      }
      if (userProfileModule == null)
      {
         throw new IdentityException("Cannot obtain UserProfileModule using JNDI name:" + userProfileModuleJNDIName);
      }
      return userProfileModule;
   }

   /**
    * We must implement this
    * @return
    * @throws LoginException
    */
   protected String getUsersPassword() throws LoginException
   {
      return password;
   }


   /**
    * This always returns true - so this module always pass.
    * @param inputPassword
    * @param string1
    * @return
    */
   protected boolean validatePassword(String inputPassword, String string1)
   {
      // Save the password for the synchro stuff
      password = inputPassword;
      return true;
   }

   /** Subclass to use the PortalPrincipal to make the username easier to retrieve by the portal. */
   protected Principal createIdentity(String username) throws Exception
   {
      return new UserPrincipal(username);
   }

   protected Group[] getRoleSets() throws LoginException
   {
      //Group group = new SimpleGroup("Roles");
      if (additionalRole != null)
      {
         try
         {
            userRoles.addMember(createIdentity(additionalRole));
         }
         catch (Exception e)
         {
            //just a try
            log.error("Error when adding additional role: ", e);
         }
      }
      if (defaultAssignedRole != null)
      {
         try
         {
            userRoles.addMember(createIdentity(defaultAssignedRole));
         }
         catch (Exception e)
         {
            //just a try
            log.error("Error when adding additional role: ", e);
         }
      }

      Group[] roleSets = {userRoles};
      return roleSets;
   }


   public boolean commit() throws LoginException
   {
      if (isSynchronizeIdentity())
      {
         try
         {
            performSynchronization(getUsername(), getUsersPassword());
         }
         catch (Throwable e)
         {
            log.warn("Failed to sychronize identity of user: " + getUsername(), e);
         }
      }


      return super.commit();
   }

   private void performSynchronization(final String name, final String password) throws Exception
   {

      log.debug("$$Synchronizing user: " + name);

      try
      {
         TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
         Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               try
               {
                  User user = null;
                  //check if user exist
                  try
                  {

                     user = getUserModule().findUserByUserName(name);

                     //synchronize password from LDAP to DB
                     if (!user.validatePassword(password))
                     {
                        user.updatePassword(password);
                     }
                  }
                  catch (Exception e)
                  {
                     // nothing as user can simply not exist
                  }

                  //if not try to synchronize it
                  if (user == null)
                  {
                     user = getUserModule().createUser(name, password);
                     getUserProfileModule().setProperty(user, User.INFO_USER_ENABLED, Boolean.TRUE);

                  }

                  Set rolesToAssign = new HashSet();

                  //now check and try synchronize all the roles
                  if (isSynchronizeRoles())
                  {
                     //obtain user principals
                     Set principals = subject.getPrincipals();
                     Group roles = null;
                     for (Iterator iterator = principals.iterator(); iterator.hasNext();)
                     {
                        Object o = iterator.next();
                        if (!(o instanceof Group))
                        {
                           continue;
                        }
                        Group group = (Group)o;

                        if (group.getName().equals("Roles"))
                        {
                           roles = group;
                           break;
                        }
                     }

                     if (roles != null)
                     {

                        if (log.isDebugEnabled())
                        {
                           log.debug("$$Processing Group: " + roles.getName());
                           Enumeration xx = roles.members();
                           while (xx.hasMoreElements())
                           {
                              Principal o = (Principal)xx.nextElement();
                              log.debug("$$Principal in group: " + o.getName() + "; " + o.toString());
                           }
                        }

                        //based on code implementation its just one SimpleGroup called "Roles"

                        Enumeration en = roles.members();
                        while (en.hasMoreElements())
                        {
                           Principal p = (Principal)en.nextElement();
                           String roleName = p.getName();
                           log.debug("$$Processing role principal object related to current user: " + roleName);
                           //check if such role is present

                           Role role = null;
                           try
                           {
                              role  = getRoleModule().findRoleByName(roleName);
                           }
                           catch (Exception e)
                           {
                              //
                           }

                           if (role == null)
                           {
                              try
                              {
                                 role = getRoleModule().createRole(roleName, roleName);
                              }
                              catch (Throwable e)
                              {
                                 log.warn("Error when trying to synchronize role: " + roleName, e);
                                 continue;
                              }
                           }

                           rolesToAssign.add(role);
                           userRoles.addMember(createIdentity(role.getName()));
                        }
                     }
                  }

                  if (defaultAssignedRole != null)
                  {
                     try
                     {
                        rolesToAssign.add(getRoleModule().findRoleByName(defaultAssignedRole));
                     }
                     catch(Exception e)
                     {
                        //
                        log.warn("Cannot find defaultAssignedRole: " + defaultAssignedRole, e);
                     }
                  }

                  if (rolesToAssign.size() > 0)
                  {
                     // If we don't want to overwrite roles assignemts already present in identity store
                     if ( isPreserveRoles() || !isSynchronizeRoles())
                     {
                        Set presentRoles = getMembershipModule().getRoles(user);
                        if (presentRoles != null)
                        {
                           rolesToAssign.addAll(presentRoles);
                           for (Object presentRole : presentRoles)
                           {
                              userRoles.addMember(createIdentity(((Role)presentRole).getName()));
                           }
                        }
                     }

                     getMembershipModule().assignRoles(user, rolesToAssign);
                  }

                  return null;

               }
               catch (Exception e)
               {
                  throw new LoginException(e.toString());
               }
            }
         });
      }
      catch (Exception e)
      {
         Throwable cause = e.getCause();
         throw new LoginException(cause.toString());
      }
   }

   protected boolean isSynchronizeIdentity()
   {
      if (synchronizeIdentity != null && synchronizeIdentity.equalsIgnoreCase("false"))
      {
            return Boolean.FALSE.booleanValue();
      }
      return Boolean.TRUE.booleanValue();
   }

   protected boolean isSynchronizeRoles()
   {
      if (synchronizeRoles != null && synchronizeRoles.equalsIgnoreCase("false"))
      {
            return Boolean.FALSE.booleanValue();
      }
      return Boolean.TRUE.booleanValue();
   }

   protected boolean isPreserveRoles()
   {
      if (preserveRoles != null && preserveRoles.equalsIgnoreCase("true"))
      {
            return Boolean.TRUE.booleanValue();
      }
      return Boolean.FALSE.booleanValue();
   }
}
