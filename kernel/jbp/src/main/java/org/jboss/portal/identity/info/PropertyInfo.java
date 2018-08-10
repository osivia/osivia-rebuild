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
package org.jboss.portal.identity.info;

import org.jboss.portal.common.i18n.LocalizedString;


/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public interface PropertyInfo
{

   //TODO: make it int or safe type enum 
   public static final String ACCESS_MODE_READ_ONLY = "read-only";
   public static final String ACCESS_MODE_READ_WRITE = "read-write";
   public static final String USAGE_MANDATORY = "mandatory";
   public static final String USAGE_OPTIONAL = "optional";
   public static final String MAPPING_DB_TYPE_COLUMN = "column";
   public static final String MAPPING_DB_TYPE_DYNAMIC = "dynamic";

   /**
    * Returns property name
    * @return
    */
   public String getName();

   /**
    * Returns property type
    * @return
    */
   public String getType();

   /**
    * Returns property access mode
    * @return
    */
   public String getAccessMode();

   /**
    * Returns property usage
    * @return
    */
   public String getUsage();

   /**
    * Returns property display name
    * @return
    */
   public LocalizedString getDisplayName();

   /**
    * Returns property description
    * @return
    */
   public LocalizedString getDescription();

   /**
    * Returns type of property database mapping
    * @return
    */
   public String getMappingDBType();

   /**
    * Returns name of LDAP attribue to which this property corresponds
    * @return
    */
   public String getMappingLDAPValue();

   /**
    * Returns name of database column name to which this property corresponds
    * @return
    */
   public String getMappingDBValue();

   /**
    * If property is mapped in database
    * @return
    */
   public boolean isMappedDB();

   /**
    * If property is mapped in LDAP
    * @return
    */
   public boolean isMappedLDAP();


}
