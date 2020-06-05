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
 */
package org.osivia.portal.core.dynamic;

import java.util.Locale;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Start dynamic page command.
 *
 * @see DynamicCommand
 */
public class StartDynamicPageCommand extends DynamicCommand {

    /** Parent identifier. */
    private String parentId;
    /** Template identifier. */
    private String templateId;
    /** Page name. */
    private String pageName;
    /** Display names. */
    private Map<Locale, String> displayNames;
    /** Properties. */
    private Map<String, String> properties;
    /** Parameters. */
    private Map<String, String> parameters;
    /** CMS parameters. */
    private Map<String, String> cmsParameters;


    /** Command info. */
    private final CommandInfo info;


    @Autowired IDynamicService dynamicService;

    /**
     * Constructor.
     */
    public StartDynamicPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);

     }


    /**
     * Constructor.
     *
     * @param parentId parent identifier
     * @param pageName page name
     * @param displayNames page display names
     * @param templateId template identifier
     * @param properties page properties
     * @param parameters page parameters
     */
    public StartDynamicPageCommand(String parentId, String pageName, Map<Locale, String> displayNames, String templateId, Map<String, String> properties,
            Map<String, String> parameters) {
        this();
        this.parentId = parentId;
        this.pageName = pageName;
        this.templateId = templateId;
        this.displayNames = displayNames;
        this.properties = properties;
        this.parameters = parameters;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {

            String pagePath = dynamicService.startDynamicPage(new PortalControllerContext(this.getControllerContext().getServerInvocation().getServerContext().getClientRequest()), parentId, pageName, displayNames, templateId, properties,
                    parameters);
            
            PortalObjectId pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);


            return new UpdatePageResponse(pageId);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


}
