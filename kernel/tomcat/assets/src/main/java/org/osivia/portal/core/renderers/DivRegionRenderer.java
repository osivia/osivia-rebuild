/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.osivia.portal.core.renderers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.customizers.RegionsDefaultCustomizerPortlet;
import org.osivia.portal.core.page.PageProperties;



public class DivRegionRenderer extends AbstractObjectRenderer implements RegionRenderer {


    /** list of regions in head. */
    private final List<String> headerRegions;
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;
    

    
    /**
     * Constructor.
     */
    public DivRegionRenderer() {
        super();
        
        this.headerRegions = new ArrayList<String>();
       // this.headerRegions.add(RegionsDefaultCustomizerPortlet.REGION_HEADER_METADATA);
        
        IInternationalizationService internationalizationService = Locator.getService(IInternationalizationService.class);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {


        PrintWriter markup = rendererContext.getWriter();
        
        // Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("<div");

            if (rrc.getCSSId() != null) {
                markup.print(" id='");
                markup.print(rrc.getCSSId());
                markup.print("'");
            }


            markup.print(">");
        }
    }


    /**
     * {@inheritDoc}
     */
    public void renderBody(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {

     


        for (Iterator i = rrc.getWindows().iterator(); i.hasNext();)
        {
            WindowRendererContext wrc = (WindowRendererContext)i.next();
            
            String regionLayoutWindowClass = rendererContext.getProperty(InternalConstants.CMS_REGION_LAYOUT_CLASS);

           
           PrintWriter markup = rendererContext.getWriter();

           if (!this.headerRegions.contains(rrc.getCSSId())) {

               markup.print("<div class='");
               markup.print(StringUtils.trimToEmpty(regionLayoutWindowClass));
               markup.println("'>");
           }
           
           rendererContext.render(wrc);
           
           if (!this.headerRegions.contains(rrc.getCSSId())) {
               markup.println("</div>");
           }
           
           
           

           
        }




    }


    /**
     * {@inheritDoc}
     */
    public void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        
        PageProperties properties = PageProperties.getProperties();
        
        // Locale
        Locale locale = LocaleUtils.toLocale(PageProperties.getProperties().getPagePropertiesMap().get( InternalConstants.LOCALE_PROPERTY));
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Bundle bundle = this.bundleFactory.getBundle(locale);       

        PrintWriter markup = rendererContext.getWriter();

         
        String dropUrl = properties.getPagePropertiesMap().get("osivia.cms.edition.url");
        if( dropUrl != null)    {
            String sRegions = PageProperties.getProperties().getPagePropertiesMap().get("cms.regions");
            
            if( sRegions != null)    {
                List<String> regions = Arrays.asList(sRegions.split(","));

                if( regions.contains(rrc.getId()))   {
                    dropUrl+= "&javax.portlet.action=drop&action=1&region="+rrc.getId();
                    markup.print("<div class=\"cms-edition-droppable border border-primary my-2 clearfix\" data-drop-url=\""+dropUrl+"\">");
                    
                    String addPortletUrl = properties.getPagePropertiesMap().get("osivia.cms.edition.addPortletUrl."+rrc.getId());

                    
                    markup.print("<a href=\"javascript:\" class=\"btn\" data-target=\"#osivia-modal\" data-load-url=\""+addPortletUrl+"\" data-title=\""+bundle.getString("ADMIN_PORTLET_APP_LIST")+"\">\n"
                    + "        <i class=\"glyphicons glyphicons-basic-square-empty-plus\"></i>\n"
                    + "        <span class=\"d-md-none\">"+bundle.getString("ADMIN_PORTLET_APP_LIST")+"</span>\n"
                    + "    </a>");
                    
                    
                    markup.print("</div>");
                }
            }
       }                 

        // End of Main DIV region (not shown in <head> tag)
        if (!this.headerRegions.contains(rrc.getCSSId())) {
            markup.print("</div>");
        }

    }


}
