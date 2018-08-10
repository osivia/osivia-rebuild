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

import javax.security.auth.Subject;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This principal purpose is to cache some computation necessary to make the portal JACC integration work.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JACCPortalPrincipal implements Principal
{

   /** The set of principals that we use to give to jacc. */
   private Principal[] principals;

   /** The set of roles that the subject owns. */
   private Set roles;

   public JACCPortalPrincipal(Subject subject)
   {
      this.roles = Collections.EMPTY_SET;

      // Get the principals and roles from the subject
      ArrayList principals = new ArrayList();
      for (Iterator i = subject.getPrincipals().iterator(); i.hasNext();)
      {
         Principal principal = (Principal)i.next();
         if (principal instanceof Group)
         {
            Group group = (Group)principal;
            for (Enumeration e = group.members(); e.hasMoreElements();)
            {
               Principal nestedPrincipal = (Principal)e.nextElement();
               principals.add(nestedPrincipal);
            }
            if ("Roles".equals(group.getName()))
            {
               roles = new HashSet();
               for (Enumeration e = group.members(); e.hasMoreElements();)
               {
                  Principal role = (Principal)e.nextElement();
                  roles.add(role);
               }
            }
         }
         else
         {
            principals.add(principal);
         }
      }
      this.principals = (Principal[])principals.toArray(new Principal[principals.size()]);
   }

   /**
    * Return the set of roles.
    *
    * @return the roles
    */
   public Set getRoles()
   {
      return roles;
   }

   /**
    * The principals owned.
    *
    * @return the principals
    */
   public Principal[] getPrincipals()
   {
      return principals;
   }

   public String getName()
   {
      return "PortalPrincipal";
   }
}
