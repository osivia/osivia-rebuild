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
package org.jboss.portal.theme;

/**
 * Abstraction of a unique identifier for registered meta data.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 8784 $</tt>
 */
public class ServerRegistrationID extends FQN
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 8042337046523234207L;

   private String desc = null;

   public static class Type
   {
      private final String type;

      private Type(String type)
      {
         this.type = type;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (o instanceof Type)
         {
            Type that = (Type)o;
            return this.type.equals(that.type);
         }
         return false;
      }

      public int hashCode()
      {
         return type.hashCode();
      }

      public String toString()
      {
         return type;
      }
   }

   private Type type;

   /** type for a registered Portal Theme. */
   public static final Type TYPE_THEME = new Type("theme");

   /** Type for a registered Portal Layout. */
   public static final Type TYPE_LAYOUT = new Type("layout");

   /** Type for a registered Portal RenderSet. */
   public static final Type TYPE_RENDERSET = new Type("renderSet");

   /**
    * @param type
    * @param names
    * @throws IllegalArgumentException
    */
   private ServerRegistrationID(Type type, String[] names)
   {
      super(names);
      this.type = type;
   }

   /**
    * Create a new registration id based on the provided type and names.
    *
    * @param type  the type of registration to create (TYPE_THEME or TYPE_LAYOUT)
    * @param names an array of names that together build a unique id
    * @return a new ServerRegistrationID
    */
   public static ServerRegistrationID createID(Type type, String[] names)
   {
      return new ServerRegistrationID(type, names);
   }

   public String toString()
   {
      if (desc == null)
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append(names[0]);
         for (int i = 1; i < names.length; i++)
         {
            buffer.append('.').append(names[i]);
         }
         desc = buffer.toString();
      }
      return desc;
   }

   /**
    * convenience method to create a new registration id for a theme.
    *
    * @param appID the name of the portal web application that contains the theme
    * @param name  the name of the theme
    * @return a registration id for the meta data of a portal theme with the unique id created from <code>appID</code>
    *         and <code>name</code>
    * @throws IllegalArgumentException if any of the provided paramter values are null
    * @see #createID
    * @see #TYPE_THEME
    */
   public static ServerRegistrationID createPortalThemeID(String appID, String name) throws IllegalArgumentException
   {

      return new ServerRegistrationID(TYPE_THEME, new String[]{appID, name});
   }

   /**
    * convenience method to create a new registration id for a layout.
    *
    * @param appID the name of the portal web application that contains the layout
    * @param name  the name of the layout
    * @return a registration id for the meta data of a portal layout with the unique id created from <code>appID</code>
    *         and <code>name</code>
    * @throws IllegalArgumentException if any of the provided parameter values are null
    * @see #createID
    * @see #TYPE_LAYOUT
    */
   public static ServerRegistrationID createPortalLayoutID(String appID, String name) throws IllegalArgumentException
   {
      return new ServerRegistrationID(TYPE_LAYOUT, new String[]{appID, name});
   }

   /** @return the type of this registration data */
   public Type getType()
   {
      return type;
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (!(obj instanceof ServerRegistrationID))
      {
         return false;
      }
      ServerRegistrationID other = (ServerRegistrationID)obj;
      if (other.names.length != names.length)
      {
         return false;
      }

      if (other.type != type)
      {
         return false;
      }

      for (int i = 0; i < names.length; i++)
      {
         if (!names[i].equals(other.names[i]))
         {
            return false;
         }
      }
      return true;
   }

   public int hashCode()
   {
      int tmp = type.hashCode();

      for (int i = 0; i < names.length; i++)
      {
         tmp = tmp * 29 + names[i].hashCode();
      }
      return tmp;
   }
}
