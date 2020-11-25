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
package org.osivia.portal.api.customization;

/**
 * Allow to customize hooks defined by portal.
 * 
 * @author Jean-Sébastien Steux
 */
public interface ICustomizationModule {

    /** Set this identifier to use the plugin customization management. */
    String PLUGIN_ID = "osivia.customizer.cms.id";


    /**
     * Customize.
     *
     * @param customizationContext the customization context
     */
    void customize(CustomizationContext customizationContext);

}
