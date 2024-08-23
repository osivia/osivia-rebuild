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

import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.portal.theme.metadata.ThemeLinkMetaData;
import org.jboss.portal.theme.metadata.ThemeScriptMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $Revision: 8784 $
 */
public final class ThemeInfo
{

   /** . */
   private final RuntimeContext ctx;

   /** . */
   private final PortalThemeMetaData meta;

   /** . */
   private final ServerRegistrationID registrationId;

   /** . */
   private final List scripts;

   /** . */
   private final List links;
   
   /** . */
   private final List styles;

   /** . */
   private final List elements;

   public ThemeInfo(RuntimeContext ctx, PortalThemeMetaData meta)
   {
      this.ctx = ctx;
      this.meta = meta;
      this.registrationId = ServerRegistrationID.createID(ServerRegistrationID.TYPE_THEME, new String[]{ctx.getAppId(), meta.getName()});

      // Initialise elements
      scripts = new ArrayList(meta.getScripts());
      links = new ArrayList(meta.getLinks());
      styles = new ArrayList(meta.getStyles());
      for (int i = 0; i < scripts.size(); i++)
      {
         ThemeScriptMetaData scriptMD = (ThemeScriptMetaData)scripts.get(i);
         ThemeScript script = new ThemeScript(ctx.getContextPath(), scriptMD.getSrc(), scriptMD.getId(), scriptMD.getType(), scriptMD.getBodyContent(), scriptMD.getCharset());
         scripts.set(i, script);
      }
      for (int i = 0; i < links.size(); i++)
      {
         ThemeLinkMetaData linkMD = (ThemeLinkMetaData)links.get(i);
         ThemeLink link = new ThemeLink(ctx.getContextPath(), linkMD.getRel(), linkMD.getType(), linkMD.getHref(), linkMD.getId(), linkMD.getTitle(), linkMD.getMedia());
         links.set(i, link);
      }
      elements = new ArrayList(scripts.size() + links.size());
      elements.addAll(scripts);
      elements.addAll(links);
   }

   /**
    * Get the context path of the servlet context in which the theme is contained.
    *
    * @return the context path of the WAR that contains this theme
    */
   public String getContextPath()
   {
      return ctx.getContextPath();
   }

   /**
    * Get the application name of the WAR that contains this theme
    *
    * @return the application name of the WAR that contains this theme
    */
   public String getAppId()
   {
      return ctx.getAppId();
   }

   /**
    * Get the name of this theme.
    *
    * @return the name of this theme
    */
   public String getName()
   {
      return meta.getName();
   }

   /**
    * Get all script elements that are defined as part of this layout
    *
    * @return a <code>java.util.List</code> of script elements that are defined as part of this theme
    */
   public List getScripts()
   {
      return scripts;
   }

   /**
    * Get all link elements that are defined as part of this theme
    *
    * @return a <code>java.util.List</code> of link elements that are defined as part of this theme
    */
   public List getLinks()
   {
      return links;
   }

   public List getStyles()
   {
      return styles;
   }
   /**
    * Get all elements of this theme. <p>Elements of a theme are all the child nodes in the HEAD tag that are part of
    * this theme. A theme can currently have script and link tags
    *
    * @return a <code>java.util.List</code> of all elements of the theme
    */
   public List getElements()
   {
      return elements;
   }

   /** @see java.lang.Object#toString */
   public String toString()
   {
      return meta.getName();
   }

   public ServerRegistrationID getRegistrationId()
   {
      return registrationId;
   }
}
