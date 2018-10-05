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
package org.jboss.portal.theme.metadata;

import org.jboss.portal.common.net.media.MediaType;

/**
 * Meta data describing a render set. <p/> exmaple of a render set descriptor: <p/> <portal-renderSet> <renderSet
 * name="divRenderer"> <ajax enabled="true"/> <set content-type="text/html"> <region-renderer>org.jboss.portal.theme.impl.render.div.DivRegionRenderer</region-renderer>
 * <window-renderer>org.jboss.portal.theme.impl.render.div.DivWindowRenderer</window-renderer>
 * <portlet-renderer>org.jboss.portal.theme.impl.render.div.DivPortletRenderer</portlet-renderer>
 * <decoration-renderer>org.jboss.portal.theme.impl.render.div.DivDecorationRenderer</decoration-renderer> </set>
 * </renderSet> <renderSet name="emptyRenderer"> <ajax enabled="true"/> <set content-type="text/html">
 * <region-renderer>org.jboss.portal.theme.impl.render.empty.EmptyRegionRenderer</region-renderer>
 * <window-renderer>org.jboss.portal.theme.impl.render.empty.EmptyWindowRenderer</window-renderer>
 * <portlet-renderer>org.jboss.portal.theme.impl.render.empty.EmptyPortletRenderer</portlet-renderer>
 * <decoration-renderer>org.jboss.portal.theme.impl.render.empty.EmptyDecorationRenderer</decoration-renderer> </set>
 * </renderSet> </portal-renderSet> <p/> </p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 10337 $
 */
public class RendererSetMetaData
{
   private MediaType mediaType;
   private String regionRenderer;
   private String windowRenderer;
   private String decorationRenderer;
   private String portletRenderer;
   private boolean ajaxEnabled;

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   public String getRegionRenderer()
   {
      return regionRenderer;
   }

   public void setRegionRenderer(String regionRenderer)
   {
      this.regionRenderer = regionRenderer;
   }

   public String getWindowRenderer()
   {
      return windowRenderer;
   }

   public void setWindowRenderer(String windowRenderer)
   {
      this.windowRenderer = windowRenderer;
   }

   public String getDecorationRenderer()
   {
      return decorationRenderer;
   }

   public void setDecorationRenderer(String decorationRenderer)
   {
      this.decorationRenderer = decorationRenderer;
   }

   public String getPortletRenderer()
   {
      return portletRenderer;
   }

   public void setPortletRenderer(String portletRenderer)
   {
      this.portletRenderer = portletRenderer;
   }

   public boolean isAjaxEnabled()
   {
      return ajaxEnabled;
   }

   public void setAjaxEnabled(boolean ajaxEnabled)
   {
      this.ajaxEnabled = ajaxEnabled;
   }
}
