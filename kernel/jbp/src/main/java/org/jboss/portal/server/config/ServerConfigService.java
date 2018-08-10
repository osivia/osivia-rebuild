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
package org.jboss.portal.server.config;

import org.jboss.portal.common.util.CLResourceLoader;
import org.jboss.portal.common.util.LoaderResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ServerConfigService implements ServerConfig
{

   /** . */
   private final String domain = "portal";

   /** . */
   private String configLocation;

   /** . */
   private LoaderResource configResource;

   /** . */
   private Map properties;

   public String getDomain()
   {
      return domain;
   }

   public ServerConfigService()
   {
      properties = new HashMap();
   }

   public String getConfigLocation()
   {
      return configLocation;
   }

   public void setConfigLocation(String configLocation)
   {
      this.configLocation = configLocation;
   }

   public LoaderResource getConfigResource()
   {
      return configResource;
   }

   public String getProperty(String name)
   {
      return (String)properties.get(name);
   }

   public void setProperty(String name, String value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("No null name accepted");
      }
      synchronized (this)
      {
         Map copy = new HashMap(properties);
         if (value != null)
         {
            value = value.trim();
            copy.put(name, value);
         }
         else
         {
            copy.remove(name);
         }
         properties = copy;
      }
   }

   public String dumpProperties(boolean html)
   {
      StringBuffer buffer = new StringBuffer();
      for (Iterator i = new TreeMap(properties).entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         buffer.append(entry.getKey()).append("=").append(entry.getValue()).append(html ? "<br/>" : "\n");
      }
      return buffer.toString();
   }

   public void create() throws Exception
   {
      configResource = new CLResourceLoader().getResource(configLocation);
      if (configResource.exists())
      {
         properties.clear();
         properties.putAll(configResource.asProperties(true));
      }

      //
      for (Iterator i = properties.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String value = (String)entry.getValue();
         value = value.trim();
         entry.setValue(value);
      }

      // Add the domain for now portal value
      properties.put(DOMAIN_KEY, domain);
   }

   public void destroy() throws Exception
   {
      properties.clear();
      configResource = null;
   }
}
