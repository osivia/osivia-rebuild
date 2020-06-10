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
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.core.constants.InternalConstants;


public class DivRegionRenderer extends AbstractObjectRenderer implements RegionRenderer {


    /**
     * Constructor.
     */
    public DivRegionRenderer() {
        super();


    }


    /**
     * {@inheritDoc}
     */
    public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {


        // Wizard mode indicator


        PrintWriter markup = rendererContext.getWriter();
        markup.print("<div");


        markup.print(" id='");
        markup.print(rrc.getCSSId());
        markup.print("'");

        markup.print(">");


    }


    /**
     * {@inheritDoc}
     */
    public void renderBody(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {

        String regionLayoutWindowClass = rendererContext.getProperty(InternalConstants.CMS_REGION_LAYOUT_CLASS);

        PrintWriter markup = rendererContext.getWriter();


        markup.print("<div class='");
        markup.print(StringUtils.trimToEmpty(regionLayoutWindowClass));
        markup.println("'>");


        for (Iterator i = rrc.getWindows().iterator(); i.hasNext();)
        {
           WindowRendererContext wrc = (WindowRendererContext)i.next();
           rendererContext.render(wrc);
        }


        markup.println("</div>");


    }


    /**
     * {@inheritDoc}
     */
    public void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {

        PrintWriter markup = rendererContext.getWriter();

        // Add footer decorator

        markup.print("</div>");

    }


}
