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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPortalCommand;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.content.IPublicationManager;
import org.osivia.portal.core.content.ViewContentCommand;
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
    
    
    @Autowired  IPublicationManager publicationManager;
    


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
                PortalCommand cmd = new ViewPortalCommand(portal.getId());
                String url = this.getControllerContext().renderURL(cmd, null, null);  
                return new RedirectionResponse(url);
            }

  
            if (BooleanUtils.toBoolean(dynaProps.get("osivia.navigation.reset"))) {
                // Reset CMS navigation
                /* Make this page restorable */
                
                Map<String, String> pageProperties = new HashMap<String, String>();
                
                pageProperties.put("osivia.initialWindowInstance", instanceId);
                
                pageProperties.put("osivia.initialWindowRegion", this.regionId);

                if (dynaProps != null && dynaProps.size() > 0) {
                    Map<String, List<String>> initProps = new HashMap<>();
                    for (String hKey : dynaProps.keySet()) {
                        java.util.List<String> lProps = new ArrayList<String>();
                        lProps.add(dynaProps.get(hKey));
                        initProps.put(hKey, lProps);
                    }
                    pageProperties.put("osivia.initialWindowProps", PageParametersEncoder.encodeProperties(initProps));                
                }

                if (params != null && params.size() > 0) {
                   Map<String, List<String>> initParams = new HashMap<>();
                    for (String hKey : params.keySet()) {
                        java.util.List<String> lProps = new ArrayList<String>();
                        lProps.add(params.get(hKey));
                        initParams.put(hKey, lProps);
                    }
                    pageProperties.put("osivia.initialWindowParams", PageParametersEncoder.encodeProperties(initParams));
                }
                
                
                // New page identifier
                UniversalID spaceId = new UniversalID(page.getProperty("osivia.spaceId"));
                PortalControllerContext portalCtx = new PortalControllerContext( this.context.getServerInvocation().getServerContext().getClientRequest());
                PortalObjectId pageId = publicationManager.getPageId(portalCtx, null, spaceId, pageProperties, null, null);


                
                return new UpdatePageResponse(pageId);
            }
            
            
            
            
            
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, this.regionId);

            for (String dynaKey : this.dynaProps.keySet()) {
                properties.put(dynaKey, this.dynaProps.get(dynaKey));
            }
 
            
            
           
            
            

            String parentPath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);

            dynamicService.startDynamicWindow(new PortalControllerContext(this.getControllerContext().getServerInvocation().getServerContext().getClientRequest()), parentPath, this.windowName, regionId, instanceId, properties, this.params);
           
            /*

            PortalObjectId windowId = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString().concat("/").concat(this.windowName),
                    PortalObjectPath.CANONICAL_FORMAT));
            
            // Maj du breadcrumb
            Breadcrumb breadcrumb = (Breadcrumb) this.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb");

            if (breadcrumb == null) {
                breadcrumb = new Breadcrumb();
            }
            else    {
                breadcrumb.getChildren().clear();
            }

            // Ajout du nouvel item
            PageURL url = new PageURL(page.getId(), this.getControllerContext());

            String name = properties.get("osivia.title");

            BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), windowId, false);
            
            // Task identifier
            if (this.dynaProps != null) {
                String taskId = this.dynaProps.get(ITaskbarService.TASK_ID_WINDOW_PROPERTY);
                item.setTaskId(taskId);
            }            


            breadcrumb.getChildren().add(item);

            this.getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb", breadcrumb);
         */
            
             
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
