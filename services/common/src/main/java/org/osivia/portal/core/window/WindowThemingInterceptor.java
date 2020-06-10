/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.core.window;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.osivia.portal.api.page.PageProperties;
import org.osivia.portal.core.constants.InternalConstants;


@SuppressWarnings("unchecked")
public class WindowThemingInterceptor extends ControllerInterceptor {


    private static Log log = LogFactory.getLog(WindowThemingInterceptor.class);


    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {


        ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

        if (cmd instanceof RenderWindowCommand) {

            RenderWindowCommand rwc = (RenderWindowCommand) cmd;



            PageProperties properties = PageProperties.getProperties();

            Window window = rwc.getWindow();
            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);



            // Title
            String title = window.getDeclaredProperty(InternalConstants.PROP_WINDOW_TITLE);
            properties.setWindowProperty(windowId, InternalConstants.PROP_WINDOW_TITLE, title);
            

            properties.setWindowProperty(windowId, "osivia.displayTitle", "1".equals(window.getDeclaredProperty("osivia.hideTitle")) ? null : "1");
            
            // Decorators
            String decorators = "1".equals(window.getDeclaredProperty("osivia.hideDecorators")) ? null : "1";
            properties.setWindowProperty(windowId, "osivia.displayDecorators", decorators);



            String customStyle = window.getDeclaredProperty("osivia.style");

            if (customStyle == null) {
                customStyle = "";
            }


            properties.setWindowProperty(windowId, "osivia.style", customStyle);

        }

        return resp;
    }


}
