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

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.portal.theme.metadata.RendererSetMetaData;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

import javax.activation.MimeTypeParseException;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10337 $
 */
public class RenderSetMetaDataFactory implements ObjectModelFactory
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
      if ("renderSet".equals(localName))
      {
         String name = attrs.getValue("name");
         RenderSetMetaData renderSet = new RenderSetMetaData();
         renderSet.setName(name);
         return renderSet;
      }
      return null;
   }

   public void addChild(ArrayList list, RenderSetMetaData renderSet, UnmarshallingContext nav, String nsURI, String localName)
   {
      list.add(renderSet);
   }

   public Object newChild(RenderSetMetaData renderSet, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs) throws MimeTypeParseException
   {
      if ("set".equals(localName))
      {
         RendererSetMetaData rendererSet = new RendererSetMetaData();
         String contentType = attrs.getValue("content-type");
         rendererSet.setMediaType(MediaType.create(contentType));
         return rendererSet;
      }
      return null;
   }

   public void addChild(RenderSetMetaData renderSet, RendererSetMetaData rendererSet, UnmarshallingContext nav, String nsURI, String localName)
   {
      renderSet.getRendererSet().add(rendererSet);
   }

   public void setValue(RendererSetMetaData rendererSet, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if ("region-renderer".equals(localName))
      {
         rendererSet.setRegionRenderer(value);
      }
      else if ("window-renderer".equals(localName))
      {
         rendererSet.setWindowRenderer(value);
      }
      else if ("portlet-renderer".equals(localName))
      {
         rendererSet.setPortletRenderer(value);
      }
      else if ("decoration-renderer".equals(localName))
      {
         rendererSet.setDecorationRenderer(value);
      }
      else if (localName.equals("ajax-enabled"))
      {
         rendererSet.setAjaxEnabled(value.equals("true"));
      }
   }
}
