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
package org.osivia.portal.core.theming.attributesbundle;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.IInternalAttributesBundle;
import org.osivia.portal.core.internationalization.InternationalizationUtils;

/**
 * Generator of the <head> meta datas informations as title, meta:author, meta:description...
 *
 * @see IAttributesBundle
 */
public final class HeaderMetadataAttributesBundle implements IInternalAttributesBundle {

    /** Singleton instance. */
    private static HeaderMetadataAttributesBundle instance;



    /** Header metadata attributes names. */
    private final Set<String> names;

    

    /**
     * Default constructor.
     */
    private HeaderMetadataAttributesBundle() {


        // Properties
        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_HEADER_TITLE);
        this.names.add(Constants.ATTR_HEADER_APPLICATION_NAME);

    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static HeaderMetadataAttributesBundle getInstance() {
        if (instance == null) {
            instance = new HeaderMetadataAttributesBundle();
        }
        return instance;
    }




    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }





    @Override
    public void fill(ControllerContext controllerContext, Page page, Map<String, Object> attributes) throws ControllerException {
        // Title
        String title="";
        // Canonical URL
        String canonicalUrl="";
        
        // Current portal
        Portal portal = page.getPortal();
        // Current locale
        Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();
        
        // Application name
        String applicationName = InternationalizationUtils.getApplicationName(portal, locale);
        attributes.put(Constants.ATTR_HEADER_APPLICATION_NAME, applicationName);

        
   /*     
        String navigationId = page.getProperties().get("osivia.navigationId");
        if( navigationId != null) {
            
        }
*/
        

        attributes.put(Constants.ATTR_HEADER_TITLE, title);

    }

}
