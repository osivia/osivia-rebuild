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

package org.jboss.portal.theme.impl;

import org.jboss.portal.WindowState;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.theme.LayoutDispatcher;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.ObjectRendererContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.RendererFactory;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.theme.render.renderer.PageRenderer;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.web.ServletContextDispatcher;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * Layout implementation that uses JSPs (and tags) to render the response back to the client.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 * @see LayoutDispatcher
 */
public final class JSPLayout extends PortalLayout implements PageRenderer
{

   public RendererContext getRenderContext(ThemeContext themeContext, MarkupInfo markupInfo, ServletContextDispatcher dispatcher)
   {
      RendererFactory factory = new RendererFactoryImpl(this, serviceInfo, info);

      //
      return new JSPRendererContext(themeContext, factory, dispatcher, markupInfo);
   }

   public RendererContext getRenderContext(ThemeContext themeContext, MarkupInfo markupInfo, ServletContextDispatcher dispatcher, Writer writer)
   {
      RendererFactory factory = new RendererFactoryImpl(this, serviceInfo, info);

      //
      return new WriterRendererContext(themeContext, factory, dispatcher, markupInfo, new PrintWriter(writer));
   }

   public void startContext(RendererContext rendererContext, ObjectRendererContext objectRenderContext)
   {
   }

   public void endContext(RendererContext rendererContext, ObjectRendererContext objectRenderContext)
   {
   }

   public void render(RendererContext rendererContext, PageRendererContext prc) throws RenderException
   {
      PageResult pageResult = (PageResult)prc;

      // Take care of maximized window
      // if one window is maximized then we do chose the maximized layout for the rendition
      // and put that window on the maximized region of this layout
      for (Iterator i = pageResult.getWindowIds().iterator(); i.hasNext();)
      {
         String windowId = (String)i.next();
         WindowContext wc = pageResult.getWindowContext(windowId);
         WindowResult res = wc.getResult();
         if (WindowState.MAXIMIZED.equals(res.getWindowState()))
         {
//            // TODO: Deep clone instead ?
//            PageResult newRes = new PageResult(
//               pageResult.getPageName(),
//               pageResult.getPageProperties(),
//               pageResult.getPortalProperties());
//
//            //
//            newRes.setLayoutState("maximized");
//            newRes.setLayoutURI(getLayoutInfo().getURI("maximized"));
//            newRes.setThemeResult(pageResult.getThemeResult());
//
//            //
//            for (Iterator j = pageResult.getWindowContextMap().values().iterator();j.hasNext();)
//            {
//               WindowContext windowContext = (WindowContext)j.next();
//
//               //
//               if (windowContext == wc)
//               {
//                  newRes.addWindowContext(new WindowContext(wc.getName(), wc.getId(), "maximized", 0, res));
//               }
//               else
//               {
//                  newRes.addWindowContext(windowContext);
//               }
//            }
//
//            //
//            pageResult = newRes;

            // Get src region
            RegionRendererContext srcRegion = pageResult.getRegion(wc.getRegionName());
            Map srcProps = srcRegion.getProperties();

            //
            pageResult.setLayoutState("maximized");
            wc.setRegionName("maximized");
            wc.setOrder("0");

            // Yes it is ugly
            pageResult.rebuild();

            // New destination region
            RegionRendererContext dstRegion = pageResult.getRegion("maximized");
            Map dstProps = dstRegion.getProperties();

            // Copy properties
            dstProps.putAll(srcProps);

            // Disable DnD
            DynaRenderOptions options = DynaRenderOptions.getOptions(Boolean.FALSE, null);
            options.setOptions(dstProps);

            //
            break;
         }
      }

      try
      {
         String layoutState = pageResult.getLayoutState();
         String layoutURI = getLayoutInfo().getURI(layoutState);
         LayoutDispatcher dispatcher = new LayoutDispatcher(rendererContext, prc, layoutURI, getLayoutInfo());
         dispatcher.include();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ServletException e)
      {
         e.printStackTrace();
      }
   }
}
