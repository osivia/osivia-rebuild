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
package org.jboss.portal.security.spi.auth;

import org.jboss.portal.security.PortalPermission;
import org.jboss.portal.security.PortalSecurityException;

import javax.security.auth.Subject;

/**
 * Portal Authorization Management Interface
 *
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @since Jan 30, 2006
 */
public interface PortalAuthorizationManager
{
   /**
    * @param permission
    * @return
    * @throws PortalSecurityException
    * @throws IllegalArgumentException if the permission is null
    */
   public boolean checkPermission(PortalPermission permission) throws IllegalArgumentException, PortalSecurityException;

   /**
    * @param checkedSubject
    * @param permission
    * @return
    * @throws PortalSecurityException
    * @throws IllegalArgumentException if the permission is null
    */
   public boolean checkPermission(Subject checkedSubject, PortalPermission permission) throws IllegalArgumentException, PortalSecurityException;
}
