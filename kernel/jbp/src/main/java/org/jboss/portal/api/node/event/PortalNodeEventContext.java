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

import org.jboss.portal.api.event.PortalEventContext;
import org.jboss.portal.api.node.PortalNode;

/**
 * The context in which a <code>PortalNodeEvent</code> is dispatched.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8785 $
 */
public interface PortalNodeEventContext extends PortalEventContext
{
   /**
    * Dispatch the event to the next listeners.
    *
    * @return the event response or null
    */
   PortalNodeEvent dispatch();

   /**
    * The current node being browsed by the bubbling effect.
    *
    * @return the current node.
    */
   PortalNode getNode();
}
