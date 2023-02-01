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
package org.osivia.portal.core.container.persistent;

import org.apache.commons.lang3.StringUtils;

import org.jboss.portal.core.impl.model.portal.ObjectNodeSecurityConstraint;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.NoSuchPortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.jems.hibernate.ContextObject;
import org.jboss.portal.security.RoleSecurityBinding;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * cf. PersistentPortalObjectContainer ...
 */
public class ObjectNodeImplBase implements ContextObject, RepositoryListener
{



   // Persistent fields
   private Long key;
   private PortalObjectId path;
   private String name;
   private ObjectNodeImplBase parent;
   private Map children;
   private PortalObjectImplBase object;
   private Map<String, ObjectNodeSecurityConstraint> securityConstraints;
   
   private boolean dirty =false;

   
    public boolean isDirty() {
        return dirty;
    }

// Runtime fields
   private StaticPortalObjectContainer.ContainerContext containerContext;
   private static final String DASHBOARD = "dashboard";

   public ObjectNodeImplBase()
   {
      this.containerContext = null;
      this.path = null;
      this.name = null;
      this.children = null;
      this.securityConstraints = null;
   }

   public ObjectNodeImplBase(PortalObjectId path, String name,Object containerContext)
   {
      this.path = path;
      this.name = name;
      this.containerContext = (StaticPortalObjectContainer.ContainerContext)containerContext;
      this.children = new ConcurrentHashMap();
      this.securityConstraints = new HashMap<String, ObjectNodeSecurityConstraint>();
   }

   // ContextObject implementation *************************************************************************************

   public void setContext(Object context)
   {
      this.containerContext = (StaticPortalObjectContainer.ContainerContext)context;
   }

   public Long getKey()
   {
      return key;
   }

   public void setKey(Long key)
   {
      this.key = key;
   }

   public PortalObjectImplBase getObject()
   {
      return object;
   }

   public void setObject(PortalObjectImplBase object)
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

   public ObjectNodeImplBase getParent()
   {
      return parent;
   }

   public void setParent(ObjectNodeImplBase parent)
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

   public StaticPortalObjectContainer.ContainerContext getContext()
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

    @Override
    public void contentModified(CMSEvent e) {
        Document sourceDocument =  e.getSourceDocument();
        if( sourceDocument instanceof Space) {
            String templated =  (String) sourceDocument.getProperties().get("osivia.connect.templated");
            if( !"false".equals(templated)) {
                dirty = true;
                DynamicPortalObjectContainer.clearCache();
            }
        }
        
    }
}