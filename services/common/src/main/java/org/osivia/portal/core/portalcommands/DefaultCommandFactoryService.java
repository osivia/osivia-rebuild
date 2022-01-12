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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.page.RestorePageCommand;
import org.osivia.portal.core.tasks.UpdateTaskCommand;
import org.osivia.portal.core.tasks.ViewTaskCommand;
import org.osivia.portal.core.ui.SaveResizableWidthCommand;
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
                    }
                }
                
                if ("startDynamicPage".equals(action)) {
                    String parentId = null;
                    String pageName = null;
                    String templateId = null;
                    String pageProps = null;
                    String pageParams = null;

                    if ((parameterMap.get("parentId") != null) && (parameterMap.get("pageName") != null) && (parameterMap.get("templateId") != null)) {
                        parentId = URLDecoder.decode(parameterMap.get("parentId")[0], CharEncoding.UTF_8);
                        pageName = URLDecoder.decode(parameterMap.get("pageName")[0], CharEncoding.UTF_8);
                        templateId = URLDecoder.decode(parameterMap.get("templateId")[0], CharEncoding.UTF_8);
                        pageProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        pageParams = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);

                        return new StartDynamicPageCommand(parentId, pageName, null, templateId, WindowPropertiesEncoder.decodeProperties(pageProps),
                                WindowPropertiesEncoder.decodeProperties(pageParams), null);
                    }
                }           
                
                if ("startDynamicWindowInNewPage".equals(action)) {

                    String parentId = null;
                    UniversalID pageId = null;
                    String pageDisplayName = null;
                    String instanceId = null;
                    String windowProps = null;
                    String params = null;
                    String templateRegion = null;


                    if ((parameterMap.get("parentId") != null) && (parameterMap.get("instanceId") != null)) {

                        parentId = URLDecoder.decode(parameterMap.get("parentId")[0], CharEncoding.UTF_8);
                        if( parameterMap.get("pageId") != null)
                            pageId = new UniversalID(URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8));
                        pageDisplayName = URLDecoder.decode(parameterMap.get("pageDisplayName")[0], CharEncoding.UTF_8);
                        instanceId = URLDecoder.decode(parameterMap.get("instanceId")[0], CharEncoding.UTF_8);
                        windowProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        params = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);
                        if( parameterMap.get("templateRegion") != null)
                                templateRegion = URLDecoder.decode(parameterMap.get("templateRegion")[0], CharEncoding.UTF_8);

                        return this.applicationContext.getBean(StartDynamicWindowInNewPageCommand.class, new UniversalID(parentId), pageId, pageDisplayName, instanceId,
                                WindowPropertiesEncoder.decodeProperties(windowProps), WindowPropertiesEncoder.decodeProperties(params), templateRegion);
                    }
                }
                
                
                if ("restore".equals(action)) {
 
                        // Nuxeo command
                        RestorePageCommand command = this.applicationContext.getBean(RestorePageCommand.class);
                        
                         return command;

                }      
                
                
                // Update task command
                if (UpdateTaskCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] uuidParameter = parameterMap.get(UpdateTaskCommand.UUID_PARAMETER);
                    String[] actionIdParameter = parameterMap.get(UpdateTaskCommand.ACTION_ID_PARAMETER);
                    String[] variablesParameter = parameterMap.get(UpdateTaskCommand.VARIABLES_PARAMETER);

                    if (ArrayUtils.isNotEmpty(uuidParameter) && ArrayUtils.isNotEmpty(actionIdParameter)) {
                        // UUID
                        UUID uuid = UUID.fromString(URLDecoder.decode(uuidParameter[0], CharEncoding.UTF_8));
                        // Action identifier
                        String actionId = URLDecoder.decode(actionIdParameter[0], CharEncoding.UTF_8);
                        // Variables
                        Map<String, String> variables;
                        if (ArrayUtils.isEmpty(variablesParameter)) {
                            variables = null;
                        } else {
                            String[] properties = StringUtils.split(URLDecoder.decode(variablesParameter[0], CharEncoding.UTF_8), "&");
                            variables = new HashMap<String, String>(properties.length);
                            for (String property : properties) {
                                String[] entry = StringUtils.split(property, "=");
                                if (entry.length == 2) {
                                    String key = StringEscapeUtils.unescapeHtml4(entry[0]);
                                    String value = StringEscapeUtils.unescapeHtml4(entry[1]);
                                    variables.put(key, value);
                                }
                            }
                        }

                        return new UpdateTaskCommand(uuid, actionId, variables);
                    }
                }
                
                
                // Update task command
                if (ViewTaskCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] uuidParameter = parameterMap.get(UpdateTaskCommand.UUID_PARAMETER);


                    if (ArrayUtils.isNotEmpty(uuidParameter)) {
                        // UUID
                        UUID uuid = UUID.fromString(URLDecoder.decode(uuidParameter[0], CharEncoding.UTF_8));


                        return new ViewTaskCommand(uuid);
                    }
                }
                
                
                // Save jQuery UI resizable component value
                if (SaveResizableWidthCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] linkedToTasksParameter = parameterMap.get(SaveResizableWidthCommand.LINKED_TO_TASKS_PARAMETER);
                    String[] widthParameter = parameterMap.get(SaveResizableWidthCommand.WIDTH_PARAMETER);

                    // Linked to tasks indicator
                    boolean linkedToTasks;
                    if (ArrayUtils.isEmpty(linkedToTasksParameter)) {
                        linkedToTasks = false;
                    } else {
                        linkedToTasks = BooleanUtils.toBoolean(URLDecoder.decode(linkedToTasksParameter[0], CharEncoding.UTF_8));
                    }
                    
                    // Resizable width
                    Integer width;
                    if (ArrayUtils.isEmpty(widthParameter)) {
                        width = null;
                    } else {
                        width = NumberUtils.toInt(URLDecoder.decode(widthParameter[0], CharEncoding.UTF_8));
                    }

                    return new SaveResizableWidthCommand(linkedToTasks, width);
                }                

            }
        } catch (Exception e) {
            // DO NOTHING
            System.out.println("erreur DefaultCommandFactoryService");
        }

        return null;
    }
}
