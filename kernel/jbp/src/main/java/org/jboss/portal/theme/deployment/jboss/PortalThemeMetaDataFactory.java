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
package org.jboss.portal.theme.deployment.jboss;

import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.portal.theme.metadata.ThemeLinkMetaData;
import org.jboss.portal.theme.metadata.ThemeScriptMetaData;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $Revision: 8784 $
 */
public class PortalThemeMetaDataFactory implements ObjectModelFactory
{

   public Object newRoot(Object root, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      return new ArrayList();
   }

   public Object completeRoot(Object root, UnmarshallingContext nav, String nsURI, String localName)
   {
      return root;
   }

   public Object newChild(ArrayList list, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if ("theme".equals(localName))
      {
         return new PortalThemeMetaData();
      }
      return null;
   }

   public void addChild(ArrayList list, PortalThemeMetaData portalTheme, UnmarshallingContext nav, String nsURI, String localName)
   {
      list.add(portalTheme);
   }

   public void setValue(PortalThemeMetaData portalTheme, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if ("name".equals(localName))
      {
         portalTheme.setName(value);
      }
      else if ("theme-implementation".equals(localName))
      {
         portalTheme.setClassName(value);
      }
   }

   public Object newChild(PortalThemeMetaData portalTheme, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if ("script".equals(localName))
      {
         ThemeScriptMetaData script = new ThemeScriptMetaData();
         script.setSrc(attrs.getValue("src"));
         script.setId(attrs.getValue("id"));
         script.setType(attrs.getValue("type"));
         script.setCharset(attrs.getValue("charset"));
         return script;
      }
      else if ("link".equals(localName))
      {
         ThemeLinkMetaData link = new ThemeLinkMetaData();
         link.setHref(attrs.getValue("href"));
         link.setId(attrs.getValue("id"));
         link.setType(attrs.getValue("type"));
         link.setTitle(attrs.getValue("title"));
         link.setMedia(attrs.getValue("media"));
         link.setRel(attrs.getValue("rel"));
         return link;
      }
      return null;
   }

   public void setValue(ThemeScriptMetaData script, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if ("script".equals(localName))
      {
         script.setBodyContent(value);
      }
   }

   public void addChild(PortalThemeMetaData portalTheme, ThemeScriptMetaData script, UnmarshallingContext nav, String nsURI, String localName)
   {
      portalTheme.getScripts().add(script);
   }

   public void addChild(PortalThemeMetaData portalTheme, ThemeLinkMetaData link, UnmarshallingContext nav, String nsURI, String localName)
   {
      portalTheme.getLinks().add(link);
   }
}
