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

import org.jboss.logging.Logger;
import org.jboss.portal.identity.metadata.config.ModulesMetaData;
import org.jboss.portal.identity.metadata.config.ModuleMetaData;
import org.jboss.portal.identity.metadata.config.IdentityMetadataProcessor;
import org.jboss.portal.identity.metadata.config.ConfigOptionMetaData;
import org.jboss.portal.identity.IdentityException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class ModuleServicesMetaData
{
   /** . */
   private List modulesList = new LinkedList();
   
   /** . */
   private Logger logger = Logger.getLogger(ModuleServicesMetaData.class);

   public ModuleServicesMetaData(ModulesMetaData defaultModulesMetaData, ModulesMetaData modulesMetaData) throws Exception
   {
      //Map[implementation] --> Map[Type] --> ModuleMetaData
      Map defaultImplementations = new HashMap();

      //update modules data with defaults
      try
      {
         List defaultModules = defaultModulesMetaData.getModules();
         for (Iterator iterator = defaultModules.iterator(); iterator.hasNext();)
         {
            ModuleMetaData module = (ModuleMetaData)iterator.next();

            //check if defaults contains all information
            if (module.getType() == null ||
               module.getImplementation() == null ||
               //module.getJNDIName() == null ||
               module.getServiceName() == null ||
               module.getConfig() == null)
            {
               
               logger.error("Default module configuration isn't complete" + module);
               throw new IdentityException("Default module configuration must be complete");
            }

            //store them as maps for different implementations
            String implType = module.getImplementation();
            Map implementation;
            if (defaultImplementations.containsKey(implType))
            {
               implementation = (Map)defaultImplementations.get(implType);
            }
            else
            {
               implementation = new HashMap();
            }

            //store per implementation
            implementation.put(module.getType(), module);
            defaultImplementations.put(implType, implementation);
         }
      }
      catch (Exception e)
      {
         throw new IdentityException("Error during processing default configuration file", e);
      }

      //process modules for instantiation
      for (Iterator iterator = modulesMetaData.getModules().iterator(); iterator.hasNext();)
      {

         ModuleMetaData module = (ModuleMetaData) iterator.next();
         //log.info("Processing module: " + module.getType() + "/" + module.getImplementation());// + "/" + module.getClassName());

         IdentityMetadataProcessor.updateModuleWithDefaults(module, defaultImplementations);

         //generate initial options
         Map configOptions = module.getConfig().getOptions();
         Map optionMap = new HashMap();
         for (Iterator iterator1 = configOptions.keySet().iterator(); iterator1.hasNext();)
         {
            String optionName = (String) iterator1.next();
            ConfigOptionMetaData option = (ConfigOptionMetaData) configOptions.get(optionName);
            optionMap.put(optionName, option.getValue());
         }

         //instantiate the module
         //IdentityModuleService moduleService = null;

         if (module.getClassName() == null)
         {
            throw new IdentityException("No classname defined for module type: " + module.getType()
                  + ". Wrong configuration.");
         }

         modulesList.add(new ModuleServiceMetaData(module, optionMap));

      }

   }


   public List getModulesList()
   {
      return modulesList;
   }
}
