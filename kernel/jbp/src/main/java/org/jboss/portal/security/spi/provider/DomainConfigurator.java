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
package org.jboss.portal.security.spi.provider;

import java.util.Set;

/**
 * Main interface to configure the policy for a specified permission type and manage security of portal resources.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public interface DomainConfigurator
{
   /**
    * Return the set of constraints for the given uri.
    *
    * @param uri
    * @return a set of SecurityConstraint containing the roles and actions allowed for the resource
    */
   Set getSecurityBindings(String uri);

   /**
    * Add the provided constraints to this policy configuration
    *
    * @param uri
    * @param securityBindings
    */
   void setSecurityBindings(String uri, Set securityBindings) throws SecurityConfigurationException;

   /**
    * Remove the contstraints for the provided uri
    *
    * @param uri the identifier of the secured resource
    */
   void removeSecurityBindings(String uri) throws SecurityConfigurationException;
}
