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

package org.jboss.portal.identity.metadata.service;

import org.jboss.portal.identity.metadata.config.IdentityConfigurationMetaData;
import org.jboss.portal.identity.metadata.config.ConfigurationParser;
import org.jboss.portal.identity.metadata.config.IdentityMetadataProcessor;
import org.jboss.portal.identity.service.IdentityConfigurationService;


/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class IdentityServicesMetaData
{

   private DatasourceServicesMetaData datasourceServices;

   private ModuleServicesMetaData moduleServices;

   private IdentityConfigurationService configurationService;

   public IdentityServicesMetaData(String defaultConfigFile, String configFile) throws Exception
   {
      IdentityConfigurationMetaData meta = ConfigurationParser.parseIdentityConfiguration(configFile);
      IdentityConfigurationMetaData defaultMeta = ConfigurationParser.parseIdentityConfiguration(defaultConfigFile);

      datasourceServices = new DatasourceServicesMetaData(defaultMeta.getDatasources(), meta.getDatasources());
      moduleServices = new ModuleServicesMetaData(defaultMeta.getModules(), meta.getModules());

       //inject configuration service
      configurationService = new IdentityConfigurationService(meta.getOptions());
      IdentityMetadataProcessor.updateOptionsWithDefaults(configurationService, defaultMeta.getOptions());

   }

   public DatasourceServicesMetaData getDatasourceServices()
   {
      return datasourceServices;
   }

   public ModuleServicesMetaData getModuleServices()
   {
      return moduleServices;
   }

   public IdentityConfigurationService getConfigurationService()
   {
      return configurationService;
   }
}
