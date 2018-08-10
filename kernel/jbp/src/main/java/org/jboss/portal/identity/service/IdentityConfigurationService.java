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
package org.jboss.portal.identity.service;

import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.metadata.config.OptionsMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupOptionMetaData;
import org.jboss.portal.identity.IdentityException;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class IdentityConfigurationService extends IdentityModuleService implements IdentityConfiguration
{

   //TODO: improve access to data to not blow up by NullPointerEx

   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityConfigurationService.class);

   private Map data;

   public IdentityConfigurationService()
   {
      super(IdentityContext.TYPE_IDENTITY_CONFIGURATION);
   }

   public IdentityConfigurationService(Map optionGroups)
   {
      this();
      this.data = optionGroups; 
   }

   public IdentityConfigurationService(OptionsMetaData meta) throws IdentityException
   {
      this();
      if (meta == null || meta.getGroups() == null)
      {
         data = new HashMap();
         return;
      }
      try
      {
         Map newGroups = new HashMap();
         Map groups = meta.getGroups();
         for (Iterator iterator = groups.keySet().iterator(); iterator.hasNext();)
         {
            String groupKey = (String)iterator.next();
            OptionsGroupMetaData group = (OptionsGroupMetaData)groups.get(groupKey);

            Map newOptions = new HashMap();
            Map options = group.getOptions();
            for (Iterator iterator1 = options.keySet().iterator(); iterator1.hasNext();)
            {
               String optionKey = (String)iterator1.next();
               OptionsGroupOptionMetaData option = (OptionsGroupOptionMetaData)options.get(optionKey);

               newOptions.put(option.getName(),option.getValues());
            }

            newGroups.put(group.getName(), newOptions);
         }
         setData(newGroups);
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot create identity configuration options data:", e);
      }
   }

   public Set getValues(String optionGroup, String option)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (data.containsKey(optionGroup))
      {
         Map group = (Map)data.get(optionGroup);
         if (group.containsKey(option))
         {
            return (Set)group.get(option);
         }
      }
      return null;
   }

   public String getValue(String optionGroup, String option)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (data.containsKey(optionGroup))
      {
         Map group = (Map)data.get(optionGroup);
         if (group.containsKey(option))
         {
            Set values = (Set)group.get(option);
            if (values.size() > 0)
            {
               return (String)values.toArray()[0];
            }
         }
      }
      return null;
   }

   public String getValue(String option)
   {
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (data.containsKey(GROUP_COMMON))
      {
         Map group = (Map)data.get(GROUP_COMMON);
         if (group.containsKey(option))
         {
            Set values =  (Set)group.get(option);
            if (values.size() > 0)
            {
               return (String)values.toArray()[0];
            }
         }
      }
      return null;
   }

   public void setValues(String optionGroup, String option, Set values)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (values == null)
      {
         throw new IllegalArgumentException("null values list");
      }
      if (data.containsKey(optionGroup))
      {
         ((Map)data.get(optionGroup)).put(option, values);
      }
   }

   public void addValue(String optionGroup, String option, String value)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (value == null)
      {
         throw new IllegalArgumentException("null value name");
      }
      if (data.containsKey(optionGroup))
      {
         Map group = (Map)data.get(optionGroup);
         if (group.containsKey(option))
         {
            ((Set)group.get(option)).add(value);
         }
      }
   }

   public Map getOptions(String optionGroup)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (data.containsKey(optionGroup))
      {
         return (Map)data.get(optionGroup);
      }
      return null;
   }

   public void setOptions(String optionGroup, Map options)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (options == null)
      {
         throw new IllegalArgumentException("null options map");
      }
      data.put(optionGroup,options);
   }

   public void remoeOption(String optionGroup, String option)
   {
      if (optionGroup == null)
      {
         throw new IllegalArgumentException("null option group");
      }
      if (option == null)
      {
         throw new IllegalArgumentException("null option name");
      }
      if (data.containsKey(optionGroup))
      {
         ((Map)data.get(optionGroup)).put(option, null);
      }
   }


   public void setData(Map data)
   {
      this.data = data;
   }

   public Map getOptionGroups()
   {
      return data;
   }

//   public String getConfigFile()
//   {
//      return configFile;
//   }
//
//   public void setConfigFile(String configFile)
//   {
//      this.configFile = configFile;
//   }


}
