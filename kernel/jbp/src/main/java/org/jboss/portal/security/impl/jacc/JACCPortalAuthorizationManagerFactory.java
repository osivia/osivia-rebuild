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

import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.PortalPermission;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.security.jacc.DelegatingPolicy;
import org.jboss.security.jacc.SubjectPolicyContextHandler;

import javax.security.jacc.PolicyContext;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JACCPortalAuthorizationManagerFactory extends AbstractJBossService
   implements PortalAuthorizationManagerFactory
{

   /** . */
   private AuthorizationDomainRegistry authorizationDomainRegistry;

   /** . */
   private final JACCPortalAuthorizationManager manager = new JACCPortalAuthorizationManager(this);

   /** The configured roles. */
   final Map configuredRoles = new HashMap();

   /** JACC bypass */
   private SecurityContext securityContext = null;

   public AuthorizationDomainRegistry getAuthorizationDomainRegistry()
   {
      return authorizationDomainRegistry;
   }

   public void setAuthorizationDomainRegistry(AuthorizationDomainRegistry authorizationDomainRegistry)
   {
      this.authorizationDomainRegistry = authorizationDomainRegistry;
   }

   public PortalAuthorizationManager getManager()
   {
      JACCPortalAuthorizationManager manager = new JACCPortalAuthorizationManager(this);

      if (this.securityContext == null)
      {
         this.securityContext = new SecurityContext();
      }

      manager.setSecurityContext(this.securityContext);
      return manager;
   }

   /** Set the PolicyContext subject security handler and the delegating policy. */
   protected void startService() throws Exception
   {
      // Set up the mandatory context handler
      SubjectPolicyContextHandler handler = new SubjectPolicyContextHandler();
// FIXME OSIVIA/MIG : registerHandler not supported
//      PolicyContext.registerHandler(SubjectPolicyContextHandler.SUBJECT_CONTEXT_KEY, handler, true);

      // Setup custom policy
      Policy policy = Policy.getPolicy();
      if (policy != null && policy instanceof DelegatingPolicy)
      {
         // Just update the permission types
         log.debug("Found existing delegating policy, configuring it with with PortalPermission");
         DelegatingPolicy dp = (DelegatingPolicy)policy;
         dp.setExternalPermissionTypes(new Class[]{PortalPermission.class});
      }
      else
      {
         // New config
         log.debug("No existing delegating policy in place, adding one configured with the PortalPermission class");
         DelegatingPolicy dp = DelegatingPolicy.getInstance();
         dp.setExternalPermissionTypes(new Class[]{PortalPermission.class});
         Policy.setPolicy(dp);
         policy = dp;
      }

      // Refresh
      policy.refresh();

      //JACC bypass
      this.securityContext = new SecurityContext();
   }
}
