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
package org.jboss.portal.server;

import org.jboss.portal.common.util.ParameterMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class AbstractServerURL implements ServerURL
{

   /** . */
   private String portalRequestPath;

   /** . */
   private Map parameters = new HashMap();

   /** . */
   private ParameterMap map = new ParameterMap(parameters);

   public void setPortalRequestPath(String portalRequestPath)
   {
      this.portalRequestPath = portalRequestPath;
   }

   public String getPortalRequestPath()
   {
      return portalRequestPath;
   }

   public void setParameterValue(String name, String value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (value == null)
      {
         map.remove(name);
      }
      else
      {
         map.put(name, new String[]{value});
      }
   }

   public void setParameterValues(String name, String[] values)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (values == null)
      {
         map.remove(name);
      }
      else
      {
         map.put(name, values);
      }
   }

   public String[] getParameterValues(String name)
   {
      return (String[])map.get(name);
   }

   public ParameterMap getParameterMap()
   {
      return map;
   }
}
