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
package org.osivia.portal.api.dynamic;


import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;



import java.util.Map;

/**
 * Portal dynamic Window API interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface IDynamicWindowService {


    /**
     * Start dynamic Window
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance         portlet instance
     * @param windowProperties        window properties
     * @return URL
     */
    void startDynamicWindow(PortalControllerContext portalControllerContext, String regionId, String portletInstance, Map<String, String> windowProperties)  throws PortalException;


  
}
