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
package org.osivia.portal.core.customizers;

import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.api.theming.IRenderedRegions;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import java.util.Arrays;
import java.util.Map;

/**
 * Technical portlet for regions default customization.
 *
 * @author Cédric Krommenhoek
 * @see GenericPortlet
 * @see ICustomizationModule
 */
public class RegionsDefaultCustomizerPortlet extends GenericPortlet implements ICustomizationModule {

    /**
     * Region : Header metadatas.
     */
    public static final String REGION_HEADER_METADATA = "header-metadata";

    /**
     * Customizer name.
     */
    private static final String CUSTOMIZER_NAME = "osivia.portal.regions.customizer";
    /**
     * Customization modules repository attribute name.
     */
    private static final String ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY = "CustomizationModulesRepository";

    /**
     * Breadcrumb path init parameter name.
     */
    private static final String BREADCRUMB_PATH_INIT_PARAM = "osivia.portal.customizer.regions.breadcrumb.path";
    /**
     * Footer path init parameter name.
     */
    private static final String FOOTER_PATH_INIT_PARAM = "osivia.portal.customizer.regions.footer.path";
    /**
     * Search path init parameter name.
     */
    private static final String SEARCH_PATH_INIT_PARAM = "osivia.portal.customizer.regions.search.path";
    /**
     * Web search path init parameter name.
     */
    private static final String WEB_SEARCH_PATH_INIT_PARAM = "osivia.portal.customizer.regions.web.search.path";
    /**
     * Tabs path init parameter name.
     */
    private static final String HEADER_METADATA_PATH_INIT_PARAM = "osivia.portal.customizer.regions.header.metadata.path";
    /**
     * Tabs path init parameter name.
     */
    private static final String TABS_PATH_INIT_PARAM = "osivia.portal.customizer.regions.tabs.path";
    /**
     * Toolbar path init parameter name.
     */
    private static final String TOOLBAR_PATH_INIT_PARAM = "osivia.portal.customizer.regions.toolbar.path";
    /**
     * Web toolbar path init parameter name.
     */
    private static final String WEB_TOOLBAR_PATH_INIT_PARAM = "osivia.portal.customizer.regions.web.toolbar.path";
    /**
     * Drawer toolbar path init parameter name.
     */
    private static final String DRAWER_TOOLBAR_PATH_INIT_PARAM = "osivia.portal.customizer.regions.drawer.toolbar.path";
    /**
     * Page settings path init parameter name.
     */
    private static final String PAGE_SETTINGS_PATH_INIT_PARAM = "osivia.portal.customizer.regions.page.settings.path";
    /**
     * Internationalization customization module metadatas.
     */
    private final CustomizationModuleMetadatas metadatas;
    /**
     * Customization modules repository.
     */
    private ICustomizationModulesRepository repository;


    /**
     * Constructor.
     */
    public RegionsDefaultCustomizerPortlet() {
        super();
        this.metadatas = this.generateMetadatas();
    }


    /**
     * Utility method used to generate attributes bundles customization module metadatas.
     *
     * @return metadatas
     */
    private final CustomizationModuleMetadatas generateMetadatas() {
        CustomizationModuleMetadatas metadatas = new CustomizationModuleMetadatas();
        metadatas.setName(CUSTOMIZER_NAME);
        metadatas.setOrder(-1);
        metadatas.setModule(this);
        metadatas.setCustomizationIDs(Arrays.asList(IRenderedRegions.CUSTOMIZER_ID));
        return metadatas;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws PortletException {
        super.init();
        this.repository = (ICustomizationModulesRepository) this.getPortletContext().getAttribute(ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY);
        this.repository.register(this.metadatas);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();
        this.repository.unregister(this.metadatas);
    }


    /**
     * {@inheritDoc}
     */
    public void customize(CustomizationContext context) {
        Map<String, Object> attributes = context.getAttributes();
        IRenderedRegions renderedRegions = (IRenderedRegions) attributes.get(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_RENDERED_REGIONS);


        // Header metadata default region
        renderedRegions.defineDefaultRenderedRegion("header-metadata", this.getInitParameter(HEADER_METADATA_PATH_INIT_PARAM));


        // Breadcrumb default region
        renderedRegions.defineDefaultRenderedRegion("breadcrumb", this.getInitParameter(BREADCRUMB_PATH_INIT_PARAM));

        /*
        if (renderedRegions.isSpaceSite()) {
            // Web toolbar default region
            renderedRegions.defineDefaultRenderedRegion("toolbar", this.getInitParameter(WEB_TOOLBAR_PATH_INIT_PARAM));
            // Web search default region
            renderedRegions.defineDefaultRenderedRegion("search", this.getInitParameter(WEB_SEARCH_PATH_INIT_PARAM));
        } else {
            // Toolbar default region
            renderedRegions.defineDefaultRenderedRegion("toolbar", this.getInitParameter(TOOLBAR_PATH_INIT_PARAM));
            // Drawer toolbar default region
//            renderedRegions.defineDefaultRenderedRegion("drawer-toolbar", this.getInitParameter(DRAWER_TOOLBAR_PATH_INIT_PARAM));
            // Search default region
            renderedRegions.defineDefaultRenderedRegion("search", this.getInitParameter(SEARCH_PATH_INIT_PARAM));
            // Tabs default region
            renderedRegions.defineDefaultRenderedRegion("tabs", this.getInitParameter(TABS_PATH_INIT_PARAM));
            // Footer default region
            renderedRegions.defineDefaultRenderedRegion("footer", this.getInitParameter(FOOTER_PATH_INIT_PARAM));
        }
*/
        // Page settings fixed region
        renderedRegions.defineFixedRenderedRegion("pageSettings", this.getInitParameter(PAGE_SETTINGS_PATH_INIT_PARAM));
    }

}
