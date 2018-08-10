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
package org.jboss.portal.identity.event;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class UserProfileChangedEvent extends IdentityEvent
{

   /** . */
   private final Object userId;

   /** . */
   private final String userName;

   /** . */
   private final String propertyName;

   /** .*/
   private Object newValue;

   public UserProfileChangedEvent(Object userId, String userName, String propertyName, Object newValue)
   {
      this.newValue = newValue;
      if (userId == null)
      {
         throw new IllegalArgumentException();
      }
      if (userName == null)
      {
         throw new IllegalArgumentException();
      }
      if (propertyName == null)
      {
         throw new IllegalArgumentException();
      }

      if (propertyName == null)
      {
         throw new IllegalArgumentException();
      }

      this.userId = userId;
      this.userName = userName;
      this.propertyName = propertyName;
      this.newValue = newValue;
   }

   public Object getUserId()
   {
      return userId;
   }

   public String getUserName()
   {
      return userName;
   }

   public String getPropertyName()
   {
      return propertyName;
   }

   public Object getNewValue()
   {
      return newValue;
   }

   public String toString()
   {
      return "UserProfileChangedEvent[userId=" + userId + ",userName=" + userName + ",propertyName=" + propertyName + ",propertyValue" + newValue + "]";
   }
}