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
package org.jboss.portal.portlet.impl.security;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import org.jboss.portal.portlet.security.PortletPermission;
import org.jboss.portal.portlet.security.PortletSecurityService;
import org.jboss.portal.security.PortalPermission;
import org.jboss.portal.security.PortalPermissionCollection;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.security.spi.provider.PermissionFactory;
import org.jboss.portal.security.spi.provider.PermissionRepository;
import org.jboss.portal.security.spi.provider.SecurityConfigurationException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Service that configures security for portlets.
 *
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @version $Revision: 8784 $
 * @since Mar 17, 2006
 */
public class JBossPortletSecurityService implements PortletSecurityService,
   AuthorizationDomain, DomainConfigurator, PermissionRepository, PermissionFactory
{

   /** . */
   protected ConcurrentReaderHashMap securityConstraintsMap;

   /** . */
   protected PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

   /** . */
   protected JBossAuthorizationDomainRegistry authorizationDomainRegistry;

   public AuthorizationDomain getAuthorizationDomain()
   {
      return this;
   }

   //*************************************************************
   //   AuthorizationDomain Interface Methods
   //*************************************************************
   public String getType()
   {
      return PortletPermission.PERMISSION_TYPE;
   }

   public DomainConfigurator getConfigurator()
   {
      return this;
   }

   public PermissionRepository getPermissionRepository()
   {
      return this;
   }

   public PermissionFactory getPermissionFactory()
   {
      return this;
   }

   public Set getSecurityBindings(String uri)
   {
      return (Set)securityConstraintsMap.get(uri);
   }

   public void setSecurityBindings(String uri, Set securityBindings) throws SecurityConfigurationException
   {
      this.securityConstraintsMap.put(uri, securityBindings);
   }

   public void removeSecurityBindings(String uri) throws SecurityConfigurationException
   {
      this.securityConstraintsMap.remove(uri);
   }

   public PortalPermission getPermission(String roleName, String uri) throws PortalSecurityException
   {
      Set set = getSecurityBindings(uri);
      if (set != null && !set.isEmpty())
      {
         for (Iterator i = set.iterator(); i.hasNext();)
         {
            RoleSecurityBinding sc = (RoleSecurityBinding)i.next();
            String constraintRoleName = sc.getRoleName();
            if (constraintRoleName.equals(roleName) ||
               SecurityConstants.UNCHECKED_ROLE_NAME.equals(constraintRoleName))
            {
               return createPermission(uri, sc.getActions());
            }
         }
      }
      return null;
   }

   public PortalPermission createPermissionContainer(PortalPermissionCollection collection) throws PortalSecurityException
   {
      return new PortletPermission(collection);
   }

   public PortalPermission createPermission(String uri, String action) throws PortalSecurityException
   {
      return new PortletPermission(uri, action);
   }

   public PortalPermission createPermission(String uri, Collection actions) throws PortalSecurityException
   {
      return new PortletPermission(uri, actions);
   }

   public void create() throws Exception
   {
      this.securityConstraintsMap = new ConcurrentReaderHashMap();
   }

   public void start() throws Exception
   {
      // Add ourself as the authorization domain
      authorizationDomainRegistry.addDomain(this);
   }

   public void stop() throws Exception
   {
      authorizationDomainRegistry.removeDomain(this);
   }

   public void destroy()
   {
      securityConstraintsMap = null;
   }

   public JBossAuthorizationDomainRegistry getAuthorizationDomainRegistry()
   {
      return authorizationDomainRegistry;
   }

   public void setAuthorizationDomainRegistry(JBossAuthorizationDomainRegistry authorizationDomainRegistry)
   {
      this.authorizationDomainRegistry = authorizationDomainRegistry;
   }

   public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory()
   {
      return portalAuthorizationManagerFactory;
   }

   public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory)
   {
      this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
   }
}
