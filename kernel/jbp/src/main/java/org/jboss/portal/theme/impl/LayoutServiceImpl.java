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

import org.jboss.logging.Logger;
import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.ContentInfo;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.theme.LayoutException;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalRenderSet;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ServerRegistrationID;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.portal.theme.metadata.RendererSetMetaData;
import org.jboss.system.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Implementaion of the layout server. <p>The layout server is a registry of all available layouts. The server also
 * allows access to all available render sets. Render sets can be independent (named), or children of a layout.
 * Accordingly, the layout server provides accessor methods to get render set by name, or by layout. Render sets can,
 * and must, be defined for a specific media type (mime type).</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 10337 $</tt>
 * @see org.jboss.portal.theme.PortalLayout
 * @see org.jboss.portal.theme.PortalRenderSet
 * @see MediaType
 */
public class LayoutServiceImpl extends AbstractJBossService implements LayoutService, Service
{
   private static Logger log = Logger.getLogger(LayoutServiceImpl.class);

   /** Map of layout id to layout. */
   private Map layouts;

   /** Map of layout name to layout id. */
   private Map layoutNames;

   /** Map of appID + "." + layout name to layout id. */
   private Map exactLayoutNames;

   /** Map of renderSet registration id to list of rendersets. */
   private Map renderSets;

   /** Map of render set name to render set. */
   private Map renderSetNames;

   /** Map of appID + "." + render set name to render set. */
   private Map exactRenderSetNames;

   /** . */
   private PortalLayout defaultLayout;

   /** . */
   private String defaultName;

   private String defaultRenderSetName;

   public LayoutServiceImpl()
   {
      log.debug("LayoutServiceImpl instantiated.");
   }

   /** @see org.jboss.system.Service#create() */
   @PostConstruct
   protected void createService() throws Exception
   {
      log.debug("create LayoutServiceImpl ....");
      layouts = Collections.synchronizedMap(new HashMap());
      layoutNames = Collections.synchronizedMap(new HashMap());
      exactLayoutNames = Collections.synchronizedMap(new HashMap());
      renderSets = Collections.synchronizedMap(new HashMap());
      renderSetNames = Collections.synchronizedMap(new HashMap());
      exactRenderSetNames = Collections.synchronizedMap(new HashMap());
   }

   /** @see org.jboss.system.Service#destroy() */
   @PreDestroy
   protected void destroyService()
   {
      log.debug("destroy LayoutServiceImpl ....");
      layouts.clear();
      layoutNames.clear();
      exactLayoutNames.clear();
      renderSetNames.clear();
      exactRenderSetNames.clear();
   }

   /** @see org.jboss.system.Service#start() */
   protected void startService() throws Exception
   {
      log.debug("start LayoutServiceImpl ....");
   }

   /** @see org.jboss.system.Service#stop() */
   protected void stopService()
   {
      log.debug("stop LayoutServiceImpl ....");
   }

   public void addLayout(RuntimeContext runtimeContext, PortalLayoutMetaData layoutMD) throws LayoutException
   {
      // TODO: if there in no default layout, set one up.
      try
      {
         if (layoutMD == null)
         {
            throw new IllegalArgumentException("Layout metaData is null");
         }

         //
         PortalLayout layout = (PortalLayout)runtimeContext.getClassLoader().loadClass(layoutMD.getClassName()).newInstance();
         log.debug("adding layout: " + layout);

         //
         LayoutInfo info = new LayoutInfo(runtimeContext, layoutMD);
         layout.init(this, info);

         //
         if (layouts == null)
         {
            create();
         }

         layouts.put(info.getRegistrationId(), layout);
         layoutNames.put(info.getName(), info.getRegistrationId());
         exactLayoutNames.put(info.getAppId() + "." + info.getName(), info.getRegistrationId());
      }
      catch (Exception e)
      {
         throw new LayoutException(e);
      }
   }

   public void setDefaultLayoutName(String name)
   {
      log.debug("setting default: " + name);
      defaultName = name;
      defaultLayout = null;
   }

   /** @see LayoutService#getDefaultLayout() */
   
   public PortalLayout getDefaultLayout( )   {
       return getDefaultLayout( true);
   }
   
   
   public PortalLayout getDefaultLayout(boolean mustLog)
   {
      if (defaultLayout == null)
      {
         if (exactLayoutNames.keySet().contains(defaultName))
         {
            ServerRegistrationID defaultID = (ServerRegistrationID)exactLayoutNames.get(defaultName);
            defaultLayout = (PortalLayout)layouts.get(defaultID);
         }
         else if (layoutNames.keySet().contains(defaultName))
         {
            ServerRegistrationID defaultID = (ServerRegistrationID)layoutNames.get(defaultName);
            defaultLayout = (PortalLayout)layouts.get(defaultID);
         }
      }
      if (defaultLayout == null && mustLog)
      {
         log.error("Couldn't find the default layout named:" + defaultName);
      }
      return defaultLayout;
   }


