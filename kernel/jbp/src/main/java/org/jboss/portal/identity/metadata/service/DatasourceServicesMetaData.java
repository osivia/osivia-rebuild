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

import org.jboss.portal.identity.metadata.config.DatasourcesMetaData;
import org.jboss.portal.identity.metadata.config.DatasourceMetaData;
import org.jboss.portal.identity.metadata.config.IdentityMetadataProcessor;
import org.jboss.portal.identity.metadata.config.ConfigOptionMetaData;
import org.jboss.portal.identity.IdentityException;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractPropertyMetaData;
import org.jboss.kernel.spi.dependency.KernelControllerContext;

import javax.management.ObjectName;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class DatasourceServicesMetaData
{

   private List datasourcesList = new LinkedList();

   public DatasourceServicesMetaData(DatasourcesMetaData defaultDatasourcesMeta, DatasourcesMetaData datasourcesMeta) throws Exception
   {
      if (datasourcesMeta == null)
      {
         new IllegalArgumentException("null datasources");
      }

      if (defaultDatasourcesMeta == null)
      {
         new IllegalArgumentException("null defaultDatasources");
      }

      //map default datasources by name
      Map defaultDatasources = new HashMap();
      for (Iterator iterator = defaultDatasourcesMeta.getDatasources().iterator(); iterator.hasNext();)
      {
         DatasourceMetaData ds = (DatasourceMetaData)iterator.next();
         defaultDatasources.put(ds.getName(), ds);

      }

      for (Iterator iterator = datasourcesMeta.getDatasources().iterator(); iterator.hasNext();)
         {
            DatasourceMetaData ds = (DatasourceMetaData)iterator.next();
            //if (log.isDebugEnabled()) log.debug("processing datasource: " + ds.getName() + "/" + ds.getClassName());
            //log.info("Installing datasourc: " + ds.getName());
            IdentityMetadataProcessor.updateDatasourceWithDefaults(ds, defaultDatasources);

            //generate initial options
            Map configOptions = ds.getConfig().getOptions();
            Map optionMap = new HashMap();
            for (Iterator iterator1 = configOptions.keySet().iterator(); iterator1.hasNext();)
            {
               String optionName = (String)iterator1.next();
               ConfigOptionMetaData option = (ConfigOptionMetaData)configOptions.get(optionName);
               optionMap.put(optionName, option.getValue());
            }

            datasourcesList.add(new DatasourceServiceMetaData(ds, optionMap));

            //instantiate the module
            //IdentityModuleService moduleService = null;


            if (ds.getClassName() == null)
            {
               throw new IdentityException("Class name not found for datasource type: " + ds.getName() + " wrong configuration");
            }

         }
   }


   public List getDatasourcesList()
   {
      return datasourcesList;
   }
}
