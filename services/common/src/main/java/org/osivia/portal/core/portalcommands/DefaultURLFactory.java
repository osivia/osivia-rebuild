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


import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;


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
