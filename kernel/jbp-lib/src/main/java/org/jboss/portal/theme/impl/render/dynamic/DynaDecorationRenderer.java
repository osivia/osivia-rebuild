/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.jboss.portal.theme.impl.render.dynamic;

import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;

import java.io.PrintWriter;

/**
 * Implementation of a drag and drop decoration renderer.
 *
 * @author <a href="mailto:tomasz.szymanski@jboss.com">Tomasz Szymanski</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $LastChangedRevision: 13018 $, $LastChangedDate: 2009-03-11 06:57:12 -0400 (Wed, 11 Mar 2009) $
 * @see org.jboss.portal.theme.render.renderer.DecorationRenderer
 */
public class DynaDecorationRenderer extends AbstractObjectRenderer implements DecorationRenderer
{

   /** . */
   private DecorationRenderer delegate;

   public DynaDecorationRenderer(DecorationRenderer decorationRenderer)
   {
      super();
      delegate = decorationRenderer;
   }

   public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException
   {
      DynaRenderOptions options = (DynaRenderOptions)rendererContext.getAttribute(DynaConstants.RENDER_OPTIONS);

      //
      PrintWriter markup = rendererContext.getWriter();

      //
      if (!DynaRenderOptions.NO_AJAX.equals(options))
      {
         if (options.isDnDEnabled())
         {
            DynaWindowRenderer.handleProvided.set(Boolean.TRUE);

            //
            markup.print("<div class=\"dnd-handle\">");
            markup.print("<div class=\"dyna-decoration\">\n");

            delegate.render(rendererContext, drc);

            // Close dnd-decoration
            markup.print("</div>");
            // Close dnd-handle
            markup.print("</div>");

         }
         else
         {
            markup.print("<div class=\"dyna-decoration\">\n");

            delegate.render(rendererContext, drc);

            // Close dyna-decoration
            markup.print("</div>");
         }
      }
      else
      {
         delegate.render(rendererContext, drc);
      }
   }
}