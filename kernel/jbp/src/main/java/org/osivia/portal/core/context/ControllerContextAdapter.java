/*
 * (C) Copyright 2020 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.context;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.context.PortalControllerContext;


public class ControllerContextAdapter {

    public static ControllerContext getControllerContext(PortalControllerContext portalContext) {

        ControllerContext controllerContext = null;
        if (portalContext.getRequest() != null) {
            controllerContext = (ControllerContext) portalContext.getRequest().getAttribute("osivia.controller");
            return controllerContext;
        }

        return controllerContext;

    }
}