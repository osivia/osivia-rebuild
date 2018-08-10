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

import org.jboss.xb.binding.GenericObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.jboss.portal.common.i18n.LocaleFormat;
import org.jboss.portal.common.util.ConversionException;
import org.xml.sax.Attributes;

import java.util.Locale;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class ProfileMetaDataFactory implements GenericObjectModelFactory
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(ProfileMetaDataFactory.class);

   public Object newRoot(Object object, UnmarshallingContext unmarshallingContext, String string, String string1, Attributes attributes)
   {
      return new ProfileMetaData();
   }

   public Object completeRoot(Object root, UnmarshallingContext unmarshallingContext, String string, String string1)
   {
      return root;
   }

   public Object newChild(Object root, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      if (root instanceof ProfileMetaData)
      {
         if ("property".equals(localName))
         {
            return new PropertyMetaData();
         }
      }
      else if (root instanceof PropertyMetaData)
      {
         if ("mapping".equals(localName))
         {
            return new PropertyMappingMetaData();
         }
         else if ("description".equals(localName))
         {
            String lang = attrs.getValue("xml:lang");
            LocalizedValueMetaData value = new LocalizedValueMetaData();
            if (lang != null)
            {
               try
               {
                  Locale locale = LocaleFormat.DEFAULT.getLocale(lang);
                  value.setLocale(locale);
               }
               catch (ConversionException e)
               {
                  log.error("Cannot obtain language value", e);
                  return null;
               }
            }
            return value;
         }
         else if ("display-name".equals(localName))
         {
            String lang = attrs.getValue("xml:lang");
            LocalizedValueMetaData value = new LocalizedValueMetaData();
            if (lang != null)
            {
               try
               {
                  Locale locale = LocaleFormat.DEFAULT.getLocale(lang);
                  value.setLocale(locale);
               }
               catch (ConversionException e)
               {
                  log.error("Cannot obtain language value", e);
                  return null;
               }
            }
            return value;
         }
      }
      else if (root instanceof PropertyMappingMetaData)
      {
         if ("database".equals(localName))
         {
            return new PropertyMappingDatabaseMetaData();
         }
         else if ("ldap".equals(localName))
         {
            return new PropertyMappingLDAPMetaData();
         }
      }
      return null;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext nav, String nsURI, String localName)
   {
      if (parent instanceof ProfileMetaData)
      {
         ProfileMetaData profile = (ProfileMetaData)parent;
         if (child instanceof PropertyMetaData)
         {
            profile.addProperty((PropertyMetaData)child);
         }
      }
      else if (parent instanceof PropertyMetaData)
      {
         PropertyMetaData describable = (PropertyMetaData)parent;
         if (child instanceof LocalizedValueMetaData)
         {
            if ("description".equals(localName))
            {
               describable.getDescription().getValues().add(child);
            }
            else if ("dispalay-name".equals(localName))
            {
               describable.getDisplayName().getValues().add(child);
            }
         }
         else if (child instanceof PropertyMappingMetaData)
         {
            describable.setMapping((PropertyMappingMetaData)child);
         }
      }
      else if (parent instanceof PropertyMappingMetaData)
      {
         PropertyMappingMetaData mapping = (PropertyMappingMetaData)parent;
         if (child instanceof PropertyMappingDatabaseMetaData)
         {
            mapping.setMappingDatabase((PropertyMappingDatabaseMetaData)child);
         }
         else if (child instanceof PropertyMappingLDAPMetaData)
         {
            mapping.setMappingLDAP((PropertyMappingLDAPMetaData)child);
         }
      }
   }

   public void setValue(Object object, UnmarshallingContext unmarshallingContext, String nsUri, String localName, String value)
   {
      if (object instanceof PropertyMetaData)
      {
         PropertyMetaData property = (PropertyMetaData)object;
         if ("name".equals(localName))
         {
            property.setName(value);
         }
         else if ("type".equals(localName))
         {
            property.setType(value);
         }
         else if ("access-mode".equals(localName))
         {
            property.setAccessMode(value);
         }
         else if ("usage".equals(localName))
         {
            property.setUsage(value);
         }
      }
      else if (object instanceof LocalizedValueMetaData)
      {
         LocalizedValueMetaData localized = (LocalizedValueMetaData)object;
         if ("description".equals(localName))
         {
            localized.setValue(value);
         }
         else if ("display-name".equals(localName))
         {
            localized.setValue(value);
         }
      }
      else if (object instanceof PropertyMappingDatabaseMetaData)
      {
         PropertyMappingDatabaseMetaData mapping = (PropertyMappingDatabaseMetaData)object;
         if ("type".equals(localName))
         {
            mapping.setType(value);
         }
         else if ("value".equals(localName))
         {
            mapping.setValue(value);
         }
      }
      else if (object instanceof PropertyMappingLDAPMetaData)
      {
         PropertyMappingLDAPMetaData mapping = (PropertyMappingLDAPMetaData)object;
         if ("value".equals(localName))
         {
            mapping.setValue(value);
         }
      }
   }
}
