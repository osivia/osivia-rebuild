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

import org.jboss.portal.security.spi.provider.PermissionRepository;

import javax.security.auth.Subject;
import java.security.Permission;
import java.security.PermissionCollection;

/**
 * Base permission that can act either as a container or as a permission that has an URI.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class PortalPermission extends Permission
{

   /** . */
   protected final String uri;

   /** . */
   protected final PortalPermissionCollection collection;

   public PortalPermission(String name, PortalPermissionCollection collection)
   {
      super(name);
      if (collection == null)
      {
         throw new IllegalArgumentException("Need a repositoty");
      }
      collection.owner = this;

      //
      this.uri = null;
      this.collection = collection;
   }

   public PortalPermission(String name, String uri)
   {
      super(name);
      if (uri == null)
      {
         throw new IllegalArgumentException("Need an uri");
      }
      this.uri = uri;
      this.collection = null;
   }

   /** Return the uri for the permission or null if the permission acts as a container. */
   public String getURI()
   {
      return uri;
   }

   /** Return an instance of PortalPermissionCollection or null if the permission does not act as a container. */
   public PermissionCollection newPermissionCollection()
   {
      return collection;
   }

   /** Return true if the permission is a container. */
   public boolean isContainer()
   {
      return collection != null;
   }

   /**
    * Return the portal permission type.
    *
    * @return the portal permission type
    */
   public abstract String getType();

   /**
    * Implement the imply logic when we check the permission against a domain.
    *
    * @param repository
    * @param caller
    * @param permission
    * @return true if the permission is implied
    */
   public abstract boolean implies(PermissionRepository repository, Subject caller, String roleName, PortalPermission permission) throws PortalSecurityException;
}
