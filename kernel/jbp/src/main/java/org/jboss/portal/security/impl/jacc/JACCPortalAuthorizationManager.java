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
package org.jboss.portal.security.impl.jacc;

import org.jboss.logging.Logger;
import org.jboss.portal.security.PortalPermission;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.security.spi.provider.PermissionFactory;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Portal Authorization Manager based on JACC Has deep integration with the JBossSX Jacc Layer.
 *
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @since Jan 30, 2006
 */
public class JACCPortalAuthorizationManager
   implements PortalAuthorizationManager
{

   /** . */
   private static Logger log = Logger.getLogger(JACCPortalAuthorizationManager.class);

   /** . */
   private static final boolean trace = log.isTraceEnabled();

   /** . */
   private final JACCPortalAuthorizationManagerFactory factory;

   /** . */
   private PolicyConfigurationFactory pcf;

   /** Used to retrieve the subject in hte jacc portal permission collection. */
   private static final ThreadLocal checkedSubjectLocal = new ThreadLocal();

   /** JACC bypass */
   private SecurityContext securityContext = null;

   /**
    * JACC bypass
    *
    * @param securityContext
    */
   void setSecurityContext(SecurityContext securityContext)
   {
      this.securityContext = securityContext;
   }

   public JACCPortalAuthorizationManager(JACCPortalAuthorizationManagerFactory factory)
   {
      this.factory = factory;
      try
      {
         pcf = PolicyConfigurationFactory.getPolicyConfigurationFactory();
      }
      catch (Exception e)
      {
         log.error("Unable to obtain the PolicyConfigurationFactory", e);
      }
   }

   public void checkRoleConfig(String contextID, String roleName) throws PortalSecurityException
   {
      /*Map configuredRoles = factory.configuredRoles;
      synchronized (configuredRoles)
      {
         // The policy configuration
         PolicyConfiguration pc = null;
         try
         {
            try
            {
               if (!configuredRoles.containsKey(roleName))
               {
                  // Iterate over all domains to add their container
                  Collection domains = factory.getAuthorizationDomainRegistry().getDomains();
                  for (Iterator j = domains.iterator(); j.hasNext();)
                  {
                     AuthorizationDomain domain = (AuthorizationDomain)j.next();
                     JACCPortalPermissionCollection collection = new JACCPortalPermissionCollection(roleName, domain);
                     PermissionFactory permissionFactory = domain.getPermissionFactory();
                     PortalPermission container = permissionFactory.createPermissionContainer(collection);

                     if (pc == null)
                     {
                        pc = pcf.getPolicyConfiguration(contextID, false);
                     }

                     if (SecurityConstants.UNCHECKED_ROLE_NAME.equals(roleName))
                     {
                        pc.addToUncheckedPolicy(container);
                     }
                     else
                     {
                        pc.addToRole(roleName, container);
                     }
                  }

                  //
                  configuredRoles.put(roleName, roleName);
               }
            }
            catch (PolicyContextException e)
            {
               throw new PortalSecurityException(e);
            }
         }
         finally
         {
            // JBossSX implies check does not happen until the PC has committed
            // and the policy has been refreshed
            try
            {
               if (pc != null && !pc.inService())
               {
                  pc.commit();
                  Policy.getPolicy().refresh();
               }
            }
            catch (PolicyContextException e)
            {
               log.error("Error when commiting policy config", e);
            }
         }
      }*/

      /**
       * JACC bypass
       */
      Map configuredRoles = factory.configuredRoles;
      synchronized (configuredRoles)
      {
         if (!configuredRoles.containsKey(roleName))
         {
            // Iterate over all domains to add their container
            Collection domains = factory.getAuthorizationDomainRegistry().getDomains();
            for (Iterator j = domains.iterator(); j.hasNext();)
            {
               AuthorizationDomain domain = (AuthorizationDomain)j.next();
               JACCPortalPermissionCollection collection = new JACCPortalPermissionCollection(roleName, domain);
               PermissionFactory permissionFactory = domain.getPermissionFactory();
               PortalPermission container = permissionFactory.createPermissionContainer(collection);

               if (SecurityConstants.UNCHECKED_ROLE_NAME.equals(roleName))
               {
                  this.securityContext.addToUncheckedPolicy(container);
               }
               else
               {
                  this.securityContext.addToRole(roleName, container);
               }
            }
            configuredRoles.put(roleName, roleName);
         }
      }
   }

   /**
    * @throws IllegalArgumentException if the permission is null
    * @throws PortalSecurityException
    */
   private boolean internalCheckPermission(PortalPermission permission) throws IllegalArgumentException, PortalSecurityException
   {
      if (permission == null)
      {
         throw new IllegalArgumentException("No null permission can be checked");
      }

      // Get the current context id.
      String contextID = PolicyContext.getContextID();

      //
      if (contextID == null)
      {
         throw new PortalSecurityException("No policy context id");
      }

      // Get the current authenticated subject through the JACC contract
      Subject currentSubject = (Subject)checkedSubjectLocal.get();

      //
      Principal[] principals;

      // Always check the unckeded permission container
      checkRoleConfig(contextID, SecurityConstants.UNCHECKED_ROLE_NAME);

      //
      if (currentSubject != null)
      {
         Set tmp = currentSubject.getPrincipals(JACCPortalPrincipal.class);
         JACCPortalPrincipal pp = null;
         for (Iterator i = tmp.iterator(); i.hasNext();)
         {
            pp = (JACCPortalPrincipal)i.next();
            if (pp != null)
            {
               break;
            }
         }
         if (pp == null)
         {
            pp = new JACCPortalPrincipal(currentSubject);
            tmp.add(pp);

            // Lazy create all the permission containers for the given role names
            for (Iterator i = pp.getRoles().iterator(); i.hasNext();)
            {
               Principal role = (Principal)i.next();
               checkRoleConfig(contextID, role.getName());

               if (trace)
               {
                  log.trace("Internal check. Contains role: " + role.getName());
               }
            }
         }
         principals = pp.getPrincipals();
      }
      else
      {
         principals = new Principal[0];
      }

      //Bypass the JACC layer and go straight to the portal security permission layer
      /*ProtectionDomain pd = new ProtectionDomain(null, null, null, principals);
      Policy policy = Policy.getPolicy();
      boolean implied = policy.implies(pd, permission);*/

      /**
       * alternately bypassing the call and not letting it go through the 
       * JACC system.
       *
       * There is not an issue with JBoss JACC implementation, but the Permissions object
       * in JDK5 which is leveraged by JACC. The synchrnozied block inside Permissions object
       * is causing performance bottleneck
       */
      ProtectionDomain pd = new ProtectionDomain(null, null, null, principals);
      boolean implied = this.securityContext.implies(pd, permission);

      return implied;
   }


   public boolean checkPermission(Subject checkedSubject, PortalPermission permission) throws IllegalArgumentException, PortalSecurityException
   {
      try
      {
         // Set the subject for later use in that layer
         checkedSubjectLocal.set(checkedSubject);


         if (trace && checkedSubject != null)
         {
            for (Principal principal : checkedSubject.getPrincipals())
            {
               log.trace("Principal name: " + principal.getName());
            }

         }
         //
         if (trace)
         {
            log.trace("hasPermission:name=" + permission.getName() + "uri=" + permission.getURI() + "::actions=" + permission.getActions() + "::type=" + permission.getType());
         }

         //
         boolean result = internalCheckPermission(permission);

         //
         if (trace)
         {
            log.trace("hasPermission:result=" + result);
         }

         //
         return result;
      }
      finally
      {
         checkedSubjectLocal.set(null);
      }
   }

   public boolean checkPermission(PortalPermission permission) throws IllegalArgumentException, PortalSecurityException
   {
      try
      {
         // Get the current authenticated subject through the JACC contract
         Subject subject = (Subject)PolicyContext.getContext("javax.security.auth.Subject.container");

         //
         return checkPermission(subject, permission);
      }
      catch (PolicyContextException e)
      {
         throw new PortalSecurityException(e);
      }
   }

   static Subject getCheckedSubject()
   {
      return (Subject)checkedSubjectLocal.get();
   }
}
