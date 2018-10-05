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
package org.jboss.portal.theme.render;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.jboss.portal.theme.render.renderer.PageRenderer;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.PortletRenderer;
import org.jboss.portal.theme.render.renderer.PortletRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.jboss.portal.web.ServletContextDispatcher;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A render context to render a context on a page. <p>A render context is scoped to a MarkupContainer (a region, or a
 * window). A render context that is not scoped to a MarkupContainer is ment to be a page level template to allow easier
 * creation of the region and window render contexts via the <code>RenderContext.getContext()</code> methods.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 10337 $, $LastChangedDate: 2008-03-19 17:46:37 -0400 (Wed, 19 Mar 2008) $
 */
public abstract class RendererContext
{

   private final ThemeContext themeContext;

   /** . */
   private final RendererFactory rendererFactory;

   /** . */
   private final ServletContextDispatcher dispatcher;

   /** . */
   private final MarkupInfo markupInfo;

   /** . */
   private final ArrayList stack;

   /** . */
   private final Map attributes;

   /**
    * Create a new render context for the provided result. <p>The result contains information about the markup container
    * (page, region , window context) to render.</p>
    */
   public RendererContext(
      ThemeContext themeContext,
      RendererFactory rendererFactory,
      ServletContextDispatcher dispatcher,
      MarkupInfo markupInfo)
   {
      if (themeContext == null)
      {
         throw new IllegalArgumentException("no theme factory provided");
      }
      if (rendererFactory == null)
      {
         throw new IllegalArgumentException("no renderer factory provided");
      }
      if (dispatcher == null)
      {
         throw new IllegalArgumentException("no server invocation provided");
      }
      if (markupInfo == null)
      {
         throw new IllegalArgumentException("no stream info provided");
      }

      this.themeContext = themeContext;
      this.rendererFactory = rendererFactory;
      this.dispatcher = dispatcher;
      this.markupInfo = markupInfo;
      this.stack = new ArrayList(6);
      this.attributes = new HashMap();
   }

   public abstract PrintWriter getWriter();

   public ThemeContext getThemeContext()
   {
      return themeContext;
   }

   public ServletContextDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public MarkupInfo getMarkupInfo()
   {
      return markupInfo;
   }

   public MediaType getMediaType()
   {
      return markupInfo.getMediaType();
   }

   public String getCharset()
   {
      return markupInfo.getCharset();
   }

   public Object getAttribute(String attrName)
   {
      if (attrName == null)
      {
         throw new IllegalArgumentException("No null attribute name");
      }
      return attributes.get(attrName);
   }

   public void setAttribute(String attrName, Object attrValue)
   {
      if (attrName == null)
      {
         throw new IllegalArgumentException("No null attribute name");
      }
      if (attrValue != null)
      {
         attributes.put(attrName, attrValue);
      }
      else
      {
         attributes.remove(attrName);
      }
   }

   public String getProperty(String propertyName)
   {
      return getProperty(propertyName, stack.size() - 1);
   }

   public String getProperty(String propertyName, PropertyFetch fetch)
   {
      return getProperty(propertyName, stack.size() - (fetch.getScope() == PropertyFetch.ALL_SCOPE ? 1 : 2));
   }

   private String getProperty(String propertyName, int from)
   {
      for (int i = from; i >= 0; i--)
      {
         ObjectRendererContext ctx = (ObjectRendererContext)stack.get(i);
         String propertyValue = ctx.getProperty(propertyName);
         if (propertyValue != null)
         {
            return propertyValue;
         }
      }
      return null;
   }

//   public int getObjectRendererContextSize()
//   {
//      return stack.size();
//   }
//
//   public ObjectRendererContext getObjectRendererContext(int index)
//   {
//      return (ObjectRendererContext)stack.get(index);
//   }

   public void pushObjectRenderContext(ObjectRendererContext objectRendererContext)
   {
      stack.add(objectRendererContext);
      ObjectRenderer renderer = rendererFactory.getRenderer(this, objectRendererContext);
      renderer.startContext(this, objectRendererContext);
   }

   public ObjectRendererContext popObjectRenderContext()
   {
      ObjectRendererContext objectRendererContext = (ObjectRendererContext)stack.remove(stack.size() - 1);
      ObjectRenderer renderer = rendererFactory.getRenderer(this, objectRendererContext);
      renderer.endContext(this, objectRendererContext);
      return objectRendererContext;
   }

   public void render(ObjectRendererContext ctx) throws RenderException, IllegalStateException
   {
      pushObjectRenderContext(ctx);

      //
      try
      {
         if (ctx instanceof PageRendererContext)
         {
            PageRendererContext prc = (PageRendererContext)ctx;
            PageRenderer renderer = (PageRenderer)rendererFactory.getRenderer(this, prc);
            renderer.render(this, prc);
         }
         if (ctx instanceof WindowRendererContext)
         {
            WindowRendererContext wrc = (WindowRendererContext)ctx;
            WindowRenderer renderer = (WindowRenderer)rendererFactory.getRenderer(this, wrc);
            renderer.render(this, wrc);
         }
         else if (ctx instanceof RegionRendererContext)
         {
            RegionRendererContext rrc = (RegionRendererContext)ctx;
            RegionRenderer renderer = (RegionRenderer)rendererFactory.getRenderer(this, rrc);

            //
            renderer.renderHeader(this, rrc);
            renderer.renderBody(this, rrc);
            renderer.renderFooter(this, rrc);
         }
         else if (ctx instanceof PortletRendererContext)
         {
            PortletRendererContext prc = (PortletRendererContext)ctx;
            PortletRenderer renderer = (PortletRenderer)rendererFactory.getRenderer(this, prc);
            renderer.render(this, prc);
         }
         else if (ctx instanceof DecorationRendererContext)
         {
            DecorationRendererContext drc = (DecorationRendererContext)ctx;
            DecorationRenderer renderer = (DecorationRenderer)rendererFactory.getRenderer(this, drc);

            //
            renderer.render(this, drc);
         }
      }
      finally
      {
         popObjectRenderContext();
      }
   }
}