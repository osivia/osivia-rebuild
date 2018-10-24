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

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class OptionsMetaData
{
   private Map groups;


   public OptionsMetaData()
   {
      groups = new HashMap();
   }


   public Map getGroups()
   {
      return groups;
   }

   public void setGroups(Map groups)
   {
      this.groups = groups;
   }

   public void addGroup(OptionsGroupMetaData group)
   {
      groups.put(group.getName(), group);
   }

   public OptionsGroupMetaData getGroup(String name)
   {
      return (OptionsGroupMetaData)groups.get(name);
   }
}