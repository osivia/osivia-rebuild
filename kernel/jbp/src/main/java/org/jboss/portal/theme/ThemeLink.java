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

import org.jboss.logging.Logger;

/**
 * An implementation of a <code>ThemeLink</code>. <p>The theme link is responsible for producing the markup of a link
 * element in the markup response's head tag.</p>
 *
 * @author Martin Holzner
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see ThemeElement
 */
public final class ThemeLink
   implements ThemeElement
{
   private static final Logger log = Logger.getLogger(ThemeLink.class);
   private final String rel;
   private final String type;
   private final String href;
   private final String title;
   private final String media;
   private final String id;
   private final String link;

   /**
    * Create a new ThemeLink
    *
    * @param contextPath the URI of the servlet context that hosts the resource that the link is pointing to
    * @param rel         the rel attributes value
    * @param type        the type attributes value
    * @param href        the href attributes value
    * @param title       the title attributes value
    * @param media       the media attributes value
    */
   public ThemeLink(String contextPath, String rel, String type, String href, String id, String title, String media)
   {
      if (log.isDebugEnabled())
      {
         log.debug("create theme link with rel=" + rel + " type=" + type
            + " href=" + href + "title=" + title + " media=" + media);
      }
      this.rel = rel;
      this.type = type;
      this.href = href;
      this.title = title;
      this.media = media;
      this.id = id;

      link = buildLinkMarkup(contextPath, rel, type, href, id, title, media);
   }

   /** @see org.jboss.portal.theme.ThemeLink#getLink */
   public String getLink()
   {
      return link;
   }

   /** @see java.lang.Object#toString */
   public String toString()
   {
      return link;
   }

   /** @see org.jboss.portal.theme.ThemeElement#getElement */
   public String getElement()
   {
      return getLink();
   }

   /** @see org.jboss.portal.theme.ThemeElement@getAttributeValue */
   public String getAttributeValue(String attributeName)
   {
      if ("rel".equals(attributeName))
      {
         return this.rel;
      }

      if ("type".equals(attributeName))
      {
         return this.type;
      }

      if ("href".equals(attributeName))
      {
         return this.href;
      }

      if ("title".equals(attributeName))
      {
         return this.title;
      }

      if ("media".equals(attributeName))
      {
         return this.media;
      }

      if ("id".equals(attributeName))
      {
         return this.id;
      }

      return null;
   }

   private static String buildLinkMarkup(String contextPath, String rel, String type, String href,
                                         String id, String title, String media)
   {
      if (log.isDebugEnabled())
      {
         log.debug("build link markup...");
      }
      StringBuffer link = new StringBuffer();

      link.append("<link rel=\"").append(rel).append("\"");

      if (type != null)
      {
         link.append(" type=\"").append(type).append("\"");
      }

      if (id != null && !"".equals(id))
      {
         link.append(" id=\"").append(id).append("\"");
      }

      if (href != null)
      {
         // adopt the context and inject a theme id param for the theme
         // servlet to be able to pick up the resource from the correct theme
         StringBuffer correctHREF = new StringBuffer();
         if (href.startsWith("/"))
         {
            correctHREF.append(contextPath);
         }
         correctHREF.append(href);
         link.append(" href=\"").append(correctHREF).append("\"");
      }

      if (title != null && !"".equals(title))
      {
         link.append(" title=\"").append(title).append("\"");
      }
      if (media != null && !"".equals(media))
      {
         link.append(" media=\"").append(media).append("\"");
      }

      link.append(" />");

      if (log.isDebugEnabled())
      {
         log.debug("returning: " + link);
      }
      return link.toString();
   }
}
