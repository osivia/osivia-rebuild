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
package org.jboss.portal.identity.metadata.profile.info;

import org.jboss.portal.identity.info.PropertyInfo;
import org.jboss.portal.identity.metadata.profile.PropertyMetaData;
import org.jboss.portal.identity.metadata.profile.LocalizedValueMetaData;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.identity.IdentityException;
import org.jboss.logging.Logger;

import java.util.Locale;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class PropertyInfoSupport implements PropertyInfo
{
   private static final Logger log = Logger.getLogger(PropertyInfo.class);

   //TODO: introduce safe types enums where possible.

   private String name;
   private String type;
   private String accessMode;
   private String usage;
   private LocalizedString displayName;
   private LocalizedString description;
   private String mappingDBType;
   private String mappingDBValue;
   private String mappingLDAPValue;
   private boolean mappedLDAP;
   private boolean mappedDB;


   public PropertyInfoSupport(PropertyMetaData meta) throws IdentityException
   {
      


      name = meta.getName();
      type = meta.getType();
      accessMode = meta.getAccessMode();
      if (!accessMode.equals(PropertyInfo.ACCESS_MODE_READ_ONLY) && !accessMode.equals(PropertyInfo.ACCESS_MODE_READ_WRITE))
      {
         throw new IdentityException("Wrong value in user profile configuration for access-mode: " + accessMode);
      }
      usage = meta.getUsage();

      if (!usage.equals(PropertyInfo.USAGE_MANDATORY) && !usage.equals(PropertyInfo.USAGE_OPTIONAL))
      {
         throw new IdentityException("Wrong value in user profile configuration for usage: " + usage);
      }



      //mappingType = meta.getMapping().getType();
      //mappingValue = meta.getMapping().getValue();



      Map descValues = new HashMap();
      for (Iterator iterator = meta.getDescription().getValues().iterator(); iterator.hasNext();)
      {
         LocalizedValueMetaData value = (LocalizedValueMetaData)iterator.next();
         descValues.put(value.getLocale(),value.getValue());
      }
      Map dispValues = new HashMap();
      for (Iterator iterator = meta.getDisplayName().getValues().iterator(); iterator.hasNext();)
      {
         LocalizedValueMetaData value = (LocalizedValueMetaData)iterator.next();
         dispValues.put(value.getLocale(),value.getValue());
      }

      description = new LocalizedString(descValues, Locale.ENGLISH);
      displayName = new LocalizedString(dispValues, Locale.ENGLISH);

      if (meta.getMapping() == null)
      {
         throw new IdentityException("Mapping section is missing");
      }
      if (meta.getMapping().getMappingDatabase() != null)
      {
         if (meta.getMapping().getMappingDatabase().getType() != null && meta.getMapping().getMappingDatabase().getValue() != null)
         {
            mappedDB = true;
            mappingDBType = meta.getMapping().getMappingDatabase().getType();
            if (!mappingDBType.equals(PropertyInfo.MAPPING_DB_TYPE_COLUMN) && !mappingDBType.equals(PropertyInfo.MAPPING_DB_TYPE_DYNAMIC))
            {
               throw new IdentityException("Wrong value in user profile configuration for database mapping type: " + mappingDBType);
            }
            mappingDBValue = meta.getMapping().getMappingDatabase().getValue();
         }
      }

      if (meta.getMapping().getMappingLDAP() != null)
      {
         if (meta.getMapping().getMappingLDAP().getValue() != null)
         {
            mappedLDAP = true;
            mappingLDAPValue = meta.getMapping().getMappingLDAP().getValue();
         }
      }

      if (log.isDebugEnabled())
      {
         log.debug("created PropertyInfo: " + toString());
      }
   }

   public String getName()
   {
      return name;
   }

   public String getType()
   {
      return type;
   }

   public String getAccessMode()
   {
      return accessMode;
   }

   public String getUsage()
   {
      return usage;
   }

   public LocalizedString getDisplayName()
   {
      return displayName;
   }

   public LocalizedString getDescription()
   {
      return description;
   }

   public String getMappingDBType()
   {
      return mappingDBType;
   }

   public String getMappingLDAPValue()
   {
      return mappingLDAPValue;
   }

   public String getMappingDBValue()
   {
      return mappingDBValue;
   }


   public boolean isMappedDB()
   {
      return mappedDB;
   }

   public boolean isMappedLDAP()
   {
      return mappedLDAP;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("name: ").append(name)
         .append("; type:").append(type)
         .append("; accessMode:").append(accessMode)
         .append("; usage: ").append(usage)
         .append("; displayName: ").append(displayName)
         .append("; description: ").append(description)
         .append("; mappingDBType: ").append(mappingDBType)
         .append("; mappingLDAPValue: ").append(mappingLDAPValue)
         .append("; mappingDBValue: ").append(mappingDBValue)
         .append("; mappedDB: ").append(mappedDB)
         .append("; mappedLDAP: ").append(mappedLDAP);
      return buf.toString();
   }
}
