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

import org.jboss.portal.identity.metadata.config.ModuleMetaData;
import org.jboss.portal.identity.metadata.config.ConfigOptionMetaData;
import org.jboss.portal.identity.metadata.config.DatasourceMetaData;
import org.jboss.portal.identity.metadata.config.OptionsMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupOptionMetaData;
import org.jboss.portal.identity.service.IdentityConfigurationService;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class IdentityMetadataProcessor
{
   /**
    * updates module with proper defaults
    *
    * @param module
    * @param defaultModules
    */
   public static void updateModuleWithDefaults(ModuleMetaData module, Map defaultModules)
   {
      if (module.getImplementation() == null)
      {
         return;
      }
      if (!defaultModules.containsKey(module.getImplementation()))
      {
         return;
      }
      Map modules = (Map)defaultModules.get(module.getImplementation());
      if (!modules.containsKey(module.getType()))
      {
         return;
      }
      ModuleMetaData def = (ModuleMetaData)modules.get(module.getType());

      if (module.getClassName() == null)
      {
         module.setClassName(def.getClassName());
      }
      if (module.getServiceName() == null)
      {
         module.setServiceName(def.getServiceName());
      }

      //now check if config options are overwritten
      if (module.getConfig() == null)
      {
         module.setConfig(def.getConfig());
      }
      else
      {
         Map moduleOptions = module.getConfig().getOptions();
         if (moduleOptions == null)
         {
            return;
         }
         Map defOptions = def.getConfig().getOptions();

         for (Iterator iterator = defOptions.keySet().iterator(); iterator.hasNext();)
         {
            String key = (String)iterator.next();
            if (!moduleOptions.containsKey(key))
            {
               ConfigOptionMetaData o = (ConfigOptionMetaData)defOptions.get(key);
               module.getConfig().addOption(o);
            }
         }
      }
   }

   /**
    * updates module with proper defaults
   */
   public static void updateDatasourceWithDefaults(DatasourceMetaData ds, Map defaultDS)
   {
      if (ds.getName() == null)
      {
         return;
      }
      if (!defaultDS.containsKey(ds.getName()))
      {
         return;
      }
      DatasourceMetaData def = (DatasourceMetaData)defaultDS.get(ds.getName());
      if (ds.getClassName() == null)
      {
         ds.setClassName(def.getClassName());
      }
      if (ds.getServiceName() == null)
      {
         ds.setServiceName(def.getServiceName());
      }

      //now check if config options are overwritten
      if (ds.getConfig() == null)
      {
         ds.setConfig(def.getConfig());
      }
      else
      {
         Map dsOptions = ds.getConfig().getOptions();
         if (dsOptions == null)
         {
            return;
         }
         Map defOptions = def.getConfig().getOptions();

         for (Iterator iterator = defOptions.keySet().iterator(); iterator.hasNext();)
         {
            String key = (String)iterator.next();
            if (!dsOptions.containsKey(key))
            {
               ConfigOptionMetaData o = (ConfigOptionMetaData)defOptions.get(key);
               ds.getConfig().addOption(o);
            }
         }
      }
   }

   /**
    * Check current options and update them with defaults if not exists;
    *
    * @param config
    * @param defaults
    */
   public static void updateOptionsWithDefaults(IdentityConfigurationService config, OptionsMetaData defaults)
   {
      //Map newGroups = new HashMap();
      Map groups = defaults.getGroups();
      for (Iterator iterator = groups.keySet().iterator(); iterator.hasNext();)
      {
         String groupKey = (String)iterator.next();

         OptionsGroupMetaData group = (OptionsGroupMetaData)groups.get(groupKey);

         if (config.getOptions(groupKey) == null)
         {
            config.setOptions(groupKey, new HashMap());
         }

         //Map newOptions = new HashMap();
         Map options = group.getOptions();
         for (Iterator iterator1 = options.keySet().iterator(); iterator1.hasNext();)
         {
            String optionKey = (String)iterator1.next();

            OptionsGroupOptionMetaData option = (OptionsGroupOptionMetaData)options.get(optionKey);

            if (config.getValues(groupKey, optionKey) == null)
            {

               //config.setValues(groupKey, optionKey, new HashSet());
               config.setValues(groupKey, optionKey, option.getValues());
            }


         }
      }

   }

}
