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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meta data describing a portal layout. <p/> here is an example for a layout descriptor: <p/> <layouts> <layout>
 * <name>nodesk</name> <uri>/nodesk/index.jsp</uri> <regions> <region name="left"/> <region name="center"/> </regions>
 * </layout> <layout> <name>generic</name> <uri>/layouts/generic/index.jsp</uri> <uri
 * state="maximized">/layouts/generic/maximized.jsp</uri> <regions> <region name="left"/> <region name="center"/>
 * <region name="navigation"/> </regions> </layout> </layouts> <p/> </p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public final class PortalLayoutMetaData
{

   private String name;
   private String uri;
   private Map stateURIMap;
   private List regionNames;
   private String className;

   public PortalLayoutMetaData()
   {
      this.className = "org.jboss.portal.theme.impl.JSPLayout";
      this.regionNames = new ArrayList();
      this.stateURIMap = new HashMap();
   }

   /** @return the name of this layout */
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   /** @return the URI for this layout as defined in the descriptor (this is the uri without a state attribute) */
   public String getURI()
   {
      return uri;
   }

   public void setURI(String URI)
   {
      this.uri = URI;
   }

   /** @return a map of layout URIs (String) keyed by layout state (String) as defined in the layout descriptor */
   public Map getLayoutURIStateMap()
   {
      return stateURIMap;
   }

   /** @return a list of the region names (of type String) of this layout as defined in the layout meta data */
   public List getRegionNames()
   {
      return regionNames;
   }

   /** @return the class name of the layout implementation to use */
   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }
}
