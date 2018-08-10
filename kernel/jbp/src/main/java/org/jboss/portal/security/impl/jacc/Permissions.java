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
package org.jboss.portal.security.impl.jacc;



import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @author <a href="mailto:sshah@redhat.com">Sohil Shah</a> */
public final class Permissions
{
   /**
    * Key is permissions Class, value is PermissionCollection for that class. Not serialized; see serialization section
    * at end of class.
    */
   private transient Map permsMap = null;


   /** Creates a new Permissions object containing no PermissionCollections. */
   public Permissions()
   {
      this.permsMap = new ConcurrentHashMap<>();
   }

   /**
    * Adds a permission object to the PermissionCollection for the class the permission belongs to. For example, if
    * <i>permission</i> is a FilePermission, it is added to the FilePermissionCollection stored in this Permissions
    * object.
    * <p/>
    * This method creates a new PermissionCollection object (and adds the permission to it) if an appropriate collection
    * does not yet exist.
    * <p/>
    *
    * @param permission the Permission object to add.
    * @throws SecurityException if this Permissions object is marked as readonly.
    * @see PermissionCollection#isReadOnly()
    */
   public void add(Permission permission)
   {
      PermissionCollection pc;
      pc = getPermissionCollection(permission, true);
      pc.add(permission);
   }

   /**
    * Checks to see if this object's PermissionCollection for permissions of the specified permission's type implies the
    * permissions expressed in the <i>permission</i> object. Returns true if the combination of permissions in the
    * appropriate PermissionCollection (e.g., a FilePermissionCollection for a FilePermission) together imply the
    * specified permission.
    * <p/>
    * <p/>
    * For example, suppose there is a FilePermissionCollection in this Permissions object, and it contains one
    * FilePermission that specifies "read" access for all files in all subdirectories of the "/tmp" directory, and
    * another FilePermission that specifies "write" access for all files in the "/tmp/scratch/foo" directory. Then if
    * the <code>implies</code> method is called with a permission specifying both "read" and "write" access to files in
    * the "/tmp/scratch/foo" directory, <code>true</code> is returned.
    * <p/>
    * <p/>
    * Additionally, if this PermissionCollection contains the AllPermission, this method will always return true.
    * <p/>
    *
    * @param permission the Permission object to check.
    * @return true if "permission" is implied by the permissions in the PermissionCollection it belongs to, false if
    *         not.
    */
   public boolean implies(Permission permission)
   {
      PermissionCollection pc = getPermissionCollection(permission, false);
      if (pc != null)
      {
         return pc.implies(permission);
      }
      else
      {
         // none found
         return false;
      }
   }


   /**
    * Gets the PermissionCollection in this Permissions object for permissions whose type is the same as that of
    * <i>p</i>. For example, if <i>p</i> is a FilePermission, the FilePermissionCollection stored in this Permissions
    * object will be returned.
    * <p/>
    * If createEmpty is true, this method creates a new PermissionCollection object for the specified type of permission
    * objects if one does not yet exist. To do so, it first calls the <code>newPermissionCollection</code> method on
    * <i>p</i>. Subclasses of class Permission override that method if they need to store their permissions in a
    * particular PermissionCollection object in order to provide the correct semantics when the
    * <code>PermissionCollection.implies</code> method is called. If the call returns a PermissionCollection, that
    * collection is stored in this Permissions object. If the call returns null and createEmpty is true, then this
    * method instantiates and stores a default PermissionCollection that uses a hashtable to store its permission
    * objects.
    * <p/>
    * createEmpty is ignored when creating empty PermissionCollection for unresolved permissions because of the overhead
    * of determining the PermissionCollection to use.
    * <p/>
    * createEmpty should be set to false when this method is invoked from implies() because it incurs the additional
    * overhead of creating and adding an empty PermissionCollection that will just return false. It should be set to
    * true when invoked from add().
    */
   private PermissionCollection getPermissionCollection(Permission p,
                                                        boolean createEmpty)
   {
      Class c = p.getClass();

      PermissionCollection pc = (PermissionCollection)permsMap.get(c);
      if (!createEmpty)
      {
         return pc;
      }
      else if (pc == null)
      {
         pc = p.newPermissionCollection();
         if (pc != null)
         {
            permsMap.put(c, pc);
         }
      }
      return pc;
   }
}