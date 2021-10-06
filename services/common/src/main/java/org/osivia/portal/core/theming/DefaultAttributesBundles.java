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
package org.osivia.portal.core.theming;

import org.osivia.portal.api.theming.IInternalAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.PageSettingsAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.ToolbarAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.TransversalAttributesBundle;
import org.osivia.portal.core.theming.attributesbundle.BreadcrumbAttributesBundle;

/**
 * Default attributes bundles enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum DefaultAttributesBundles {

//    /** "Back" function attributes bundle. */
//    BACK(BackAttributesBundle.getInstance()),
//    /** Breadcrumb attributes bundle. */
    BREADCRUMB(BreadcrumbAttributesBundle.getInstance()),
    /** Page settings attributes bundle. */
    PAGE_SETTINGS(PageSettingsAttributesBundle.getInstance()),
//        /** Search attributes bundle. */
//        SEARCH(SearchAttributesBundle.getInstance());
//    /** Header metadata. */
//    HEADER_METADATA(HeaderMetadataAttributesBundle.getInstance()),
//    /** Site map attributes bundle. */
//    SITE_MAP(SiteMapAttributesBundle.getInstance()),
//    /** Tabs attributes bundle. */
//    TABS(TabsAttributesBundle.getInstance()),
//    /** Toolbar attributes bundle. */
    TOOLBAR(ToolbarAttributesBundle.getInstance()),
    /** Transversal attributes bundle. */
    TRANSVERSAL(TransversalAttributesBundle.getInstance());


    /** Attributes bundle. */
    private final IInternalAttributesBundle bundle;


    /**
     * Constructor.
     *
     * @param bundle attributes bundle
     */
    private DefaultAttributesBundles(IInternalAttributesBundle bundle) {
        this.bundle = bundle;
    }


    /**
     * Getter for bundle.
     *
     * @return the bundle
     */
    public IInternalAttributesBundle getBundle() {
        return this.bundle;
    }

}
