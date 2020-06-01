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
package org.osivia.portal.core.portalcommands;

import java.net.URLDecoder;

import org.apache.commons.lang3.CharEncoding;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Default command factory service.
 *
 * @see AbstractCommandFactory
 */
public class DefaultCommandFactoryService extends AbstractCommandFactory {

    
    @Autowired
    private ApplicationContext applicationContext;

    
    /**
     * Default constructor.
     */
    public DefaultCommandFactoryService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        try {
            ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();

            if (parameterMap != null) {
                // Action name
                String action = parameterMap.getValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME);

                // Window identifier
                String windowId;
                String[] windowIdParameters = parameterMap.get("windowId");
                if (windowIdParameters == null) {
                    windowId = null;
                } else {
                    windowId = URLDecoder.decode(windowIdParameters[0], CharEncoding.UTF_8);
                }

   
                if ("startDynamicWindow".equals(action)) {
                    String pageId = null;
                    String instanceId = null;
                    String regionId = null;
                    String windowName = null;
                    String windowProps = null;
                    String params = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("regionId") != null) && (parameterMap.get("instanceId") != null)
                            && (parameterMap.get("windowName") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        regionId = URLDecoder.decode(parameterMap.get("regionId")[0], CharEncoding.UTF_8);
                        instanceId = URLDecoder.decode(parameterMap.get("instanceId")[0], CharEncoding.UTF_8);
                        windowName = URLDecoder.decode(parameterMap.get("windowName")[0], CharEncoding.UTF_8);
                        windowProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        params = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);

                        // Nuxeo command
                        StartDynamicWindowCommand command = this.applicationContext.getBean(StartDynamicWindowCommand.class, pageId, regionId, instanceId, windowName, WindowPropertiesEncoder.decodeProperties(windowProps),
                              WindowPropertiesEncoder.decodeProperties(params));
                        
                         return command;

//                        return new StartDynamicWindowCommand(pageId, regionId, instanceId, windowName, WindowPropertiesEncoder.decodeProperties(windowProps),
//                                WindowPropertiesEncoder.decodeProperties(params));
                    }
                }

            }
        } catch (Exception e) {
            // DO NOTHING

        }

        return null;
    }
}
