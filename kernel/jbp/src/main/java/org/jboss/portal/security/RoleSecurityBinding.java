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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Binds a role and a set of actions together. This object is immutable. <p>A portal resource (portal, page, window,
 * instance, portlet...) is secured via a set of security constraints. each security constraint holds the information
 * about what roles are allowed what actions.</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class RoleSecurityBinding implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 2723720035191741138L;

   /** The role name of this contraint. */
   private final String roleName;

   /** The set of actions of this constraint. */
   private final Set actions;

   /** The cached toString value. */
   private transient String toString;

   /** The cached hash code. */
   private transient int hashCode;

   /** The cached actions as a string. */
   private transient String actionsAsString;

   /**
    * Create a new constraint with the provided actions for the specified role.
    *
    * @param actions a comma separated list of allowed actions
    * @param role    the role name
    */
   public RoleSecurityBinding(String actions, String role)
   {
      if (role == null)
      {
         throw new IllegalArgumentException("Role cannot be null");
      }
      if (actions == null)
      {
         throw new IllegalArgumentException("Actions cannot be null");
      }

      //
      StringTokenizer tokens = new StringTokenizer(actions, ",");
      Set set = new HashSet();
      while (tokens.hasMoreTokens())
      {
         set.add(tokens.nextToken().trim());
      }

      //
      this.roleName = role;
      this.actions = Collections.unmodifiableSet(set);
   }

   /**
    * Create a new constraint with the provided actions and the specified role.
    *
    * @param actions the set of actions
    * @param role    the role name
    */
   public RoleSecurityBinding(Set actions, String role)
   {
      if (role == null)
      {
         throw new IllegalArgumentException("Role cannot be null");
      }
      if (actions == null)
      {
         throw new IllegalArgumentException("Actions cannot be null");
      }

      //
      this.roleName = role;
      this.actions = Collections.unmodifiableSet(new HashSet(actions));
   }

   /** Copy constructor. */
   public RoleSecurityBinding(RoleSecurityBinding other)
   {
      if (other == null)
      {
         throw new IllegalArgumentException("The constraint to clone cannot be null");
      }

      //
      this.roleName = other.roleName;
      this.actions = other.actions;
   }

   /**
    * Return a <code>java.util.Set<String></code> of allowed actions.
    *
    * @return the action set
    */
   public Set getActions()
   {
      return actions;
   }

   /**
    * Return the role of this constraint
    *
    * @return the role
    */
   public String getRoleName()
   {
      return roleName;
   }

   /**
    * Return a comma separated list of actions.
    *
    * @return the action string representation
    */
   public String getActionsAsString()
   {
      if (actionsAsString == null)
      {
         StringBuffer tmp = new StringBuffer();
         for (Iterator i = actions.iterator(); i.hasNext();)
         {
            String action = (String)i.next();
            if (i.hasNext())
            {
               tmp.append(", ");
            }
            tmp.append(action);
         }
         actionsAsString = tmp.toString();
      }
      return actionsAsString;
   }

   /** @see Object#toString */
   public String toString()
   {
      if (toString == null)
      {
         StringBuffer tmp = new StringBuffer("SecurityConstraint[actions=(");
         for (Iterator i = actions.iterator(); ;)
         {
            String action = (String)i.next();
            tmp.append(action);
            if (i.hasNext())
            {
               tmp.append(", ");
            }
            else
            {
               break;
            }
         }
         tmp.append("),role=").append(roleName).append("]");
         toString = tmp.toString();
      }
      return toString;
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o instanceof RoleSecurityBinding)
      {
         RoleSecurityBinding that = (RoleSecurityBinding)o;
         return actions.equals(that.actions) && roleName.equals(that.roleName);
      }
      return false;
   }

   public int hashCode()
   {
      if (hashCode == 0)
      {
         int hashCode;
         hashCode = actions.hashCode();
         hashCode = 29 * hashCode + roleName.hashCode();
         this.hashCode = hashCode;
      }
      return hashCode;
   }
}
