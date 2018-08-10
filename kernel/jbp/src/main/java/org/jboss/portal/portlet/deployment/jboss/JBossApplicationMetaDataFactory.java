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
package org.jboss.portal.portlet.deployment.jboss;

import org.jboss.portal.common.transaction.Transactions;
import org.jboss.portal.portlet.deployment.jboss.info.impl.CacheInfoImpl;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossApplicationMetaData;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossPortletMetaData;
import org.jboss.portal.portlet.deployment.jboss.metadata.PolicyPermissionMetaData;
import org.jboss.xb.binding.GenericObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10557 $
 */
public class JBossApplicationMetaDataFactory implements GenericObjectModelFactory
{

   public Object newRoot(Object root,
                         UnmarshallingContext nav,
                         String nsURI,
                         String localName,
                         Attributes attrs)
   {
      if (root == null)
      {
         root = new JBossApplicationMetaData();
      }
      return root;
   }

   public Object completeRoot(Object root, UnmarshallingContext nav, String uri, String name)
   {
      return root;
   }

   public Object newChild(Object object, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if (object instanceof JBossApplicationMetaData)
      {
         if ("portlet".equals(localName))
         {
            return createJBossPortlet();
         }
      }
//      else if (object instanceof JBossPortletMetaData)
//      {
//         if ("security-constraint".equals(localName))
//         {
//            return new SecurityConstraintMetaData();
//         }
//      }
//      else if (object instanceof SecurityConstraintMetaData)
//      {
//         if ("policy-permission".equals(localName))
//         {
//            return new PolicyPermissionMetaData();
//         }
//      }
      else if (object instanceof PolicyPermissionMetaData)
      {
         if ("unchecked".equals(localName))
         {
            return "unchecked";
         }
      }
      return null;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext nav, String nsURI, String localName)
   {
      if (parent instanceof JBossApplicationMetaData)
      {
         JBossApplicationMetaData app = (JBossApplicationMetaData)parent;
         if (child instanceof JBossPortletMetaData)
         {
            JBossPortletMetaData portlet = (JBossPortletMetaData)child;
            app.getPortlets().put(portlet.getName(), portlet);
            portlet.merge(app);
         }
      }
//      else if (parent instanceof JBossPortletMetaData)
//      {
//         JBossPortletMetaData portlet = (JBossPortletMetaData)parent;
//         if (child instanceof SecurityConstraintMetaData)
//         {
//            portlet.setSecurityConstraint((SecurityConstraintMetaData)child);
//         }
//      }
//      else if (parent instanceof SecurityConstraintMetaData)
//      {
//         SecurityConstraintMetaData securityConstraint = (SecurityConstraintMetaData)parent;
//         if (child instanceof PolicyPermissionMetaData)
//         {
//            PolicyPermissionMetaData policyPermission = (PolicyPermissionMetaData)child;
//            PolicyPermissionMetaData other = (PolicyPermissionMetaData)securityConstraint.getPolicyPermissions().get(policyPermission.getRoleName());
//            if (other != null)
//            {
//               other.getActions().addAll(policyPermission.getActions());
//            }
//            else
//            {
//               securityConstraint.getPolicyPermissions().put(policyPermission.getRoleName(), policyPermission);
//            }
//         }
//      }
//      else if (parent instanceof PolicyPermissionMetaData)
//      {
//         PolicyPermissionMetaData policyPermission = (PolicyPermissionMetaData)parent;
//         if ("unchecked".equals(child))
//         {
//            policyPermission.setRoleName(SecurityConstants.UNCHECKED_ROLE_NAME);
//         }
//      }
   }

   public void setValue(Object object, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if (object instanceof JBossApplicationMetaData)
      {
         JBossApplicationMetaData app = (JBossApplicationMetaData)object;
         if ("app-id".equals(localName))
         {
            app.setId(value);
         }
         else if ("remotable".equals(localName))
         {
            if ("true".equalsIgnoreCase(value))
            {
               app.setRemotable(Boolean.TRUE);
            }
            else if ("false".equalsIgnoreCase(value))
            {
               app.setRemotable(Boolean.FALSE);
            }
            else
            {
               throw new RuntimeException();
            }
         }
      }
      else if (object instanceof JBossPortletMetaData)
      {
         JBossPortletMetaData portlet = (JBossPortletMetaData)object;
         if ("portlet-name".equals(localName))
         {
            portlet.setName(value);
         }
         else if ("remotable".equals(localName))
         {
            if ("true".equalsIgnoreCase(value))
            {
               portlet.setRemotable(Boolean.TRUE);
            }
            else if ("false".equalsIgnoreCase(value))
            {
               portlet.setRemotable(Boolean.FALSE);
            }
            else
            {
               throw new RuntimeException();
            }
         }
         else if ("distributed".equals(localName))
         {
            if ("true".equalsIgnoreCase(value))
            {
               portlet.setDistributed(Boolean.TRUE);
            }
            else if ("false".equalsIgnoreCase(value))
            {
               portlet.setDistributed(Boolean.FALSE);
            }
            else
            {
               throw new RuntimeException();
            }
         }
         else if ("ref-type".equals(localName))
         {
            if ("strong".equals(localName))
            {
               portlet.setCacheRefType(new Integer(CacheInfoImpl.REF_STRONG));
            }
            else if ("soft".equals(localName))
            {
               portlet.setCacheRefType(new Integer(CacheInfoImpl.REF_SOFT));
            }
            else
            {
               // log.warn("Unrecognized reference type " + refType);
            }
         }
         else if ("trans-attribute".equals(localName))
         {
            if ("Required".equalsIgnoreCase(value))
            {
               portlet.setTxType(Transactions.TYPE_REQUIRED);
            }
            else if ("Mandatory".equals(value))
            {
               portlet.setTxType(Transactions.TYPE_MANDATORY);
            }
            else if ("Never".equals(value))
            {
               portlet.setTxType(Transactions.TYPE_NEVER);
            }
            else if ("Supports".equals(value))
            {
               portlet.setTxType(Transactions.TYPE_SUPPORTS);
            }
            else if ("NotSupported".equals(value))
            {
               portlet.setTxType(Transactions.TYPE_NOT_SUPPORTED);
            }
            else if ("RequiresNew".equals(value))
            {
               portlet.setTxType(Transactions.TYPE_REQUIRES_NEW);
            }
            else
            {
               throw new RuntimeException();
            }
         }
      }
      else if (object instanceof PolicyPermissionMetaData)
      {
         PolicyPermissionMetaData policyPermission = (PolicyPermissionMetaData)object;
         if ("role-name".equals(localName))
         {
            policyPermission.setRoleName(value);
         }
         else if ("action-name".equals(localName))
         {
            policyPermission.getActions().add(value);
         }
      }
   }

   /** Can be subclasses to allow sub class. */
   protected JBossPortletMetaData createJBossPortlet()
   {
      return new JBossPortletMetaData();
   }
}
