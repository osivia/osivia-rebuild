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
package org.jboss.portal.api.node.event;

import org.jboss.portal.api.event.PortalEvent;
import org.jboss.portal.api.node.PortalNode;

/**
 * Base class for all portal node events.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 */
public abstract class PortalNodeEvent extends PortalEvent
{

   /** The portal node targetted by this event. */
   protected final PortalNode node;

   /**
    * @param node the node the event targets
    * @throws IllegalArgumentException if the node argument is null
    */
   public PortalNodeEvent(PortalNode node) throws IllegalArgumentException
   {
      if (node == null)
      {
         throw new IllegalArgumentException("No null node accepted");
      }
      this.node = node;
   }

   /**
    * Returns the portal node targetted by the event.
    *
    * @return the portal node
    */
   public PortalNode getNode()
   {
      return node;
   }
}