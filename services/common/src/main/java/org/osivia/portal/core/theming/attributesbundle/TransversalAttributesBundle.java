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
package org.osivia.portal.core.theming.attributesbundle;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;


import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.IInternalAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;

import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;

/**
 * Transversal attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class TransversalAttributesBundle implements IInternalAttributesBundle {

    /** Singleton instance. */
    private static TransversalAttributesBundle instance;



    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private TransversalAttributesBundle() {
        super();

  
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        this.names = new TreeSet<String>();
        this.names.add(InternalConstants.ATTR_CONTROLLER_CONTEXT);
        this.names.add(InternalConstants.ATTR_CMS_PATH);
        this.names.add(InternalConstants.ATTR_COMMAND_PREFIX);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL);
        this.names.add(Constants.ATTR_PAGE_CATEGORY);
        this.names.add(Constants.ATTR_USER_DATAS);
        this.names.add(Constants.ATTR_SPACE_CONFIG);
        this.names.add(Constants.ATTR_PORTAL_CTX);
        this.names.add(Constants.ATTR_URL_FACTORY);
        this.names.add(Constants.ATTR_PORTAL_HOME_URL);
        this.names.add(Constants.ATTR_WIZARD_MODE);
        this.names.add(Constants.ATTR_SPACE_SITE_INDICATOR);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static TransversalAttributesBundle getInstance() {
        if (instance == null) {
            instance = new TransversalAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        attributes.put(InternalConstants.ATTR_CONTROLLER_CONTEXT, controllerContext);
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        
        // Request
        HttpServletRequest request = serverContext.getClientRequest();

        // Current page
        Page page = renderPageCommand.getPage();


        // Generic command URL
        String commandUrl = serverContext.getPortalContextPath() + "/commands";
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL, commandUrl);
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(renderPageCommand.getControllerContext().getServerInvocation().getServerContext().getClientRequest());
        attributes.put(Constants.ATTR_PORTAL_CTX, portalControllerContext);

		// User datas
        Map<String, Object> userDatas = (Map<String, Object>) controllerContext.getServerInvocation().getAttribute(Scope.SESSION_SCOPE, "osivia.userDatas");
        attributes.put(Constants.ATTR_USER_DATAS, userDatas);


        // logged person - v3.3
        DirectoryPerson person = (DirectoryPerson) controllerContext.getServerInvocation().getAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON);
        attributes.put(Constants.ATTR_LOGGED_PERSON, person);

        attributes.put(Constants.ATTR_PAGE_CATEGORY, renderPageCommand.getPage().getProperty("osivia.pageCategory"));


        // URL factory
        attributes.put(Constants.ATTR_URL_FACTORY, this.urlFactory);

        // Portal home page URL
        String homeUrl = serverContext.getPortalContextPath();
        if( request.getUserPrincipal() != null && ! homeUrl.endsWith("/auth"))
            homeUrl += "/auth";
        attributes.put(Constants.ATTR_PORTAL_HOME_URL, homeUrl);

 

    }




    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
