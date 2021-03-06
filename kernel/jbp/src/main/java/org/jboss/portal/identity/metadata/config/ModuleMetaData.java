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

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class ModuleMetaData
{
   private String type;
   private String implementation;
   private String serviceName;
   private String className;
   //private String jndiName;
   private ConfigMetaData config;


   public ModuleMetaData()
   {
   }


   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getImplementation()
   {
      return implementation;
   }

   public void setImplementation(String implementation)
   {
      this.implementation = implementation;
   }

   public String getServiceName()
   {
      return serviceName;
   }

   public void setServiceName(String serviceName)
   {
      this.serviceName = serviceName;
   }

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   public ConfigMetaData getConfig()
   {
      return config;
   }

   public void setConfig(ConfigMetaData config)
   {
      this.config = config;
   }
   
   public String toString()
   {
      return "ModuleMetaData[type=" + type + ", implementation= " + implementation + 
             ", serviceName=" + serviceName + ", className=" + className + "config=" + config + "]";
   }
}
