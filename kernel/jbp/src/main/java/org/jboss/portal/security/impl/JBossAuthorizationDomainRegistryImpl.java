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
package org.jboss.portal.security.impl;

import org.jboss.portal.common.util.CopyOnWriteRegistry;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;

import java.util.Collection;

/**
 * MBean to allow access to the policy config service and the policy configs stored within.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossAuthorizationDomainRegistryImpl extends AbstractJBossService implements JBossAuthorizationDomainRegistry
{

   /** A map of permission types to stores. */
   private CopyOnWriteRegistry domains = new CopyOnWriteRegistry();

   public void addDomain(AuthorizationDomain domain)
   {
      if (domain == null)
      {
         throw new IllegalArgumentException("Authorization Domain is null");
      }
      log.debug("Add authorization domain " + domain.getType());
      domains.register(domain.getType(), domain);
   }

   public void removeDomain(AuthorizationDomain domain)
   {
      if (domain == null)
      {
         throw new IllegalArgumentException();
      }
      log.debug("Remove authorization domain " + domain.getType());
      domains.unregister(domain.getType());
   }

   public AuthorizationDomain getDomain(String domainType)
   {
      return (AuthorizationDomain)domains.getRegistration(domainType);
   }

   public Collection getDomains()
   {
      return domains.getRegistrations();
   }
}
