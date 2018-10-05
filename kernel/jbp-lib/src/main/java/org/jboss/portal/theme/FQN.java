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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 * @deprecated Defines a reusable and generic full qualified name.
 */
public class FQN implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -4843390736196899215L;

   private static final String[] EMPTY_ARRAY = new String[0];

   /** The list of objects. */
   protected String[] names; // julien : perhaps Serializable[] ?

   /** The cached hashcode. */
   private int hashCode;

   public FQN()
   {
      names = EMPTY_ARRAY;
      hashCode = 0;
   }

   public FQN(String name) throws IllegalArgumentException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Name cannot be null");
      }
      names = new String[]{name};
      hashCode = 0;
   }

   public FQN(String[] names) throws IllegalArgumentException
   {
      if (names == null)
      {
         throw new IllegalArgumentException("Names array must not be null");
      }
      this.names = new String[names.length];
      this.hashCode = 0;
      for (int i = 0; i < names.length; i++)
      {
         String name = names[i];
         if (name == null)
         {
            throw new IllegalArgumentException("One of the names is null");
         }
         this.names[i] = name;
      }
   }

   public FQN(FQN base, String name) throws IllegalArgumentException
   {
      if (base == null)
      {
         throw new IllegalArgumentException("Base cannot be null");
      }
      if (name == null)
      {
         throw new IllegalArgumentException("Name cannot be null");
      }
      names = new String[base.names.length + 1];
      hashCode = 0;
      System.arraycopy(base.names, 0, names, 0, base.names.length);
      names[base.names.length] = name;
   }

   public FQN(FQN base, String[] relative) throws IllegalArgumentException
   {
      if (base == null)
      {
         throw new IllegalArgumentException("Base cannot be null");
      }
      if (relative == null)
      {
         throw new IllegalArgumentException("Relative cannot be null");
      }
      names = new String[base.names.length + relative.length];
      hashCode = 0;
      System.arraycopy(base.names, 0, names, 0, base.names.length);
      System.arraycopy(relative, 0, names, base.names.length, relative.length);
   }

   public FQN(FQN base, FQN relative) throws IllegalArgumentException
   {
      if (base == null)
      {
         throw new IllegalArgumentException("Base cannot be null");
      }
      if (relative == null)
      {
         throw new IllegalArgumentException("Relative cannot be null");
      }
      names = new String[base.names.length + relative.names.length];
      hashCode = 0;
      System.arraycopy(base.names, 0, names, 0, base.names.length);
      System.arraycopy(relative.names, 0, names, base.names.length, relative.names.length);
   }

   public int size()
   {
      return names.length;
   }

   public String getName(int index) throws ArrayIndexOutOfBoundsException
   {
      return names[index];
   }

   public String[] toArray()
   {
      String[] copy = new String[names.length];
      System.arraycopy(names, 0, copy, 0, copy.length);
      return copy;
   }

   public boolean isChildOf(FQN parent) throws IllegalArgumentException
   {
      if (parent == null)
      {
         throw new IllegalArgumentException("Cannot compare to null");
      }
      if (names.length != parent.names.length + 1)
      {
         return false;
      }
      for (int i = 0; i < parent.names.length; i++)
      {
         Object name = parent.names[i];
         if (!name.equals(names[i]))
         {
            return false;
         }
      }
      return true;
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (!(obj instanceof FQN))
      {
         return false;
      }
      FQN other = (FQN)obj;
      if (other.names.length != names.length)
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
      if (names.length == 0)
      {
         return 0;
      }
      if (hashCode == 0)
      {
         int tmp = 0;
         for (int i = 0; i < names.length; i++)
         {
            tmp = tmp * 29 + names[i].hashCode();
         }
         hashCode = tmp;
      }
      return hashCode;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < names.length; i++)
      {
         buffer.append('/').append(names[i]);
      }
      return buffer.toString();
   }

   private void writeObject(ObjectOutputStream out) throws IOException
   {
      out.writeInt(names.length);
      for (int i = 0; i < names.length; i++)
      {
         out.writeObject(names[i]);
      }
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      names = new String[in.readInt()];
      for (int i = 0; i < names.length; i++)
      {
         names[i] = (String)in.readObject();
      }
   }
}
