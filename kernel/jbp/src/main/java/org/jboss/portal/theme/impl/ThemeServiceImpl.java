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
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ServerRegistrationID;
import org.jboss.portal.theme.ThemeException;
import org.jboss.portal.theme.ThemeInfo;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.system.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * TODO: A description of this class.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 8784 $</tt>
 */
public class ThemeServiceImpl extends AbstractJBossService implements ThemeService, Service
{

   private static Logger log = Logger.getLogger(ThemeServiceImpl.class);

   private Map themes;
   private Map themeNames;
   private Map exactThemeNames;
   private ServerRegistrationID defaultID;

   private PortalTheme defaultTheme;
   private String defaultThemeName;

   public ThemeServiceImpl()
   {
      log.debug("ThemeServiceImpl instantiated.");
   }

   /** @throws Exception  */
   protected void createService() throws Exception
   {
      log.debug("create ThemeServiceImpl ....");
      themes = Collections.synchronizedMap(new HashMap());
      themeNames = Collections.synchronizedMap(new HashMap());
      exactThemeNames = Collections.synchronizedMap(new HashMap());
   }

   protected void destroyService()
   {
      log.debug("destroy ThemeServiceImpl ....");
      themes.clear();
      themeNames.clear();
      exactThemeNames.clear();
   }

   protected void startService() throws Exception
   {
      log.debug("start ThemeServiceImpl ....");
   }

   protected void stopService()
   {
      log.debug("stop ThemeServiceImpl ....");
   }

   public void addTheme(RuntimeContext runtimeContext, PortalThemeMetaData metaData) throws ThemeException
   {
      try
      {
         log.debug("add theme: " + metaData);

         PortalTheme theme = (PortalTheme)runtimeContext.getClassLoader().loadClass(metaData.getClassName()).newInstance();

         // Make sure the hash maps are initialized
         if (themes == null)
         {
            try
            {
               create();
            }
            catch (Exception e)
            {
               throw new ThemeException(e);
            }
         }

         ThemeInfo info = new ThemeInfo(runtimeContext, metaData);
         theme.init(this, info);

         if (ServerRegistrationID.TYPE_THEME.equals(info.getRegistrationId().getType()))
         {

            themeNames.put(metaData.getName(), info.getRegistrationId());
            themes.put(info.getRegistrationId(), theme);

            String key = info.getAppId() + "." + metaData.getName();
            exactThemeNames.put(key, info.getRegistrationId());
         }
         else
         {
            throw new ThemeException("wrong meta data type: " + info.getRegistrationId().getType());
         }
      }
      catch (Exception e)
      {
         throw new ThemeException(e);
      }
   }

   /** @see ThemeService#removeTheme(org.jboss.portal.theme.PortalTheme) */
   public void removeTheme(PortalTheme theme)
   {
      ServerRegistrationID themeId = theme.getThemeInfo().getRegistrationId();
      if (themes.containsKey(themeId))
      {
         ThemeInfo info = theme.getThemeInfo();
         theme.destroy();

         log.debug("remove theme " + themeId + ": " + info.getName());

         final String themeName = info.getAppId() + "." + info.getName();
         if (exactThemeNames.keySet().contains(themeName))
         {
            log.debug("removing theme exact " + themeName);
            exactThemeNames.remove(themeName);
         }
         if (themeNames.keySet().contains(info.getName()))
         {
            log.debug("removing theme " + info.getName());
            themeNames.remove(info.getName());
         }
         themes.remove(themeId);

         // if the default was removed, set a random one
         if (defaultID != null && defaultID.equals(themeId))
         {
            log.debug("removed default theme; need to set a new default...");
            Iterator keySet = themes.keySet().iterator();
            if (keySet.hasNext())
            {
               defaultID = (ServerRegistrationID)keySet.next();
               log.debug("set new default to " + defaultID);
            }
            else
            {
               defaultID = null;
            }
         }
      }
   }

   /** @see ThemeService#removeThemes(String) */
   public void removeThemes(String appId)
   {
      List themesToDelete = new ArrayList();

      // first get all the themes that fit the criteria (can't remove while iterating)
      for (Iterator allThemes = themes.keySet().iterator(); allThemes.hasNext();)
      {
         PortalTheme theme = (PortalTheme)themes.get(allThemes.next());
         if (theme.getThemeInfo().getAppId().equals(appId))
         {
            themesToDelete.add(theme);
         }
      }

      // now remove them
      for (Iterator toDelete = themesToDelete.iterator(); toDelete.hasNext();)
      {
         PortalTheme t = (PortalTheme)toDelete.next();
         removeTheme(t);
      }
   }

   /** @see ThemeService#setDefault(org.jboss.portal.theme.ServerRegistrationID) */
   public void setDefault(ServerRegistrationID themeId) throws ThemeException
   {
      log.debug("set default theme " + themeId);
      if (themes.keySet().contains(themeId))
      {
         defaultID = themeId;
         log.debug("set new default theme to " + defaultID);
         return;
      }
      throw new ThemeException("Theme with name [" + themeId + "] does not exist");
   }

