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
package org.jboss.portal.security.metadata;

import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class SecurityConstraintsMetaData
{

   /** . */
   private Set children;

   public SecurityConstraintsMetaData()
   {
      children = new HashSet();
   }

   public Set getConstraints()
   {
      return children;
   }

   public static SecurityConstraintsMetaData buildSecurityConstraintMetaData(Element securityConstraintElt)
   {
      SecurityConstraintsMetaData securityConstraint = new SecurityConstraintsMetaData();
      for (Iterator i = XMLTools.getChildren(securityConstraintElt, "policy-permission").iterator(); i.hasNext();)
      {
         Element policyPermissionElt = (Element)i.next();
         Element uncheckedElt = XMLTools.getUniqueChild(policyPermissionElt, "unchecked", false);
         Set actionNames = new HashSet();
         for (Iterator j = XMLTools.getChildren(policyPermissionElt, "action-name").iterator(); j.hasNext();)
         {
            Element actionNameElt = (Element)j.next();
            String actionName = XMLTools.asString(actionNameElt);
            actionNames.add(actionName);
         }
         if (uncheckedElt != null)
         {
            RoleSecurityBinding binding = new RoleSecurityBinding(actionNames, SecurityConstants.UNCHECKED_ROLE_NAME);
            securityConstraint.children.add(binding);
         }
         else
         {
            for (Iterator j = XMLTools.getChildren(policyPermissionElt, "role-name").iterator(); j.hasNext();)
            {
               Element roleNameElt = (Element)j.next();
               String roleName = XMLTools.asString(roleNameElt);
               RoleSecurityBinding binding = new RoleSecurityBinding(actionNames, roleName);
               securityConstraint.children.add(binding);
            }
         }
      }
      return securityConstraint;
   }
}
