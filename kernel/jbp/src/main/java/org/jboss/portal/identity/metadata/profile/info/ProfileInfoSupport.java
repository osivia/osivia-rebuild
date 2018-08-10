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

import org.jboss.portal.identity.info.ProfileInfo;
import org.jboss.portal.identity.info.PropertyInfo;
import org.jboss.portal.identity.metadata.profile.ProfileMetaData;
import org.jboss.portal.identity.metadata.profile.PropertyMetaData;
import org.jboss.portal.identity.IdentityException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class ProfileInfoSupport implements ProfileInfo
{
   private Map properties;


   public ProfileInfoSupport(ProfileMetaData profile) throws IdentityException
   {

      try
      {
         properties = new HashMap();
         Map meta = profile.getProperties();
         for (Iterator iterator = meta.keySet().iterator(); iterator.hasNext();)
         {
            String name = (String)iterator.next();
            PropertyMetaData property = (PropertyMetaData)meta.get(name);
            PropertyInfo pi = new PropertyInfoSupport(property);
            properties.put(pi.getName(), pi);
         }
      }
      catch (IdentityException e)
      {
         throw new IdentityException("PrifileInfo creation error: ", e);
      }
   }

   public Map getPropertiesInfo()
   {
       if (properties != null)
      {
         return Collections.unmodifiableMap(properties);
      }
      else
      {
         return Collections.EMPTY_MAP;
      }
   }

   public PropertyInfo getPropertyInfo(String name)
   {
      if (name != null)
      {
         return (PropertyInfo)properties.get(name);
      }
      return null;  
   }
}
