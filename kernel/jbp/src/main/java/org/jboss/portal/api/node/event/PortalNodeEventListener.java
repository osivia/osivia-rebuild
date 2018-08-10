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

/**
 * A listener for portal events
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8785 $
 */
public interface PortalNodeEventListener
{
   /**
    * <p>Dispatch an event to the listener. If the listener does not care about replacing the current event with a new
    * one then it should return the event returned by the <code>PortalNodeEventContext</code> provided such as:</p> <p/>
    * <p><code>return context.dispatch()</code></p> <p/> <p>Otherwise it can return a new event to replace the current
    * one. Only events of type <code>WindowNavigationEvent</code> or <code>WindowActionEvent;</code> will have an
    * effect.</p>
    *
    * @param context the context in which the event is triggered
    * @param event   the fired event
    * @return an event
    */
   PortalNodeEvent onEvent(PortalNodeEventContext context, PortalNodeEvent event);
}
