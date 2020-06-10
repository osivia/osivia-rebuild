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

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.page.PageProperties;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {


    /**
     * Constructor.
     */
    public DivWindowRenderer() {
        super();

    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {


        PrintWriter out = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();
        // Set current window identifier for decorators
        properties.setCurrentWindowId(wrc.getId());


        // Window identifier
        String windowId = wrc.getProperty("osivia.windowId");


        // Styles
        String styles = properties.getWindowProperty(wrc.getId(), "osivia.style");
        if (styles != null) {
            styles = styles.replaceAll(",", " ");
        } else {
            styles = StringUtils.EMPTY;
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


        out.print("<section class=\"portlet-container " + styles + "\">");


        // Div rendering

        String headerClass;
        String bodyClass;

        headerClass = "portlet-header";
        bodyClass = "portlet-content-center";

        if (WindowState.MAXIMIZED.equals(wrc.getWindowState()) || !"1".equals(properties.getWindowProperty(wrc.getId(), "osivia.displayTitle"))) {
            headerClass += " sr-only";
        }

        // Header
        out.print("<div class=\"" + headerClass + "\">");
        rendererContext.render(wrc.getDecoration());
        out.print("</div>");

        out.print("<div class=\"" + bodyClass + "\">");
        rendererContext.render(wrc.getPortlet());
        out.print("</div>");


        // Footer


        // Portlet container
        out.print("</section>");
        // Wizard edging

        // Dyna window content
        out.print("</div>");


        // End of DIV for osivia.windowID or no ajax link
        out.print("</div>");
    }


}
