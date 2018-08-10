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

import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.StateURIMetaData;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PortalLayoutMetaDataFactory implements ObjectModelFactory
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
      if ("layout".equals(localName))
      {
         return new PortalLayoutMetaData();
      }
      return null;
   }

   public void addChild(ArrayList list, PortalLayoutMetaData portalLayout, UnmarshallingContext nav, String nsURI, String localName)
   {
      list.add(portalLayout);
   }

   public void setValue(PortalLayoutMetaData portalLayout, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if ("name".equals(localName))
      {
         portalLayout.setName(value);
      }
      else if ("layout-implementation".equals(localName))
      {
         portalLayout.setClassName(value);
      }
      else if ("uri".equals(localName))
      {
         portalLayout.setURI(value);
      }
      else if ("region".equals(localName))
      {
         portalLayout.getRegionNames().add(value);
      }
   }

   public Object newChild(PortalLayoutMetaData portalLayout, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if ("uri".equals(localName))
      {
         String state = attrs.getValue("state");
         if (state != null)
         {
            StateURIMetaData stateURI = new StateURIMetaData();
            stateURI.setState(state);
            return stateURI;
         }
      }
      else if ("region".equals(localName))
      {
         return attrs.getValue("name");
      }
      return null;
   }

   public void setValue(StateURIMetaData stateURI, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      if ("uri".equals(localName))
      {
         stateURI.setURI(value);
      }
   }

   public void addChild(PortalLayoutMetaData portalLayout, StateURIMetaData stateURI, UnmarshallingContext nav, String nsURI, String localName)
   {
      portalLayout.getLayoutURIStateMap().put(stateURI.getState(), stateURI);
   }

   public void addChild(PortalLayoutMetaData portalLayout, String regionName, UnmarshallingContext nav, String nsURI, String localName)
   {
      portalLayout.getRegionNames().add(regionName);
   }
}
