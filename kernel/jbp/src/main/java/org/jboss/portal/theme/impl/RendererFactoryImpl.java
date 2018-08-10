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

import org.jboss.portal.common.util.ContentInfo;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutServiceInfo;
import org.jboss.portal.theme.PortalRenderSet;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.render.ObjectRenderer;
import org.jboss.portal.theme.render.ObjectRendererContext;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.RendererFactory;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.jboss.portal.theme.render.renderer.PageRenderer;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.PortletRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10337 $
 */
public class RendererFactoryImpl implements RendererFactory
{

   /** . */
   private PageRenderer pageRenderer;

   /** . */
   private LayoutServiceInfo layoutServiceInfo;

   /** . */
   private LayoutInfo layoutInfo;

   public RendererFactoryImpl(
      PageRenderer pageRenderer,
      LayoutServiceInfo layoutServiceInfo,
      LayoutInfo layoutInfo)
   {
      this.pageRenderer = pageRenderer;
      this.layoutServiceInfo = layoutServiceInfo;
      this.layoutInfo = layoutInfo;
   }

   public ObjectRenderer getRenderer(RendererContext rendererContext, ObjectRendererContext objectRendererContext)
   {
      if (objectRendererContext instanceof PageRendererContext)
      {
         return pageRenderer;
      }
      if (objectRendererContext instanceof RegionRendererContext)
      {
         return getRegionRenderer(rendererContext, (RegionRendererContext)objectRendererContext);
      }
      else if (objectRendererContext instanceof WindowRendererContext)
      {
         return getWindowRenderer(rendererContext, (WindowRendererContext)objectRendererContext);
      }
      else if (objectRendererContext instanceof PortletRendererContext)
      {
         return getPortletRenderer(rendererContext, (PortletRendererContext)objectRendererContext);
      }
      else if (objectRendererContext instanceof DecorationRendererContext)
      {
         return getDecorationRenderer(rendererContext, (DecorationRendererContext)objectRendererContext);
      }
      return null;
   }

   private ObjectRenderer getRegionRenderer(RendererContext rendererContext, RegionRendererContext objectRenderContext)
   {
      return getRenderSet(rendererContext).getRegionRenderer();
   }

   /**
    * Get the window renderer for the provided window. <p>The window result can optionally contain a window property
    * that points to a render set to use when getting the window renderer. If no window property was provided, then the
    * default render set of this context will be used.</p>
    *
    * @param result the window result possibly containing the window property that specifies an alternative render set
    *               name for the window renderer to use
    * @return a window renderer
    */
   public ObjectRenderer getWindowRenderer(RendererContext rendererContext, WindowRendererContext result)
   {
      String renderSetName = null;
      if (result != null)
      {
         renderSetName = result.getProperty(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER);
      }

      //
      if (result != null && renderSetName == null)
      {
         renderSetName = rendererContext.getProperty(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER);
      }

      //
      if (renderSetName != null)
      {
         PortalRenderSet renderSet = layoutServiceInfo.getRenderSet(renderSetName, rendererContext.getMediaType());

         //
         if (renderSet != null)
         {
            return renderSet.getWindowRenderer();
         }
      }

      //
      return getRenderSet(rendererContext).getWindowRenderer();
   }

   /**
    * Get the decoration renderer for the window that the provided window result is associated with. <p>If the window
    * result contains a window property that points to a render set, that render set will be used to determine the
    * decoration renderer. Otherwise, the render set that was determined for this context will be used.</p>
    *
    * @param result the window result possibly containing the window property that specifies an alternative render set
    *               name for the decoration renderer to use
    * @return a decoration renderer
    */
   public ObjectRenderer getDecorationRenderer(RendererContext rendererContext, DecorationRendererContext result)
   {
      String renderSetName = null;

      if (result != null)
      {
         renderSetName = result.getProperty(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER);
      }

      //
      if (result != null && renderSetName == null)
      {
         renderSetName = rendererContext.getProperty(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER);
      }

      //
      if (renderSetName != null)
      {
         PortalRenderSet renderSet = layoutServiceInfo.getRenderSet(renderSetName, rendererContext.getMediaType());

         //
         if (renderSet != null)
         {
            return renderSet.getDecorationRenderer();
         }
      }

      //
      return getRenderSet(rendererContext).getDecorationRenderer();
   }

   /**
    * Get the portlet renderer for the render set defined in the window properties that were provided. <p>If the
    * provided window result doesn't contain any window property that defines a render set name, the default render set
    * that was determined for this context will be used to get the portlet renderer.</p>
    *
    * @param result the window result containing the window properties to introspect for a render set name to get the
    *               portlet renderer from
    * @return a portlet renderer
    */
   public ObjectRenderer getPortletRenderer(RendererContext rendererContext, PortletRendererContext result)
   {
      String renderSetName = null;

      //
      if (result != null)
      {
         renderSetName = result.getProperty(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER);
      }

      //
      if (result != null && renderSetName == null)
      {
         renderSetName = rendererContext.getProperty(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER);
      }

      //
      if (renderSetName != null)
      {
         PortalRenderSet renderSet = layoutServiceInfo.getRenderSet(renderSetName, rendererContext.getMediaType());

         //
         if (renderSet != null)
         {
            return renderSet.getPortletRenderer();
         }
      }

      //
      return getRenderSet(rendererContext).getPortletRenderer();
   }

   private PortalRenderSet getRenderSet(RendererContext rendererContext)
   {
      // Find the render set name
      String renderSetName = rendererContext.getProperty(ThemeConstants.PORTAL_PROP_RENDERSET);

//      if (renderSetName == null)
//      {
//         throw new IllegalArgumentException("No RenderSet determined");
//      }

      ContentInfo contentInfo = rendererContext.getMarkupInfo();

      return layoutServiceInfo.getRenderSet(layoutInfo, contentInfo, renderSetName);
//      if (renderSet == null)
//      {
//         throw new IllegalArgumentException("No RenderSet determined for " + renderSetName);
//      }
   }
}
