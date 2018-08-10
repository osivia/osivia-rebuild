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
package org.jboss.portal.api.navstate;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.node.PortalNode;

/**
 * Provide access to a portion of the navigational state managed by the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8785 $
 */
public interface NavigationalStateContext
{
   /**
    * Returns the window state of a window or null if it is not found.
    *
    * @param window the window from which the window state is returned
    * @return the window state
    * @throws IllegalArgumentException
    */
   WindowState getWindowState(PortalNode window) throws IllegalArgumentException;

   /**
    * Updates the window state of a window.
    *
    * @param window      the window to update
    * @param windowState the new window state value
    * @throws IllegalArgumentException
    */
   void setWindowState(PortalNode window, WindowState windowState) throws IllegalArgumentException;

   /**
    * Returns the mode of a window or null if it is not found.
    *
    * @param window the window from which the mode is returned
    * @return the mode
    * @throws IllegalArgumentException
    */
   Mode getMode(PortalNode window) throws IllegalArgumentException;

   /**
    * Updates the mode of a window.
    *
    * @param window the window to update
    * @param mode   the new mode value
    * @throws IllegalArgumentException
    */
   void setMode(PortalNode window, Mode mode) throws IllegalArgumentException;
}