   public void setDefaultThemeName(String defaultName) throws ThemeException
   {
      log.debug("setting default: " + defaultName);
      defaultThemeName = defaultName;
      defaultTheme = null;
   }

   public PortalTheme getDefaultTheme()
   {
      if (defaultTheme == null)
      {
         if (exactThemeNames.keySet().contains(defaultThemeName))
         {
            ServerRegistrationID defaultID = (ServerRegistrationID)exactThemeNames.get(defaultThemeName);
            defaultTheme = (PortalTheme)themes.get(defaultID);
         }
         else if (themeNames.keySet().contains(defaultThemeName))
         {
            ServerRegistrationID defaultID = (ServerRegistrationID)themeNames.get(defaultThemeName);
            defaultTheme = (PortalTheme)themes.get(defaultID);
         }
      }
      if (defaultTheme == null)
      {
         log.error("Couldn't find the default layout named:" + defaultThemeName);
      }
      return defaultTheme;
   }

   /**
    * Set the default theme.
    *
    * @param name the name of the theme that is to be set as default
    * @throws ThemeException if the theme is not part of the available themes
    */
   public void setDefaultFromName(String name) throws ThemeException
   {
      log.debug("set default theme " + name);
      if (exactThemeNames.keySet().contains(name))
      {
         defaultID = (ServerRegistrationID)exactThemeNames.get(name);
         return;
      }
      if (themeNames.keySet().contains(name))
      {
         defaultID = (ServerRegistrationID)themeNames.get(name);
         return;
      }
      throw new ThemeException("Theme with name [" + name + "] does not exist");
   }

   /** @see ThemeService#getTheme(org.jboss.portal.theme.ServerRegistrationID,boolean) */
   public PortalTheme getTheme(ServerRegistrationID themeId, boolean defaultOnNull)
   {
      log.debug("get theme " + themeId);
      if (themeId == null)
      {
         throw new IllegalArgumentException("Theme ID must not be null");
      }

      if (!themes.keySet().contains(themeId) && defaultOnNull && defaultID != null)
      {
         return (PortalTheme)themes.get(defaultID);
      }

      return (PortalTheme)themes.get(themeId);
   }

   /** @see ThemeService#getTheme(String,boolean) */
   public PortalTheme getTheme(String name, boolean defaultOnNull)
   {
      log.debug("get theme " + name);
      if (name == null || "".equals(name))
      {
         throw new IllegalArgumentException("Name cannot be null or empty");
      }

      if (exactThemeNames.keySet().contains(name))
      {
         log.debug("found theme exact " + name);
         return (PortalTheme)themes.get(exactThemeNames.get(name));
      }
      if (themeNames.keySet().contains(name))
      {
         log.debug("found theme " + name);
         return (PortalTheme)themes.get(themeNames.get(name));
      }
      else if (defaultOnNull && defaultID != null)
      {
         log.debug("returning default theme " + defaultID);
         return (PortalTheme)themes.get(defaultID);
      }

      log.warn("Theme with name [" + name + "] not found");

      return null;
   }

   public PortalTheme getThemeById(String themeIdString)
   {
      PortalTheme theme;

      // If the id is provided in the form of context.name then look up the theme via a registration id
      if (themeIdString == null)
      {
         theme = getDefaultTheme();
      }
      else if (themeIdString.lastIndexOf(".") > 0)
      {
         ServerRegistrationID themeId = ServerRegistrationID.createID(ServerRegistrationID.TYPE_THEME, parseId(themeIdString));
         theme = getTheme(themeId, true);
      }
      else
      {
         // Otherwise use the ordinary theme name provided and lookup the theme via the name
         theme = getTheme(themeIdString, true);
      }

      // Last Chance
      if (theme == null)
      {
         theme = getTheme("renaissance", true);
      }

      // We don't like that situation
      if (theme == null)
      {
         throw new IllegalStateException("No Theme found for " + themeIdString);
      }

      //
      return theme;
   }

   /** @see org.jboss.portal.theme.ThemeService#getThemes() */
   public Collection getThemes()
   {
      return Collections.unmodifiableCollection(themes.values());
   }

   /** @see org.jboss.portal.theme.ThemeService#getThemeNames() */
   public Collection getThemeNames()
   {
      return Collections.unmodifiableCollection(themeNames.keySet());
   }

   /**
    * parse the provided String for '.' as a separator. For each token, add an entry to a String[] that will be returned
    * as the result
    *
    * @param layoutIDString the string to be examined
    * @return an array of Strings
    */
   public static String[] parseId(String layoutIDString)
   {
      List names = new ArrayList();
      StringTokenizer tokens = new StringTokenizer(layoutIDString, ".");
      if (tokens.countTokens() > 1)
      {
         while (tokens.hasMoreElements())
         {
            names.add(tokens.nextToken());
         }
      }
      else
      {
         names.add(layoutIDString);
      }

      String[] id = new String[names.size()];
      names.toArray(id);
      return id;
   }
}
