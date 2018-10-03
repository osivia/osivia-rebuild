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
package org.osivia.portal.jpb.services;

import org.jboss.logging.Logger;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNodeSecurityConstraint;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.NoSuchPortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.jems.hibernate.ContextObject;
import org.jboss.portal.security.RoleSecurityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12376 $
 */
public class ObjectNodeMock implements ContextObject
{

   /** . */
   protected static final Logger log = Logger.getLogger(ObjectNodeMock.class);

   /** . */
   protected static final boolean trace = log.isTraceEnabled();

   // Persistent fields
   private Long key;
   private PortalObjectId path;
   private String name;
   private ObjectNodeMock parent;
   private Map children;
   private PortalObjectImplMock object;
   private Map<String, ObjectNodeSecurityConstraint> securityConstraints;

   // Runtime fields
   private AbstractPortalObjectContainer.ContainerContext containerContext;
   private static final String DASHBOARD = "dashboard";

   public ObjectNodeMock()
   {
      this.containerContext = null;
      this.path = null;
      this.name = null;
      this.children = null;
      this.securityConstraints = null;
   }

   public ObjectNodeMock(PortalObjectId path, String name)
   {
      this.path = path;
      this.name = name;
      this.children = new HashMap();
      this.securityConstraints = new HashMap<String, ObjectNodeSecurityConstraint>();
   }

   // ContextObject implementation *************************************************************************************

   public void setContext(Object context)
   {
      this.containerContext = (AbstractPortalObjectContainer.ContainerContext)context;
   }

   public Long getKey()
   {
      return key;
   }

   public void setKey(Long key)
   {
      this.key = key;
   }

   public PortalObjectImplMock getObject()
   {
      return object;
   }

   public void setObject(PortalObjectImplMock object)
   {
      this.object = object;
   }

  
  

   public PortalObjectId getPath()
   {
      return path;
   }

   public void setPath(PortalObjectId path)
   {
      this.path = path;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public ObjectNodeMock getParent()
   {
      return parent;
   }

   public void setParent(ObjectNodeMock parent)
   {
      this.parent = parent;
   }

   public Map getChildren()
   {
      return children;
   }

   public void setChildren(Map children)
   {
      this.children = children;
   }

   public AbstractPortalObjectContainer.ContainerContext getContext()
   {
      return containerContext;
   }

   public String toString()
   {
      return "PortalObject[id=" + path + "]";
   }

   protected PortalObjectId toChildPath(String name)
   {
      return new PortalObjectId(path.getNamespace(), path.getPath().getChild(name));
   }

   public Map getSecurityConstraints()
   {
      return securityConstraints;
   }

   public void setSecurityConstraints(Map securityConstraints)
   {
      this.securityConstraints = securityConstraints;
   }

   public void setBindings(Set bindings)
   {
	   /*
      // Clear existing constraints
      for (ObjectNodeSecurityConstraint onsc : securityConstraints.values())
      {
         onsc.setObjectNode(null);
      }
      securityConstraints.clear();

      // Replace with new ones
      for (Object binding : bindings)
      {
         RoleSecurityBinding sc = (RoleSecurityBinding)binding;

         // Optmize a bit
         if (sc.getActions().size() > 0)
         {
            ObjectNodeSecurityConstraint onsc = new ObjectNodeSecurityConstraint(sc.getActions(), sc.getRoleName());

            //
            onsc.setObjectNode(this);
            securityConstraints.put(onsc.getRole(), onsc);
         }
      }

      //
      containerContext.updated(this);
      */
   }

   public Set getBindings()
   {
      Set<RoleSecurityBinding> bindings = new HashSet<RoleSecurityBinding>();
      for (ObjectNodeSecurityConstraint onsc : securityConstraints.values())
      {
         Set actions = onsc.getActions();
         RoleSecurityBinding sc = new RoleSecurityBinding(actions, onsc.getRole());
         bindings.add(sc);
      }
      return bindings;
   }

   public RoleSecurityBinding getBinding(String roleName)
   {
      Set<String> actions = null;

      //
      ObjectNodeSecurityConstraint onsc = securityConstraints.get(roleName);
      if (onsc != null)
      {
         actions = onsc.getActions();
      }

      //
      if (DASHBOARD.equals(path.getNamespace()))
      {
         if (actions == null)
         {
            actions = Collections.singleton(DASHBOARD);
         }
         else
         {
            actions = new HashSet<String>(actions);
            actions.add(DASHBOARD);
         }
      }

      // Add the dashboard permission
      if (actions != null)
      {
         return new RoleSecurityBinding(actions, roleName);
      }
      else
      {
         return null;
      }
   }
}