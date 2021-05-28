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
package org.osivia.portal.api.theming;

import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;



/**
 * Attributes bundle interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IAttributesBundle {

    /** Regions attributes bundles customizer identifier. */
    static final String CUSTOMIZER_ID = "osivia.customizer.attributesBundles.id";
    /** Regions attributes bundles customizer name attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_NAME = "osivia.customizer.attributesBundles.name";
    /** Regions attributes bundles customizer bundle result attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_RESULT = "osivia.customizer.attributesBundles.result";


    /**
     * Fill attributes map with current region attributes.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param attributes attributes map
     * @throws ControllerException
     */
    void fill(PortalControllerContext portalCtx, Map<String, Object> attributes) throws PortalException;


    /**
     * Get attribute names set.
     *
     * @return attribute names set
     */
    Set<String> getAttributeNames();

}
