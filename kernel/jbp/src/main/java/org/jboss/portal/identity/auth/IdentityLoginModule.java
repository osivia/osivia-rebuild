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
package org.jboss.portal.identity.auth;

import org.jboss.portal.common.transaction.Transactions;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.UserStatus;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionManager;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A login module that uses the user module.
 * 
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author <a href="mailto:sshah@redhat.com">Sohil Shah</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6803 $
 */
public class IdentityLoginModule extends UsernamePasswordLoginModule
{

   protected String userModuleJNDIName;

   protected String roleModuleJNDIName;

   protected String userProfileModuleJNDIName;

   protected String membershipModuleJNDIName;

   protected String additionalRole;

   protected String havingRole;

   protected String validateUserNameCase;

   protected String userNameToLowerCase;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);

      // Get data
      userModuleJNDIName = (String) options.get("userModuleJNDIName");
      roleModuleJNDIName = (String) options.get("roleModuleJNDIName");
      userProfileModuleJNDIName = (String) options
            .get("userProfileModuleJNDIName");
      membershipModuleJNDIName = (String) options
            .get("membershipModuleJNDIName");
      additionalRole = (String) options.get("additionalRole");
      havingRole = (String) options.get("havingRole");
      validateUserNameCase = (String) options.get("validateUserNameCase");
      userNameToLowerCase = (String) options.get("userNameToLowerCase");

      // Some info
      log.trace("userModuleJNDIName = " + userModuleJNDIName);
      log.trace("roleModuleJNDIName = " + roleModuleJNDIName);
      log.trace("userProfileModuleJNDIName = " + userProfileModuleJNDIName);
      log.trace("membershipModuleJNDIName = " + membershipModuleJNDIName);
      log.trace("additionalRole = " + additionalRole);
      log.trace("havingRole = " + havingRole);
      log.trace("validateUserNameCase = " + validateUserNameCase);
      log.trace("userNameToLowerCase = " + userNameToLowerCase);
   }

   private UserModule userModule;

   private RoleModule roleModule;

   private UserProfileModule userProfileModule;

   private MembershipModule membershipModule;

   protected UserModule getUserModule() throws NamingException
   {
      if (userModule == null)
      {
         userModule = (UserModule)new InitialContext().lookup(userModuleJNDIName);
      }
      return userModule;
   }

   protected RoleModule getRoleModule() throws NamingException
   {

      if (roleModule == null)
      {
         roleModule = (RoleModule)new InitialContext().lookup(roleModuleJNDIName);
      }
      return roleModule;
   }

   protected UserProfileModule getUserProfileModule() throws NamingException
   {

      if (userProfileModule == null) {
         userProfileModule = (UserProfileModule) new InitialContext()
               .lookup(userProfileModuleJNDIName);
      }
      return userProfileModule;
   }

   protected MembershipModule getMembershipModule() throws NamingException
   {

      if (membershipModule == null)
      {
         membershipModule = (MembershipModule)new InitialContext().lookup(membershipModuleJNDIName);
      }
      return membershipModule;
   }

   protected String getUsersPassword() throws LoginException
   {
      return "";
   }

   protected boolean validatePassword(final String inputPassword, String expectedPassword)
   {

      HttpServletRequest request = null;
      try
      {
         request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
      }
      catch(Exception e)
      {
         log.error(this,e);
         throw new RuntimeException(e);
      }

      Object ssoSuccess = request.getAttribute("ssoSuccess");
      if(ssoSuccess != null)
      {
         return true;
      }

      if (inputPassword != null)
      {
         try
         {
            try
            {

               UserStatus userStatus = getUserStatus(inputPassword);

               // Set the user Status in the request so that the login page can show an error message accordingly
               request.setAttribute("org.jboss.portal.userStatus", userStatus);
               
               if (userStatus == UserStatus.DISABLE)
               {
                  request.setAttribute("org.jboss.portal.loginError", "Your account is disabled");
                  return false;
               }
               else if (userStatus == UserStatus.NOTASSIGNEDTOROLE)
               {
                  request.setAttribute("org.jboss.portal.loginError", "The user doesn't have the correct role");
                  return false;
               }
               else if ((userStatus == UserStatus.UNEXISTING) || userStatus == UserStatus.WRONGPASSWORD)
               {
                  request.setAttribute("org.jboss.portal.loginError", "The user doesn't exist or the password is incorrect");
                  return false;
               }
               else if (userStatus == UserStatus.OK)
               {
                  return true;
               }
               else
               {
                  log.error("Unexpected error while logging in");
                  return false;
               }            }
            catch (Exception e)
            {
               log.error("Error when validating password", e);
            }
         }
         catch (Exception e)
         {
            log.debug("Failed to validate password", e);
         }
      }
      return false;
   }

   protected UserStatus getUserStatus(final String inputPassword)
   {
      UserStatus result = UserStatus.OK;

      try {
         TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
         UserStatus tmp = (UserStatus)Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               try
               {
                  User user = getUserModule().findUserByUserName(getUsername());
                  // in case module implementation doesn't throw proper
                  // exception...
                  if (user == null)
                  {
                     throw new NoSuchUserException("UserModule returned null user object");  
                  }

                  //This is because LDAP binds can be non case sensitive
                  if (validateUserNameCase != null && validateUserNameCase.equalsIgnoreCase("true")
                     && !getUsername().equals(user.getUserName()))
                  {
                     return UserStatus.UNEXISTING;
                  }

                  boolean enabled = false;
                  try {
                     Object enabledS;
                     enabledS = getUserProfileModule().getProperty(user,
                           User.INFO_USER_ENABLED);
                     if (enabledS != null) {
                        enabled = new Boolean(enabledS.toString());
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
                  if (!enabled) {
                     return UserStatus.DISABLE;
                  }
                  if (havingRole != null)
                  {
                     boolean hasTheRole = false;
                     Set roles = getMembershipModule().getRoles(user);
                     for (Iterator i = roles.iterator(); i.hasNext();)
                     {
                        Role role = (Role)i.next();
                        if (havingRole.equals(role.getName()))
                        {
                           hasTheRole = true;
                           break;
                        }
                     }
                     if (!hasTheRole)
                     {
                        return UserStatus.NOTASSIGNEDTOROLE;
                     }
                  }
                  if (!user.validatePassword(inputPassword))
                  {
                     return UserStatus.WRONGPASSWORD;
                  }
               }
               catch (NoSuchUserException e1)
               {
                  return UserStatus.UNEXISTING;
               }
               catch (Exception e)
               {
                  throw new LoginException(e.toString());
               }
               return null;
            }
         });
         if (tmp != null)
         {
            result = tmp;
         }
      } catch (NamingException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      return result;
   }

   protected Group[] getRoleSets() throws LoginException
   {
      try {
         TransactionManager tm = (TransactionManager) new InitialContext()
               .lookup("java:/TransactionManager");
         return (Group[]) Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               Group rolesGroup = new SimpleGroup("Roles");

               //
               if (additionalRole != null) {
                  rolesGroup.addMember(createIdentity(additionalRole));
               }

               try {
                  User user = getUserModule().findUserByUserName(getUsername());
                  Set roles = getMembershipModule().getRoles(user);

                  //

                  for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
                     Role role = (Role) iterator.next();
                     String roleName = role.getName();
                     try {
                        Principal p = createIdentity(roleName);
                        rolesGroup.addMember(p);
                     } catch (Exception e) {
                        log.debug("Failed to create principal " + roleName, e);
                     }
                  }


               } catch (Exception e) {
                  throw new LoginException(e.toString());
               }
               //
               return new Group[] { rolesGroup };
            }
         });
      } catch (Exception e) {
         Throwable cause = e.getCause();
         throw new LoginException(cause.toString());
      }
   }

   /** Subclass to use the PortalPrincipal to make the username easier to retrieve by the portal. */
   protected Principal createIdentity(String username) throws Exception
   {
      return new UserPrincipal(username);
   }

   protected String getUsername()
   {
      if (userNameToLowerCase != null && userNameToLowerCase.equalsIgnoreCase("true"))
      {
         return super.getUsername().toLowerCase();   
      }
      return super.getUsername();
   }

   protected String[] getUsernameAndPassword() throws LoginException
   {
      String[] names =  super.getUsernameAndPassword();

      if (userNameToLowerCase != null && userNameToLowerCase.equalsIgnoreCase("true"))
      {
         if (names[0] != null)
         {
            names[0] = names[0].toLowerCase();
         }
      }
      return names;

   }
}
