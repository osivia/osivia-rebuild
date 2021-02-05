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


import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;

import org.osivia.portal.core.content.IPublicationManager;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Start dynamic window in new dynamic page command.
 *
 * @see DynamicCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class StartDynamicWindowInNewPageCommand extends DynamicCommand {

    /** Parent identifier. */
    private UniversalID parentId;
    /** Page name. */
    private UniversalID templateId;
    /** Page display name. */
    private String displayName;
    /** Window portlet instance identifier. */
    private String instanceId;
    /** Window properties. */
    private Map<String, String> dynaProps;
    /** Window parameters. */
    private Map<String, String> params;


    /** Command info. */
    private final CommandInfo info;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Constructor.
     */
    public StartDynamicWindowInNewPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);
    }
    
    private IPublicationManager publicationManager;
    
    private IPublicationManager getPublicationManager() {
        if (publicationManager == null) {
            publicationManager = Locator.getService(IPublicationManager.class);
        }
        return publicationManager;
    }


    /**
     * Constructor.
     *
     * @param parentId parent identifier
     * @param pageName page name
     * @param displayName page display name
     * @param portletInstance window portlet instance identifier
     * @param props window properties
     * @param params window parameters
     */
    public StartDynamicWindowInNewPageCommand(UniversalID parentId, UniversalID templateId, String displayName, String portletInstance, Map<String, String> props,
            Map<String, String> params) {
        this();
        this.parentId = parentId;
        this.templateId = templateId;
        this.displayName = displayName;
        this.instanceId = portletInstance;
        this.dynaProps = props;
        this.params = params;
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
            
            /*
            // Generic template name
            String templateName = this.dynaProps.get("template.name");
            if (templateName == null) {

                String genericTemplateName = System.getProperty("template.generic.name");
                if (genericTemplateName == null) {
                    throw new ControllerException("template.generic.name undefined. Cannot instantiate this page");
                } else
                    templateName = genericTemplateName;
            }

            // Generic template region name
            String genericTemplateRegion = System.getProperty("template.generic.region");
            if (genericTemplateRegion == null) {
                throw new ControllerException("template.generic.region undefined. Cannot instantiate this page");
            }
*/
            
            String genericTemplateRegion="modal-region";

            // Page display names
            Map<Locale, String> displayNames = new HashMap<Locale, String>();
            if (this.displayName != null) {
                displayNames.put(Locale.FRENCH, this.displayName);
            }



            // Window properties
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.putAll(this.dynaProps);
            windowProps.put("osivia.dynamic.disable.close", "1");

            
            // Page properties
            Map<String, String> properties = new HashMap<String, String>();


            if ("normal".equals(this.dynaProps.get("osivia.windowState"))) {
                properties.put("osivia.windowState", "normal");
            }
            
            /* Make this page restorable */
             
            properties.put("osivia.initialWindowInstance", instanceId);

            if (windowProps != null) {
                Map<String, List<String>> initProps = new HashMap<>();
                for (String hKey : windowProps.keySet()) {
                    java.util.List<String> lProps = new ArrayList<String>();
                    lProps.add(windowProps.get(hKey));
                    initProps.put(hKey, lProps);
                }
                properties.put("osivia.initialWindowProps", PageParametersEncoder.encodeProperties(initProps));                
            }

            if (params != null) {
               Map<String, List<String>> initParams = new HashMap<>();
                for (String hKey : params.keySet()) {
                    java.util.List<String> lProps = new ArrayList<String>();
                    lProps.add(params.get(hKey));
                    initParams.put(hKey, lProps);
                }
                properties.put("osivia.initialWindowParams", PageParametersEncoder.encodeProperties(initParams));
            }



            // New page identifier
            PortalControllerContext portalCtx = new PortalControllerContext( this.context.getServerInvocation().getServerContext().getClientRequest());
            PortalObjectId pageId = getPublicationManager().getPageId(portalCtx, parentId, templateId);

            // Nuxeo command
            StartDynamicWindowCommand windowCommand = this.applicationContext.getBean(StartDynamicWindowCommand.class, pageId.toString(PortalObjectPath.SAFEST_FORMAT), genericTemplateRegion,
                    this.instanceId, "virtual", windowProps, this.params);
            

            return this.context.execute(windowCommand);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

}
