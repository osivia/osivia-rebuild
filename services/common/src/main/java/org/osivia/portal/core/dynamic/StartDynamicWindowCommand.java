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

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Start dynamic window command.
 *
 * @see DynamicCommand
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class StartDynamicWindowCommand extends DynamicCommand {


    /** Command info. */
    private static final CommandInfo info = new ActionCommandInfo(false);


    @Autowired IDynamicService dynamicService;

    /** Page identifier. */
    private String pageId;
    /** Region identifier. */
    private String regionId;
    /** Instance identifier. */
    private String instanceId;
    /** Window name. */
    private String windowName;
    /** Dynamic properties. */
    private Map<String, String> dynaProps;
    /** Parameters. */
    private Map<String, String> params;


    /**
     * Constructor.
     */
    public StartDynamicWindowCommand() {
    }


    /**
     * Constructor.
     *
     * @param pageId page identifier
     * @param regionId region identifier
     * @param portletInstance portlet instance
     * @param windowName window name
     * @param props dynamic properties
     * @param params parameters
     * @param addTobreadcrumb add to breadcrumb indicator
     * @param editionState edition state
     */
    public StartDynamicWindowCommand(String pageId, String regionId, String portletInstance, String windowName, Map<String, String> props,
            Map<String, String> params) {
        this.pageId = pageId;
        this.regionId = regionId;
        this.instanceId = portletInstance;
        this.windowName = windowName;
        this.dynaProps = props;
        this.params = params;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            // Récupération page
            PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
            Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            if (page == null) {
                // La page dynamique n'existe plus
                // Redirection vers la page par défaut du portail
                Portal portal = this.getControllerContext().getController().getPortalObjectContainer().getContext().getDefaultPortal();
                return new UpdatePageResponse(portal.getDefaultPage().getId());
            }

  
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, this.regionId);

            for (String dynaKey : this.dynaProps.keySet()) {
                properties.put(dynaKey, this.dynaProps.get(dynaKey));
            }
            


            String parentPath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);

            dynamicService.startDynamicWindow(new PortalControllerContext(this.getControllerContext().getServerInvocation().getServerContext().getClientRequest()), parentPath, this.windowName, regionId, instanceId, properties);
           
            return new UpdatePageResponse(page.getId());
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }




    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

}
