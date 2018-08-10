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
package org.jboss.portal.identity;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.info.ProfileInfo;
import org.jboss.portal.identity.User;

import java.util.Map;

/**
 * Manages user properties
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public interface UserProfileModule
{

   /**
    * Returns user property
    * @param user
    * @param propertyName
    * @return
    * @throws IdentityException
    * @throws IllegalArgumentException
    */
   public Object getProperty(User user, String propertyName) throws IdentityException, IllegalArgumentException;

   /**
    * Sets user property. If the property value is null the property will be removed.
    * @param user
    * @param name
    * @param property value
    * @throws IdentityException
    * @throws IllegalArgumentException
    */
   public void setProperty(User user, String name, Object property) throws IdentityException, IllegalArgumentException;

   /**
    * Returns all properties related to user
    * @param user
    * @return
    * @throws IdentityException
    * @throws IllegalArgumentException
    */
   public Map getProperties(User user) throws IdentityException, IllegalArgumentException;

   /**
    * Return ProfileInfo object that can be used to obtain PropertyInfo on specific property name.
    * @return
    * @throws IdentityException
    */
   public ProfileInfo getProfileInfo() throws IdentityException;

}
