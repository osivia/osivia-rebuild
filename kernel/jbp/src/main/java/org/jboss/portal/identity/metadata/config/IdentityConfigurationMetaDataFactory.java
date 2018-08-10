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

import org.jboss.xb.binding.GenericObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.jboss.portal.identity.metadata.config.IdentityConfigurationMetaData;
import org.jboss.portal.identity.metadata.config.ModulesMetaData;
import org.jboss.portal.identity.metadata.config.OptionsMetaData;
import org.jboss.portal.identity.metadata.config.ModuleMetaData;
import org.jboss.portal.identity.metadata.config.ConfigMetaData;
import org.jboss.portal.identity.metadata.config.ConfigOptionMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupMetaData;
import org.jboss.portal.identity.metadata.config.OptionsGroupOptionMetaData;
import org.jboss.portal.identity.metadata.config.DatasourcesMetaData;
import org.jboss.portal.identity.metadata.config.DatasourceMetaData;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class IdentityConfigurationMetaDataFactory implements GenericObjectModelFactory
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityConfigurationMetaDataFactory.class);

   public Object newRoot(Object object, UnmarshallingContext unmarshallingContext, String string, String string1, Attributes attributes)
   {
      return new IdentityConfigurationMetaData();
   }

   public Object completeRoot(Object root, UnmarshallingContext unmarshallingContext, String string, String string1)
   {
      return root;
   }

   public Object newChild(Object root, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if (root instanceof IdentityConfigurationMetaData)
      {
         if ("modules".equals(localName))
         {
            return new ModulesMetaData();
         }
         else if ("datasources".equals(localName))
         {
            return new DatasourcesMetaData();
         }
         else if("options".equals(localName))
         {
            return new OptionsMetaData();
         }
      }
      if (root instanceof DatasourcesMetaData)
      {
         if ("datasource".equals(localName))
         {
            return new DatasourceMetaData();
         }
      }
      if (root instanceof DatasourceMetaData)
      {
         if ("config".equals(localName))
         {
            return new ConfigMetaData();
         }
      }
      else if (root instanceof ModulesMetaData)
      {
         if ("module".equals(localName))
         {
            return new ModuleMetaData();
         }
      }
      else if (root instanceof ModuleMetaData)
      {
         if ("config".equals(localName))
         {
            return new ConfigMetaData();
         }
      }
      else if (root instanceof ConfigMetaData)
      {
         if ("option".equals(localName))
         {
            return new ConfigOptionMetaData();
         }
      }
      else if (root instanceof OptionsMetaData)
      {
         if ("option-group".equals(localName))
         {
            return new OptionsGroupMetaData();
         }
      }
      else if (root instanceof OptionsGroupMetaData)
      {
         if ("option".equals(localName))
         {
            return new OptionsGroupOptionMetaData();
         }
      }
      return null;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext nav, String nsURI, String localName)
   {
      if (parent instanceof IdentityConfigurationMetaData)
      {
         IdentityConfigurationMetaData identity = (IdentityConfigurationMetaData)parent;
         if (child instanceof ModulesMetaData)
         {
            identity.setModules((ModulesMetaData)child);
         }
         else if (child instanceof DatasourcesMetaData)
         {
            identity.setDatasources((DatasourcesMetaData)child);
         }
         else if (child instanceof OptionsMetaData)
         {
            identity.setOptions((OptionsMetaData)child);
         }
      }
      else if (parent instanceof DatasourcesMetaData)
      {
         DatasourcesMetaData datasources = (DatasourcesMetaData)parent;
         if (child instanceof DatasourceMetaData)
         {
            datasources.addDatasource((DatasourceMetaData)child);
         }
      }
      else if (parent instanceof DatasourceMetaData)
      {
         DatasourceMetaData datasource = (DatasourceMetaData)parent;
         if (child instanceof ConfigMetaData)
         {
            datasource.setConfig((ConfigMetaData)child);
         }
      }
      
      else if (parent instanceof ModulesMetaData)
      {
         ModulesMetaData modules = (ModulesMetaData)parent;
         if (child instanceof ModuleMetaData)
         {
            modules.addModule((ModuleMetaData)child);
         }
      }
      else if (parent instanceof ModuleMetaData)
      {
         ModuleMetaData module = (ModuleMetaData)parent;
         if (child instanceof ConfigMetaData)
         {
            module.setConfig((ConfigMetaData)child);
         }
      }
      else if (parent instanceof ConfigMetaData)
      {
         ConfigMetaData config = (ConfigMetaData)parent;
         if (child instanceof ConfigOptionMetaData)
         {
            config.addOption((ConfigOptionMetaData)child);
         }
      }
      else if (parent instanceof OptionsMetaData)
      {
         OptionsMetaData options = (OptionsMetaData)parent;
         if (child instanceof OptionsGroupMetaData)
         {
            options.addGroup((OptionsGroupMetaData)child);
         }
      }
      else if (parent instanceof OptionsGroupMetaData)
      {
         OptionsGroupMetaData group = (OptionsGroupMetaData)parent;
         if (child instanceof OptionsGroupOptionMetaData)
         {
            group.addOption((OptionsGroupOptionMetaData)child);
         }
      }
   }

   public void setValue(Object object, UnmarshallingContext unmarshallingContext, String nsUri, String localName, String value)
   {
      if (object instanceof ModuleMetaData)
      {
         ModuleMetaData module = (ModuleMetaData)object;
         if ("type".equals(localName))
         {
            module.setType(value);
         }
         else if ("implementation".equals(localName))
         {
            module.setImplementation(value);
         }
         else if ("service-name".equals(localName))
         {
            module.setServiceName(value);
         }
         else if ("class".equals(localName))
         {
            module.setClassName(value);
         }
      }
      if (object instanceof DatasourceMetaData)
      {
         DatasourceMetaData ds = (DatasourceMetaData)object;
         if ("name".equals(localName))
         {
            ds.setName(value);
         }
         else if ("service-name".equals(localName))
         {
            ds.setServiceName(value);
         }
         else if ("class".equals(localName))
         {
            ds.setClassName(value);
         }
      }
      else if (object instanceof ConfigOptionMetaData)
      {
         ConfigOptionMetaData option = (ConfigOptionMetaData)object;
         if ("name".equals(localName))
         {
            option.setName(value);
         }
         else if ("value".equals(localName))
         {
            option.setValue(value);
         }
      }
      else if (object instanceof OptionsGroupMetaData)
      {
         OptionsGroupMetaData group = (OptionsGroupMetaData)object;
         if ("group-name".equals(localName))
         {
            group.setName(value);
         }
      }
      else if (object instanceof OptionsGroupOptionMetaData)
      {
         OptionsGroupOptionMetaData option = (OptionsGroupOptionMetaData)object;
         if ("name".equals(localName))
         {
            option.setName(value);
         }
         else if ("value".equals(localName))
         {
            option.addValue(value);
         }
      }
   }
}
