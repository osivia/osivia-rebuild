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
package org.jboss.portal.api.session;

/**
 * The portal session, the portal session attributes are accessible however it is not possible to influence the
 * lifecycle of the session as it is managed by the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8785 $
 */
public interface PortalSession
{
   /**
    * Return the session id.
    *
    * @return the session id
    */
   String getId();

   /**
    * Returns a session attribute.
    *
    * @param name the attribute name
    * @return the attribute value or null if it is not found
    * @throws IllegalArgumentException if the attribute name is null
    */
   Object getAttribute(String name) throws IllegalArgumentException;

   /**
    * Update an attribute value. If the attribute value is null, then it is considered as a removal.
    *
    * @param name      the attribute name
    * @param attribute the attribute value
    * @throws IllegalArgumentException if the attribute name is null
    */
   void setAttribute(String name, Object attribute) throws IllegalArgumentException;

   /**
    * Removes an attribute value.
    *
    * @param name the attribute name
    * @throws IllegalArgumentException if the attribute name is null
    */
   void removeAttribute(String name) throws IllegalArgumentException;
}
