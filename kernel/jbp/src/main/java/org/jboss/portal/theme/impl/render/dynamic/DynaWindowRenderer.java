/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
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
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.io.PrintWriter;

/**
 * Implementation of a drag and drop WindowRenderer.
 *
 * @author <a href="mailto:tomasz.szymanski@jboss.com">Tomasz Szymanski</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DynaWindowRenderer extends AbstractObjectRenderer implements WindowRenderer
{

   //
   private WindowRenderer delegate;

   static final ThreadLocal handleProvided = new ThreadLocal();

   public DynaWindowRenderer(WindowRenderer windowRenderer) throws InstantiationException,
      IllegalAccessException, ClassNotFoundException
   {
      super();
      delegate = windowRenderer;
   }

   /** @see org.jboss.portal.theme.render.renderer.WindowRenderer#render */
   public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException
   {
      // Get render options
      DynaRenderOptions parentOptions = (DynaRenderOptions)rendererContext.getAttribute(DynaConstants.RENDER_OPTIONS);

      // It could be null if the parent renderers are not dyna renderers
      if (parentOptions == null)
      {
         parentOptions = DynaRenderOptions.NO_AJAX;
      }

      //
      try
      {
         String dndValue = wrc.getProperty(DynaRenderOptions.DND_ENABLED);
         String partialRefreshValue = wrc.getProperty(DynaRenderOptions.PARTIAL_REFRESH_ENABLED);
         DynaRenderOptions windowOptions = DynaRenderOptions.getOptions(dndValue, partialRefreshValue);

         //
         DynaRenderOptions options = DynaMergeBehavior.mergeForWindow(parentOptions, windowOptions);
         rendererContext.setAttribute(DynaConstants.RENDER_OPTIONS, options);

         //
         PrintWriter out = rendererContext.getWriter();

         //
         if (!DynaRenderOptions.NO_AJAX.equals(parentOptions))
         {
            out.print("<div class=\"dyna-window\">");
            out.print("<div id=\"");
            out.print(wrc.getId());
            if (options.isDnDEnabled())
            {
               if (options.isPartialRefreshEnabled())
               {
                  out.print("\" class=\"dnd-window partial-refresh-window\">\n");
               }
               else
               {
                  out.print("\" class=\"dnd-window\">\n");
               }
            }
            else
            {
               if (options.isPartialRefreshEnabled())
               {
                  out.print("\" class=\"partial-refresh-window\">\n");
               }
               else
               {
                  out.print("\">\n");
               }
            }

            //
            delegate.render(rendererContext, wrc);

            //
            out.print("</div></div>\n");
         }
         else
         {
            delegate.render(rendererContext, wrc);
         }

         //
         handleProvided.set(null);
      }
      finally
      {
         rendererContext.setAttribute(DynaConstants.RENDER_OPTIONS, parentOptions);
      }
   }
}
