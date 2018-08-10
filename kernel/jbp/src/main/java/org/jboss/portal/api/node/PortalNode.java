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
package org.jboss.portal.api.node;

import org.jboss.portal.api.PortalRuntimeContext;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a portal node, a first class entity for the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8785 $
 */
public interface PortalNode
{

   /** A node of type context. */
   int TYPE_CONTEXT = 0;

   /** A node of type portal. */
   int TYPE_PORTAL = 1;

   /** A node of type page. */
   int TYPE_PAGE = 2;

   /** A node of type window. */
   int TYPE_WINDOW = 3;

   /**
    * Return the node type.
    *
    * @return the node type
    */
   int getType();

   /**
    * Return the root node of this node.
    *
    * @return the root node
    */
   PortalNode getRoot();

   /**
    * Return the parent node of this node.
    *
    * @return the parent node
    */
   PortalNode getParent();

   /**
    * Return the node name which identifies the node in the context to its parent.
    *
    * @return the node name
    */
   String getName();

   /**
    * Returns the best display name for specified locale.
    *
    * @return the display name
    */
   String getDisplayName(Locale locale);

   /**
    * Return a child of this object.
    *
    * @param name the child name
    * @return a named child
    */
   PortalNode getChild(String name);

   /**
    * Return the children of this object.
    *
    * @return the children
    */
   Collection getChildren();

   /**
    * Get a portal node relative to this object.
    *
    * @param relativePath the relative path
    * @return the relative object
    */
   PortalNode resolve(String relativePath);

   /**
    * Returns the node properties.
    *
    * @return the node properties
    */
   Map getProperties();

   /**
    * Create a portal node url for this object.
    *
    * @param portalRuntimeContext the portal runtime context
    * @return the portal node url
    */
   PortalNodeURL createURL(PortalRuntimeContext portalRuntimeContext);
}
