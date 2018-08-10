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
package org.jboss.portal.identity.metadata.profile;


/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class PropertyMetaData
{
   private String name;
   private String type;
   private String accessMode;
   private String usage;
   private LocalizedStringMetaData displayName;
   private LocalizedStringMetaData description;
   private PropertyMappingMetaData mapping;


   public PropertyMetaData()
   {
      displayName= new LocalizedStringMetaData();
      description = new LocalizedStringMetaData();
   }


   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getAccessMode()
   {
      return accessMode;
   }

   public void setAccessMode(String accessMode)
   {
      this.accessMode = accessMode;
   }

   public String getUsage()
   {
      return usage;
   }

   public void setUsage(String usage)
   {
      this.usage = usage;
   }

   public LocalizedStringMetaData getDisplayName()
   {
      return displayName;
   }

   public void setDisplayName(LocalizedStringMetaData displayName)
   {
      this.displayName = displayName;
   }

   public LocalizedStringMetaData getDescription()
   {
      return description;
   }

   public void setDescription(LocalizedStringMetaData description)
   {
      this.description = description;
   }

   public PropertyMappingMetaData getMapping()
   {
      return mapping;
   }

   public void setMapping(PropertyMappingMetaData mapping)
   {
      this.mapping = mapping;
   }
}
