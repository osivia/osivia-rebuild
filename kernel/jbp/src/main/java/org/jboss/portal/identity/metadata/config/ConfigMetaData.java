/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity.metadata.config;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class ConfigMetaData
{
   private Map options;


   public ConfigMetaData()
   {
      options = new HashMap();
   }


   public Map getOptions()
   {
      return options;
   }

   public void setOptions(Map options)
   {
      this.options = options;
   }

   public void addOption(ConfigOptionMetaData option)
   {
      options.put(option.getName(), option);
   }

   public ConfigOptionMetaData getOption(String name)
   {
      return (ConfigOptionMetaData)options.get(name);
   }
}
