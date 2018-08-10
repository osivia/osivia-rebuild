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

package org.jboss.portal.identity.boot;

import org.jboss.portal.identity.ServiceJNDIBinder;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.metadata.config.ModuleMetaData;
import org.jboss.portal.identity.metadata.config.DatasourceMetaData;
import org.jboss.portal.identity.metadata.service.ModuleServiceMetaData;
import org.jboss.portal.identity.metadata.service.DatasourceServiceMetaData;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractPropertyMetaData;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 0.1 $
 */
public class IdentityServiceLoader
{
   //private final ServiceJNDIBinder serviceJNDIBinder;

   private final IdentityContext identityContext;

   private final Kernel kernel;

   private final boolean registerMBeans;


   public IdentityServiceLoader(IdentityContext identityContext, Kernel kernel, boolean registerMBeans)
   {
      this.identityContext = identityContext;
      this.kernel = kernel;
      this.registerMBeans = registerMBeans;
   }


   public void bootstrapDatasource(List datasources) throws Throwable
   {
      for (Iterator iterator = datasources.iterator(); iterator.hasNext();)
      {
         DatasourceServiceMetaData datasourceService = (DatasourceServiceMetaData)iterator.next();

         DatasourceMetaData ds = datasourceService.getDatasource();

         //instantiate datasource using MC
         String entryName = "portal:identity=Datasource,type=" + ds.getName();
         AbstractBeanMetaData dsBMD = new AbstractBeanMetaData(entryName,
            ds.getClassName());
         AbstractPropertyMetaData propertyBMD = new AbstractPropertyMetaData("identityContext", identityContext);
         dsBMD.addProperty(propertyBMD);
         propertyBMD = new AbstractPropertyMetaData("jndiBinder", getServiceJNDIBinder());
         dsBMD.addProperty(propertyBMD);

         //initiate parameters from <config>
         for (Iterator iterator1 = datasourceService.getOptions().keySet().iterator(); iterator1.hasNext();)
         {
            String propertyKey = (String)iterator1.next();
            String propertyValue = (String)datasourceService.getOptions().get(propertyKey);
            //if (log.isDebugEnabled()) log.debug("adding parameter: " + propertyKey + " ; " + propertyValue);
            propertyBMD = new AbstractPropertyMetaData(propertyKey, propertyValue);
            dsBMD.addProperty(propertyBMD);
         }

         // Installation
         //beans.add(dsBMD);
         KernelControllerContext controllerContext = kernel.getController().install(dsBMD);
         Object datasource = controllerContext.getTarget();

         //make a part of identityContext
         //moduleService.setIdentityContext(identityContext);

         //register as an mbean
         if (isRegisterMBeans() && (ds.getServiceName() != null))
         {
            unregisterMBean(ds.getServiceName());
            registerMBean(ds.getServiceName(), datasource);
         }


      }
   }

   public void bootstrapModules(List modules) throws Throwable
   {

      for (Iterator iterator = modules.iterator(); iterator.hasNext();)
      {

         ModuleServiceMetaData moduleService = (ModuleServiceMetaData)iterator.next();
         ModuleMetaData module = moduleService.getModuleData();
         
         String entryName = "portal:identity=Module,type=" + module.getType();
         AbstractBeanMetaData moduleBMD = new AbstractBeanMetaData(entryName,
            module.getClassName());
         AbstractPropertyMetaData propertyBMD = new AbstractPropertyMetaData("identityContext", identityContext);
         moduleBMD.addProperty(propertyBMD);
         propertyBMD = new AbstractPropertyMetaData("jndiBinder", getServiceJNDIBinder());
         moduleBMD.addProperty(propertyBMD);

         //initiate parameters from <config>
         for (Iterator iterator1 = moduleService.getModuleOptions().keySet().iterator(); iterator1.hasNext();)
         {
            String propertyKey = (String)iterator1.next();
            String propertyValue = (String)moduleService.getModuleOptions().get(propertyKey);
            //if (log.isDebugEnabled()) log.debug("adding parameter: " + propertyKey + " ; " + propertyValue);
            propertyBMD = new AbstractPropertyMetaData(propertyKey, propertyValue);
            moduleBMD.addProperty(propertyBMD);
         }

         //make the type from name
         propertyBMD = new AbstractPropertyMetaData("moduleType", module.getType());
         moduleBMD.addProperty(propertyBMD);


         // Installation
         //beans.add(moduleBMD);
         KernelControllerContext controllerContext = kernel.getController().install(moduleBMD);
         Object moduleServiceObject = controllerContext.getTarget();

         //make a part of identityContext
         //moduleService.setIdentityContext(identityContext);

         //register as an mbean
         if (isRegisterMBeans() && (module.getServiceName() != null))
         {
            unregisterMBean(module.getServiceName());
            registerMBean(module.getServiceName(), moduleServiceObject);

         }
      }

   }
   
   /**
    * Should be extended to provide mbean registration
    * @param serviceName
    * @param serviceObject
    */
   protected void unregisterMBean(String serviceName) throws Exception
   {
      //does nothing
   }

   /**
    * Should be extended to provide mbean registration
    * @param serviceName
    * @param serviceObject
    */
   protected void registerMBean(String serviceName, Object serviceObject) throws Exception
   {
      //does nothing
   }

   /**
    * Should be extended to provide JNDI binder
    * @return
    */
   protected ServiceJNDIBinder getServiceJNDIBinder() throws Exception
   {
      return null;   
   }

   public IdentityContext getIdentityContext()
   {
      return identityContext;
   }

   public Kernel getKernel()
   {
      return kernel;
   }

   public boolean isRegisterMBeans()
   {
      return registerMBeans;
   }
}
