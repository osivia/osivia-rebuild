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
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;

    /**
     * Constructor.
     */
    public DivWindowRenderer() {
        super();
        
        IInternationalizationService internationalizationService = Locator.getService(IInternationalizationService.class);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {


        PrintWriter out = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();
        // Set current window identifier for decorators
        properties.setCurrentWindowId(wrc.getId());
        
        // Locale
        Locale locale = null;
        String currentWindowId = PageProperties.getProperties().getCurrentWindowId();
        if (currentWindowId != null) {
            locale = LocaleUtils.toLocale(PageProperties.getProperties().getWindowProperty(currentWindowId, InternalConstants.LOCALE_PROPERTY));
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Bundle bundle = this.bundleFactory.getBundle(locale);       
        
        
        // Show CMS tools indicator
        boolean showCMSTools = this.showCMSTools(wrc, properties);
        
        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.bootstrapPanelStyle"));
        


        // Hide portlet indicator
        boolean hidePortlet = !showCMSTools && BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), "osivia.hidePortlet"));
        if (hidePortlet) {
            out.println("<div class=\"dyna-window-content\" ></div>");
            return;
        }        


        // Window identifier
        String windowId = wrc.getProperty("osivia.windowId");


        // Styles

        
        String styles = properties.getWindowProperty(wrc.getId(), "osivia.style");
        if (styles != null) {
            styles = styles.replaceAll(",", " ");
        } else {
            styles = StringUtils.EMPTY;
        }


        if( Mode.ADMIN.equals(wrc.getMode()))	{
        	bootstrapPanelStyle = false;
        	styles =  StringUtils.EMPTY;
        }

        // Window root element
        out.print("<div");
        if (windowId != null) {
            out.print(" id=\"");
            out.print(windowId);
            out.print("\"");
        }

        out.print(">");
        
        


        out.print("<div class=\"dyna-window-content\">");


        if (showCMSTools) {


            String dropUrl = properties.getWindowProperty(wrc.getId(),"osivia.cms.edition.url");
            if( dropUrl == null)
                dropUrl = "";
            dropUrl+= "&javax.portlet.action=drop&action=1&targetWindow="+properties.getWindowProperty(wrc.getId(), "osivia.cms.edition.windowName");            
            
            out.print("<div class=\"cms-edition-droppable border border-primary my-2 clearfix d-flex\" data-drop-url=\""+dropUrl+"\">");            
            
            // Add
            out.print("<a href=\"javascript:\" class=\"btn btn-link\" data-target=\"#osivia-modal\" data-load-url=\""+properties.getWindowProperty(wrc.getId(),"osivia.cms.edition.addPortletUrl")+"\" data-title=\""+bundle.getString("ADMIN_PORTLET_APP_LIST")+"\">\n"
            + "        <i class=\"glyphicons glyphicons-basic-square-empty-plus\"></i>\n"
            + "        <span class=\"d-md-none\">"+bundle.getString("ADD")+"</span>\n"
            + "    </a>");
            

            out.print("<div class=\"ml-auto d-flex\">");
            
            // Edit
            
            out.print("<a class=\"btn btn-link\" href=\"javascript:\" title=\""+bundle.getString("EDIT")+"\" data-load-url=\""+properties.getWindowProperty(wrc.getId(),"osivia.cms.edition.modityPortletUrl")+"\" data-title=\""+bundle.getString("ADMIN_PORTLET_APP_EDIT")+"\" data-target=\"#osivia-modal\">\n"
            + "      <i class=\"glyphicons glyphicons-basic-wrench\"></i>\n"
            + "    </a>");
                            
            
            
            
            // Portlet administration display command
            Collection<ActionRendererContext> actions = wrc.getDecoration().getTriggerableActions(ActionRendererContext.MODES_KEY);
            for (ActionRendererContext action : actions) {
                if ((InternalConstants.ACTION_ADMIN.equals(action.getName())) && (action.isEnabled())) {
                    String displayAdminURL = action.getURL() + "&windowstate=maximized";
                    
                    out.print("<a href=\""+displayAdminURL+"\" class=\"btn btn-link\">\n"
                            + "        <i class=\"glyphicons glyphicons-basic-cogwheel\"></i>\n"
                            + "        <span class=\"d-md-none\">"+bundle.getString("PARAMS")+"</span>\n"
                            + "    </a>");                    
                    
                    break;
                }
            }

            // Delete
            
            out.print("<a class=\"btn btn-link\" href=\"javascript:\" title=\""+bundle.getString("DELETE")+"\" data-load-url=\""+properties.getWindowProperty(wrc.getId(),"osivia.cms.edition.deletePortletUrl")+"\" data-title=\""+bundle.getString("ADMIN_PORTLET_APP_DELETE")+"\" data-target=\"#osivia-modal\">\n"
            + "      <i class=\"glyphicons glyphicons-basic-bin\"></i>\n"
            + "    </a>");
                            
            out.print("</div>");           
            
            out.print("</div>");
            
            String windowName = properties.getWindowProperty(wrc.getId(), "osivia.cms.edition.windowName");
            out.print("<div class=\"cms-edition-draggable  clearfix\" data-src-window=\""+windowName+"\" >");

        }

        
        
        out.print("<section class=\"portlet-container " + styles + "\">");

        boolean displayCardHeader = false;
        
        if(bootstrapPanelStyle && "1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle")))
        	displayCardHeader = true;
        
        if (bootstrapPanelStyle) {
            out.print("<div class=\"card\">");
            if( displayCardHeader) {
            	out.print("<div class=\"card-header\">");
            }
        }
        
        // Div rendering

        String headerClass;
        String bodyClass;

        headerClass = "portlet-header";
        bodyClass = "portlet-content-center";

        if (WindowState.MAXIMIZED.equals(wrc.getWindowState()) || !"1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {
            headerClass += " sr-only";
        }

        // Header
        if( !Mode.ADMIN.equals(wrc.getMode()))	{
	        out.print("<div class=\"" + headerClass + "\">");
	        
	        rendererContext.render(wrc.getDecoration());
	        out.print("</div>");
        }
        
        
        if (bootstrapPanelStyle) {
        	 if( displayCardHeader) {
             	out.print("</div>");
             }
            out.print("<div class=\"card-body\">");
        }
       

        out.print("<div class=\"" + bodyClass + "\">");
        rendererContext.render(wrc.getPortlet());
        out.print("</div>");


        // Footer
        
        // Portlet container
        if (bootstrapPanelStyle) {
            out.print("</div></div>");
        }

        // Portlet container
        out.print("</section>");
        // Wizard edging

        if (showCMSTools) {
            out.print("</div>");
            
           
        }
        
        // Dyna window content
        out.print("</div>");
        



        // End of DIV for osivia.windowID or no ajax link
        out.print("</div>");
    }

    /**
     * Display CMS Tools if window is marked "CMS" (dynamic window) and if the tools are enabled in the session.
     *
     * @param wrc window context
     * @return true if CMS tools must be shown
     */
    private boolean showCMSTools(WindowRendererContext wrc, PageProperties properties ) {
        boolean showCmsTools = false;

        if (wrc instanceof WindowContext) {
            WindowContext wc = (WindowContext) wrc;
            showCmsTools = BooleanUtils.toBoolean(properties.getWindowProperty(wrc.getId(), InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY));

                
//            if (BooleanUtils.isTrue(wc.getRegionCms()) && (wrc.getProperty("osivia.windowId") != null)) {
//                showCmsTools = BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY));
//                        && !BooleanUtils.toBoolean(wrc.getProperty(InternalConstants.INHERITANCE_INDICATOR_PROPERTY));
            }
//        }

        return showCmsTools;
    }


}
