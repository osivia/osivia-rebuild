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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An implementation of a <code>ThemeScript</code>. <p>The script element is responsible for creating the markup that
 * represents a single script tag in the response markup.</p>
 *
 * @author Martin Holzner
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.ThemeElement
 */
public final class ThemeScript
   implements ThemeElement
{
	
	private static final Log log  = LogFactory.getLog(ThemeScript.class);



   private final String src;
   private final String type;
   private final String id;
   private final String charset;
   private final String script;

   /**
    * Create a theme script.
    *
    * @param contextPath the URI of the servlet context that hosts the resource that the script tag is pointing to
    * @param src         the value of the src attribute
    * @param type        the value of the type attribute
    */
   public ThemeScript(String contextPath, String src, String id, String type, String bodyContent, String charset)
   {
      if (log.isDebugEnabled())
      {
         log.debug("creating theme script with src=" + src + " type=" + type);
      }
      this.src = src;
      this.id = id;
      this.type = type;
      this.charset = charset;
      script = buildScriptMarkup(contextPath, src, id, type, bodyContent, charset);
   }

   /** @see org.jboss.portal.theme.ThemeScript#getScript */
   public String getScript()
   {
      return script;
   }

   /** @see java.lang.Object#toString() */
   public String toString()
   {
      return script;
   }

   /** @see org.jboss.portal.theme.ThemeElement#getElement */
   public String getElement()
   {
      return getScript();
   }

   /** @see org.jboss.portal.theme.ThemeElement@getAttributeValue */
   public String getAttributeValue(String attributeName)
   {
      if ("type".equals(attributeName))
      {
         return this.type;
      }

      if ("id".equals(attributeName))
      {
         return this.id;
      }

      if ("src".equals(attributeName))
      {
         return this.src;
      }

      if ("charset".equals(attributeName))
      {
         return this.charset;
      }

      return null;
   }

   private static String buildScriptMarkup(String contextPath, String src, String id, String type, String bodyContent, String charset)
   {
      if (log.isDebugEnabled())
      {
         log.debug("build script markup...");
      }
      StringBuffer script = new StringBuffer();
      script.append("<script");
      // adopt the context and inject a theme id param for the theme
      // servlet to be able to pick up the resource from the correct theme
      if (src != null && !"".equals(src))
      {
         StringBuffer correctSRC = new StringBuffer();
         if (src.startsWith("/"))
         {
            correctSRC.append(contextPath);
         }
         correctSRC.append(src);
         script.append(" src=\"").append(correctSRC).append("\"");
      }

      if (id != null && !"".equals(id))
      {
         script.append(" id=\"").append(id).append("\"");
      }

      if (type != null && !"".equals(type))
      {
         script.append(" type=\"").append(type).append("\"");
      }

      if (charset != null && !"".equals(charset))
      {
         script.append(" charset=\"").append(charset).append("\"");
      }

      if (bodyContent == null || "".equals(bodyContent))
      {
         //script.append(" />");
         script.append("></script>");
      }
      else
      {
         script.append(">").append(bodyContent).append("</script>");
      }

      if (log.isDebugEnabled())
      {
         log.debug("returning script : " + script);
      }

      return script.toString();
   }
}
