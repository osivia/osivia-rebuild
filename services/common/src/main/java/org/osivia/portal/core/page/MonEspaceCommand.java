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
package org.osivia.portal.core.page;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Portal;


public class MonEspaceCommand extends ControllerCommand {

    /** . */
    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(MonEspaceCommand.class);

    /** . */
    private String portalName;

    public MonEspaceCommand() {
        this(null);
    }

    public MonEspaceCommand(String portalName) {
        this.portalName = portalName;
    }

    @Override
    public CommandInfo getInfo() {
        return info;
    }

    public String getPortalName() {
        return this.portalName;
    }

    @Override
    public ControllerResponse execute() throws ControllerException {
        try {

            Portal portal = null;

            String portalName = this.getPortalName();

            if (portalName != null) {
                portal = this.getControllerContext().getController().getPortalObjectContainer().getContext().getPortal(
                        portalName);
            } else {
                portal = this.getControllerContext().getController().getPortalObjectContainer().getContext()
                        .getDefaultPortal();
            }

            HttpServletRequest request = this.getControllerContext().getServerInvocation().getServerContext().getClientRequest();

            if (request.getUserPrincipal() == null) {
                return new SecurityErrorResponse("Vous devez être connecté", SecurityErrorResponse.NOT_AUTHORIZED,
                        false);
            }

            // No profile
            PageURL url = new PageURL(portal.getId(), this.getControllerContext());
            url.setRelative(false);
            return new RedirectionResponse(url.toString() );


        } catch (Exception e) {
            if (!(e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw (ControllerException) e;
            }
        }

    }
}
