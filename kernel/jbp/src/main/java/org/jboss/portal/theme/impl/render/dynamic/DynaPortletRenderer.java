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
import org.jboss.portal.theme.render.renderer.PortletRenderer;
import org.jboss.portal.theme.render.renderer.PortletRendererContext;

import java.io.PrintWriter;

/**
 * Implementation of a drag and drop Portlet renderer.
 *
 * @author <a href="mailto:tomasz.szymanski@jboss.com">Tomasz Szymanski</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.PortletRenderer
 */
public class DynaPortletRenderer extends AbstractObjectRenderer implements PortletRenderer
{

   private PortletRenderer delegate;

   public DynaPortletRenderer(PortletRenderer portletRenderer) throws InstantiationException,
      IllegalAccessException, ClassNotFoundException
   {
      super();
      delegate = portletRenderer;
   }

   public void render(RendererContext rendererContext, PortletRendererContext prc) throws RenderException
   {
      doDND(rendererContext, prc);
   }

   private void doDND(RendererContext rendererContext, PortletRendererContext prc) throws RenderException
   {
      DynaRenderOptions options = (DynaRenderOptions)rendererContext.getAttribute(DynaConstants.RENDER_OPTIONS);

      //
      if (!DynaRenderOptions.NO_AJAX.equals(options))
      {
         if (options.isDnDEnabled() && !Boolean.TRUE.equals(DynaWindowRenderer.handleProvided.get()))
         {
            //
            DynaWindowRenderer.handleProvided.set(Boolean.TRUE);

            //
            PrintWriter out = rendererContext.getWriter();

            //
            out.print("<div class=\"dnd-handle\">");

            //
            doCatchClicks(rendererContext, prc);

            //
            out.print("</div>");
         }
         else
         {
            doCatchClicks(rendererContext, prc);
         }
      }
      else
      {
         delegate.render(rendererContext, prc);
      }
   }

   private void doCatchClicks(RendererContext rendererContext, PortletRendererContext prc) throws RenderException
   {
      PrintWriter out = rendererContext.getWriter();
      out.print("<div class=\"dyna-portlet\">");
      delegate.render(rendererContext, prc);
      out.print("</div>");
   }
}
