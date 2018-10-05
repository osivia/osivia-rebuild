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
package org.jboss.portal.theme;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.theme.impl.render.dynamic.DynaDecorationRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaPortletRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaRegionRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaWindowRenderer;
import org.jboss.portal.theme.metadata.RendererSetMetaData;
import org.jboss.portal.theme.render.ObjectRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.PortletRenderer;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.WindowRenderer;

/**
 * The portal render set is a set of renderer implementations for the markup container of a page. <p>A render set
 * consists of 4 iterface implementations with some meta data describing the web application the render set is coming
 * from, and the content type it is capable of handling.</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 10337 $</tt>
 */
public final class PortalRenderSet
{

   private final RegionRenderer regionRenderer;
   private final WindowRenderer windowRenderer;
   private final PortletRenderer portletRenderer;
   private final DecorationRenderer decorationRenderer;
   private final MediaType mediaType;
   private final String appID;
   private final String name;
   private final ServerRegistrationID registrationId;
   private final boolean ajaxEnabled;

   /**
    * construct a render set
    *
    * @param renderSetName  the name of the render set as defined in the portal-renderSet.xml descriptor, or null if it
    *                       was defined as part of a layout descriptor.
    * @param runtimeContext the runtime environment (data describing the portal web application in wich the render set
    *                       was defined)
    * @param rendererSetMD  meta data about an individual set of 4 renderer implementation together with their supported
    *                       content type
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws ClassNotFoundException
    */
   public PortalRenderSet(String renderSetName, RuntimeContext runtimeContext, RendererSetMetaData rendererSetMD) throws IllegalAccessException, InstantiationException, ClassNotFoundException
   {
      // there CAN be render sets without a name !

      if (runtimeContext == null)
      {
         throw new IllegalArgumentException("no valid container");
      }
      if (rendererSetMD == null)
      {
         throw new IllegalArgumentException("no valid set");
      }
      this.name = renderSetName;
      this.appID = runtimeContext.getAppId();
      this.registrationId = ServerRegistrationID.createID(ServerRegistrationID.TYPE_RENDERSET, new String[]{appID, name});
      this.mediaType = rendererSetMD.getMediaType();
      this.ajaxEnabled = rendererSetMD.isAjaxEnabled();

      if (ajaxEnabled) // wrap the current renderer with the ajax delegator one.
      {
         this.regionRenderer = new DynaRegionRenderer((RegionRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getRegionRenderer()));
         this.windowRenderer = new DynaWindowRenderer((WindowRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getWindowRenderer()));
         this.portletRenderer = new DynaPortletRenderer((PortletRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getPortletRenderer()));
         this.decorationRenderer = new DynaDecorationRenderer((DecorationRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getDecorationRenderer()));
      }
      else
      {
         this.regionRenderer = (RegionRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getRegionRenderer());
         this.windowRenderer = (WindowRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getWindowRenderer());
         this.portletRenderer = (PortletRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getPortletRenderer());
         this.decorationRenderer = (DecorationRenderer)loadRenderer(runtimeContext.getClassLoader(), rendererSetMD.getDecorationRenderer());
      }
   }

   public ServerRegistrationID getRegistrationId()
   {
      return registrationId;
   }

   /** @see PortalRenderSet#getMediaType */
   public MediaType getMediaType()
   {
      return mediaType;
   }

   /**
    * Get the name of the application (the WAR) that contains this render set.
    *
    * @return the name of the application (the WAR) that contains this render set
    */
   public String getAppId()
   {
      return appID;
   }

   /**
    * Get the name of this render set.
    *
    * @return the name of this render set
    */
   public String getName()
   {
      return name;
   }

   /** @see PortalRenderSet#getRegionRenderer */
   public RegionRenderer getRegionRenderer()
   {
      return regionRenderer;
   }

   /** @see PortalRenderSet#getWindowRenderer */
   public WindowRenderer getWindowRenderer()
   {
      return windowRenderer;
   }

   /** @see PortalRenderSet#getPortletRenderer */
   public PortletRenderer getPortletRenderer()
   {
      return portletRenderer;
   }

   /** @see PortalRenderSet#getDecorationRenderer */
   public DecorationRenderer getDecorationRenderer()
   {
      return decorationRenderer;
   }

   public boolean isAjaxEnabled()
   {
      return ajaxEnabled;
   }

   /** @see java.lang.Object#toString */
   public String toString()
   {
      return "PortalRenderSet: " + getAppId() + "." + (getName() == null ? "<from layout>" : getName()) + ": " +
         getMediaType() + " [" + getRegionRenderer() + "][" + getWindowRenderer() + "][" + getDecorationRenderer() +
         "][" + getPortletRenderer() + "]";
   }

   private ObjectRenderer loadRenderer(ClassLoader loader, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
   {
      return (ObjectRenderer)loader.loadClass(className).newInstance();
   }
}
