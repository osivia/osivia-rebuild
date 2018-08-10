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

/**
 * @author Martin Holzner
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 */
public final class ThemeScriptMetaData
{

   /** . */
   private String src;

   /** . */
   private String type;

   /** . */
   private String id;

   /** . */
   private String bodyContent;

   private String charset;

   public String getSrc()
   {
      return src;
   }

   public void setSrc(String src)
   {
      this.src = src;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getBodyContent()
   {
      return bodyContent;
   }

   public void setBodyContent(String bodyContent)
   {
      this.bodyContent = bodyContent;
   }

   public String getCharset()
   {
      return charset;
   }

   public void setCharset(String charset)
   {
      this.charset = charset;
   }
}
