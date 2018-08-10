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
package org.jboss.portal.theme.page;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The window result represent the generated content of a window. It contains several kind of data : <li> <ul>the window
 * title</ul> <ul>the window content</ul> <ul>a map of actions available for the window</ul> </li>
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11754 $
 */
public class WindowResult
{

   /** . */
   private String title;

   /** . */
   private String content;

   /** . */
   private Map actions;

   /** . */
   private final List<Element> headerContent;

   /** . */
   private Map properties;

   /** . */
   private final WindowState windowState;

   /** . */
   private final Mode mode;

   /**
    * Create a new WindowResult with the information about the renderered portlet. <p>A window result contains the the
    * rendered markup fragment of the portlet, the title, a map of action urls to trigger mode and state changes, and
    * the properties that were set as portlet response properties.</p>
    *
    * @param title            the title to be displayed for this portlet in the rendered page
    * @param content          the rendered markup fragment of the portlet
    * @param actions          a map of actions possible for this portlet
    * @param windowProperties the properties for this window
    * @param headerChars      content that needs to be injected into the header
    * @see WindowResult.Action
    */
   public WindowResult(
      String title,
      String content,
      Map actions,
      Map windowProperties,
      List<Element> headerContent,
      WindowState windowState,
      Mode mode)
   {
      this.title = title;
      this.content = content;
      this.actions = actions;
      this.headerContent = headerContent;
      this.properties = windowProperties;
      this.windowState = windowState;
      this.mode = mode;
   }

   public String getTitle()
   {
      return title;
   }

   public String getContent()
   {
      return content;
   }

   public List<Element> getHeaderContent()
   {
      return headerContent;
   }

   public Mode getMode()
   {
      return mode;
   }

   public WindowState getWindowState()
   {
      return windowState;
   }

   public Collection getTriggerableActions(String familyName)
   {
      return (Collection)actions.get(familyName);
   }

   public Map getProperties()
   {
      return properties;
   }

   /**
    * Represents an action that can be triggered.
    * <p/>
    * todo : add more stuff
    *
    * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
    */
   public static class Action implements ActionRendererContext
   {
      /** The origin of the action. */
      private final String name;

      /** The origin of the action. */
      private final String family;

      /** The action url. */
      private final String url;

      /** Enabled or not. */
      private final boolean enabled;

      public Action(String name, String family, String url, boolean enabled)
      {
         this.name = name;
         this.family = family;
         this.url = url;
         this.enabled = enabled;
      }

      public String getName()
      {
         return name;
      }

      public String getFamily()
      {
         return family;
      }

      public String getURL()
      {
         return url;
      }

      public boolean isEnabled()
      {
         return enabled;
      }

      public String getProperty(String name)
      {
         return null;
      }

      public Map getProperties()
      {
         return Collections.EMPTY_MAP;
      }
   }
}