   /** @see LayoutService#getLayout(org.jboss.portal.theme.ServerRegistrationID,boolean) */
   public PortalLayout getLayout(ServerRegistrationID id, boolean defaultOnNull)
   {
      log.debug("get " + id + "...");
      if (id == null)
      {
         throw new IllegalArgumentException("No null id argument allowed");
      }

      if (!layouts.keySet().contains(id) && defaultOnNull)
      {
         return getDefaultLayout();
      }
      return (PortalLayout)layouts.get(id);
   }

   /** @see LayoutService#getLayout(String,boolean) */
   public PortalLayout getLayout(String name, boolean defaultOnNull)
   {
      log.debug("get " + name + "...");

      if (exactLayoutNames.containsKey(name))
      {
         log.debug("found exact: " + name);
         return (PortalLayout)layouts.get(exactLayoutNames.get(name));
      }
      else if (layoutNames.containsKey(name))
      {
         log.debug("found " + name);
         return (PortalLayout)layouts.get(layoutNames.get(name));
      }
      else if (defaultOnNull)
      {
         log.debug("try to return default " + name);
         return getDefaultLayout();
      }

      log.debug("oops , not found " + name);
      return null;
   }

   public PortalLayout getLayoutById(String layoutIdString)
   {
      //
      PortalLayout layout;

      if (layoutIdString == null)
      {
         layout = getDefaultLayout();
      }
      else if (layoutIdString.lastIndexOf(".") > 0)
      {
         // if the id is provided in the form of context.name then look up the layout via a registration id
         ServerRegistrationID layoutID = ServerRegistrationID.createID(ServerRegistrationID.TYPE_LAYOUT, ThemeServiceImpl.parseId(layoutIdString));
         layout = getLayout(layoutID, true);
      }
      else
      {
         // otherwise use the ordinary layout name provided and lookup the layout via the name
         layout = getLayout(layoutIdString, true);
      }

      // Last Chance
      if (layout == null)
      {
         layout = getLayout("generic", true);
      }

      // We don't like that situation
      if (layout == null)
      {
         throw new IllegalStateException("NO LAYOUT FOUND FOR " + layoutIdString);
      }

      //
      return layout;
   }

   /**
    * Remove the layout identified by the provided registration id.
    *
    * @param id the id of the layout that needs to be removed
    * @throws LayoutException
    */
   public void removeLayout(ServerRegistrationID id) throws LayoutException
   {
      log.debug("removing layout with id: " + id);
      if (id == null)
      {
         throw new IllegalArgumentException("No null id argument allowed");
      }

      PortalLayout layout = (PortalLayout)layouts.get(id);

      if (layout == null)
      {
         log.debug("Not found. Nothing to remove.");
         return;
      }

      LayoutInfo info = layout.getLayoutInfo();
      final String layoutName = info.getAppId() + "." + info.getName();
      if (exactLayoutNames.keySet().contains(layoutName))
      {
         log.debug("removing layout exact " + layoutName);
         exactLayoutNames.remove(layoutName);
      }
      if (layoutNames.keySet().contains(info.getName()))
      {
         if (id.equals(layoutNames.get(info.getName())))
         {
            log.debug("removing layout " + info.getName());
            layoutNames.remove(info.getName());
         }
      }

      layouts.remove(id);
      layout.destroy();

      if (getDefaultLayout( false) != null && getDefaultLayout(false).equals(layout))
      {
         log.debug("removed default layout, need to set new one...");
         Iterator i = layouts.keySet().iterator();

         if (i.hasNext())
         {
            ServerRegistrationID defaultID = (ServerRegistrationID)i.next();
            layout = (PortalLayout)layouts.get(defaultID);
            defaultLayout = layout;
            defaultName = layout.getLayoutInfo().getName();
            log.debug("set default layout to " + layout.getLayoutInfo().getAppId() + "." + layout.getLayoutInfo().getName());
         }
         else
         {
            // We're screwed there is no more layout.
            defaultLayout = null;
            defaultName = null;
         }
      }
   }

   /** @see LayoutService#removeLayouts(String) */
   public void removeLayouts(String appID) throws LayoutException
   {
      List layoutsToDelete = new ArrayList();
      ServerRegistrationID id;

      // first get all the layouts that fit the criteria (can't remove while iterating)
      for (Iterator allLayouts = layouts.keySet().iterator(); allLayouts.hasNext();)
      {
         id = (ServerRegistrationID)allLayouts.next();
         PortalLayout layout = (PortalLayout)layouts.get(id);
         if (layout.getLayoutInfo().getAppId().equals(appID))
         {
            layoutsToDelete.add(id);
         }
      }

      // now remove them
      for (Iterator toDelete = layoutsToDelete.iterator(); toDelete.hasNext();)
      {
         removeLayout((ServerRegistrationID)toDelete.next());
      }
   }

