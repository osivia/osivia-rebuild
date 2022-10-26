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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.cms.edition.CMSEditionChangeModeCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.page.RestorePageCommand;
import org.osivia.portal.core.tasks.UpdateTaskCommand;
import org.osivia.portal.core.tasks.ViewTaskCommand;
import org.osivia.portal.core.ui.SaveResizableWidthCommand;


/**
 * Default URL factory.
 *
 * @see URLFactoryDelegate
 */
public class DefaultURLFactory extends URLFactoryDelegate {

    /** Generic command action parameter name. */
    public static final String COMMAND_ACTION_PARAMETER_NAME = "action";

    /** Path. */
    private String path;


    /**
     * Default constructor.
     */
    public DefaultURLFactory() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("No null command accepted");
        }

        

        if (cmd instanceof StartDynamicWindowCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicWindow");
            return asu;


        }
        
        if (cmd instanceof StartDynamicPageCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicPage");
            return asu;
        }        
        
        if (cmd instanceof StartDynamicWindowInNewPageCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicWindowInNewPage");
            return asu;
        }
        

     
        if (cmd instanceof RestorePageCommand) {
            
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "restore");
            return asu;
        }
       
        
        if (cmd instanceof RefreshPageCommand) {
            RefreshPageCommand command = (RefreshPageCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String pageId = command.getPageId();


            try {
                asu.setParameterValue("action", "refreshPage");

                asu.setParameterValue("pageId", URLEncoder.encode(pageId, "UTF-8"));
                
                         

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }
        
        // Update task command
        if (cmd instanceof UpdateTaskCommand) {
            UpdateTaskCommand command = (UpdateTaskCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            // Command parameters
            try {
                asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, UpdateTaskCommand.ACTION);

                asu.setParameterValue(UpdateTaskCommand.UUID_PARAMETER, URLEncoder.encode(command.getUuid().toString(), CharEncoding.UTF_8));
                asu.setParameterValue(UpdateTaskCommand.ACTION_ID_PARAMETER, URLEncoder.encode(command.getActionId(), CharEncoding.UTF_8));
                if (MapUtils.isNotEmpty(command.getVariables())) {
                    List<String> variables = new ArrayList<String>(command.getVariables().size());
                    for (Entry<String, String> entry : command.getVariables().entrySet()) {
                        variables.add(StringEscapeUtils.escapeHtml4(entry.getKey()) + "=" + StringEscapeUtils.escapeHtml4(entry.getValue()));
                    }
                    asu.setParameterValue(UpdateTaskCommand.VARIABLES_PARAMETER, URLEncoder.encode(StringUtils.join(variables, "&"), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }
        
        // Update task command
        if (cmd instanceof ViewTaskCommand) {
            ViewTaskCommand command = (ViewTaskCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            // Command parameters
            try {
                asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, ViewTaskCommand.ACTION);

                asu.setParameterValue(UpdateTaskCommand.UUID_PARAMETER, URLEncoder.encode(command.getUuid().toString(), CharEncoding.UTF_8));

            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }

        
        // Save jQuery UI resizable component value
        if (cmd instanceof SaveResizableWidthCommand) {
            SaveResizableWidthCommand command = (SaveResizableWidthCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, SaveResizableWidthCommand.ACTION);

            // Parameters
            try {
                // Linked to tasks indicator
                asu.setParameterValue(SaveResizableWidthCommand.LINKED_TO_TASKS_PARAMETER,
                        URLEncoder.encode(String.valueOf(command.isLinkedToTasks()), CharEncoding.UTF_8));

                // Resizable width
                if (command.getWidth() != null) {
                    asu.setParameterValue(SaveResizableWidthCommand.WIDTH_PARAMETER, URLEncoder.encode(String.valueOf(command.getWidth()), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }

        // Change edition Mode
        if (cmd instanceof CMSEditionChangeModeCommand) {


            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, ("changeEditionMode"));

           
            return asu;
        }
        
        
        

        return null;
    }


    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Setter for path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
