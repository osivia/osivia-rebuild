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
package org.jboss.portal.security;

import org.apache.log4j.Logger;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.security.spi.provider.PermissionRepository;

import javax.security.auth.Subject;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;

/**
 * This class is a litteral reference to a portal permission repository. Subclasses should implement the implies logic
 * and leverage the repository to get the role.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class PortalPermissionCollection extends PermissionCollection
{

   /** . */
   private static final Logger log = Logger.getLogger(PortalPermissionCollection.class);

   /** The repository to load the permission. */
   private AuthorizationDomain domain;

   /** The owner of this collection. */
   PortalPermission owner;

   /**
    * Create a new portal permission collection.
    *
    * @param repository the repository to load the permissions from
    * @throws IllegalArgumentException if any argument is null
    */
   public PortalPermissionCollection(AuthorizationDomain repository) throws IllegalArgumentException
   {
      if (repository == null)
      {
         throw new IllegalArgumentException("Need a repository");
      }
      this.domain = repository;
   }

   /**
    * The only time this method is called is when JACC creates an instance of this object in order to add the owner
    * permission to the collection.
    *
    * @throws IllegalArgumentException if the added permission is not the owner of this collection
    */
   public final void add(Permission permission) throws IllegalArgumentException
   {
//      if (owner != permission)
//      {
//         throw new IllegalArgumentException("Should only call with the owner");
//      }
   }

   /**
    * This implementation delegates to the container permission associated with this collection the logic of the check
    * against the repository using the method PortalPermission#implies(AuthorizationDomain,String,PortalPermission).
    */
   public boolean implies(Permission permission)
   {
      if (permission instanceof PortalPermission)
      {
         try
         {
            PortalPermission portalPermission = (PortalPermission)permission;
            Subject caller = getCheckedSubject();
            String roleName = getRoleName();
            PermissionRepository repository = domain.getPermissionRepository();
            boolean implied = owner.implies(repository, caller, roleName, portalPermission);
            return implied;
         }
         catch (Exception e)
         {
            log.error("Permission check against the repository failed", e);
         }
      }
      return false;
   }

   public abstract Enumeration elements();

   /**
    * Return the role name attached to the collection.
    *
    * @return the role name
    */
   public abstract String getRoleName();

   /**
    * Return the subject being checked or null if there is none.
    *
    * @return the current subject
    */
   public abstract Subject getCheckedSubject();
}