   public void addRenderSet(RuntimeContext runtimeContext, RenderSetMetaData renderSetMD) throws LayoutException
   {
      try
      {
         for (Iterator s = renderSetMD.getRendererSet().iterator(); s.hasNext();)
         {
            RendererSetMetaData rendererSetMD = (RendererSetMetaData)s.next();
            PortalRenderSet renderSet = new PortalRenderSet(renderSetMD.getName(), runtimeContext, rendererSetMD);
            log.debug("adding : " + renderSet.getRegistrationId());
            Map sets = (Map)this.renderSets.get(renderSet.getRegistrationId());
            if (sets == null)
            {
               sets = new HashMap();
               this.renderSets.put(renderSet.getRegistrationId(), sets);
            }
            sets.put(renderSet.getMediaType(), renderSet);
            renderSetNames.put(renderSet.getName(), renderSet.getRegistrationId());
            exactRenderSetNames.put(renderSet.getAppId() + "." + renderSet.getName(), renderSet.getRegistrationId());
         }
      }
      catch (Exception e)
      {
         throw new LayoutException(e);
      }
   }

   /** @see LayoutService#getRenderSet(String,org.jboss.portal.common.util.MediaType) */
   public PortalRenderSet getRenderSet(String renderSetName, MediaType mediaType)
   {
      ServerRegistrationID id;
      id = (ServerRegistrationID)exactRenderSetNames.get(renderSetName);

      if (id == null)
      {
         id = (ServerRegistrationID)renderSetNames.get(renderSetName);
      }

      return getRenderSet(id, mediaType);
   }

   public PortalRenderSet getRenderSet(ServerRegistrationID id, MediaType mediaType)
   {
      if (id != null)
      {
         Map sets = (Map)renderSets.get(id);
         return (PortalRenderSet)sets.get(mediaType);
      }
      return null;
   }

   /** @see LayoutService#removeRenderSets(String) */
   public void removeRenderSets(String appId) throws LayoutException
   {
      log.debug("removing named render sets for : " + appId);
      List renderSetsToDelete = new ArrayList();
      for (Iterator i = renderSets.keySet().iterator(); i.hasNext();)
      {
         ServerRegistrationID id = (ServerRegistrationID)i.next();
         if (appId.equals(id.getName(0)))
         {
            renderSetsToDelete.add(id);
         }
      }

      for (Iterator i = renderSetsToDelete.iterator(); i.hasNext();)
      {
         ServerRegistrationID id = (ServerRegistrationID)i.next();
         renderSets.remove(id);
         String key = id.getName(0) + "." + id.getName(1);
         log.debug("removing render sets: " + key);
         exactRenderSetNames.remove(key);

         // make sure that the we don't delete a render set with the same name in a different appId
         if (id.equals(renderSetNames.get(id.getName(1))))
         {
            renderSetNames.remove(id.getName(1));
         }
      }
   }

   /** @see org.jboss.portal.theme.LayoutService#getLayouts() */
   public Collection getLayouts()
   {
      return Collections.unmodifiableCollection(layouts.values());
   }

   /** @see org.jboss.portal.theme.LayoutService#getRenderSets() */
   public Collection getRenderSets()
   {
      return Collections.unmodifiableCollection((renderSetNames.values()));
   }

   public void setDefaultRenderSetName(String name)
   {
      log.debug("setting default render set: " + name);
      defaultRenderSetName = name;
   }

   public String getDefaultRenderSetName()
   {
      return defaultRenderSetName;
   }

   /**
    * Get the PortalRenderSet to use for the provided layout, page and media type. <p>The render set can be defined
    * specifically for a layout, a page, or a portal. The one defined for the layout overwrites the one defined for the
    * page, which in turn overwrites the one defined for the portal. The render set is defined for a specific content
    * type (media type), which will be determined from the provided HttpStreamInfo.</p>
    */
   public PortalRenderSet getRenderSet(LayoutInfo info, ContentInfo streamInfo, String renderSetName)
   {
      if (renderSetName == null)
      {
         renderSetName = getDefaultRenderSetName();
      }
      if (info == null || renderSetName == null || streamInfo == null)
      {
         throw new IllegalArgumentException("No null arguments allowed [" + info + "] [" + renderSetName + "] [" + streamInfo + "]");
      }
      MediaType mediaType = streamInfo.getMediaType();
      return getRenderSet(renderSetName, mediaType);
   }
}
