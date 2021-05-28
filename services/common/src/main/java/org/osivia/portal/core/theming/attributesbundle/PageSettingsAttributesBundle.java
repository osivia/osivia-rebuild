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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.Predicate;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.theming.IAttributesBundle;

import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;


/**
 * Page settings attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class PageSettingsAttributesBundle implements IAttributesBundle {

       /** Singleton instance. */
    private static PageSettingsAttributesBundle instance;

    /** Bundle factory. */
    private final IBundleFactory bundleFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;



    /**
     * Private constructor.
     */
    private PageSettingsAttributesBundle() {
        super();

        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

        // Attribute names
        this.names = new TreeSet<String>();
        this.names.add(InternalConstants.ATTR_USER_ADMIN);

    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static PageSettingsAttributesBundle getInstance() {
        if (instance == null) {
            instance = new PageSettingsAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Attributes initialization to prevent multiple fill call
        for (String attributeName : this.names) {
            attributes.put(attributeName, null);
        }

        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();

        if (PageCustomizerInterceptor.isAdministrator(controllerContext)) {

        }


    }



    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }


   
}
