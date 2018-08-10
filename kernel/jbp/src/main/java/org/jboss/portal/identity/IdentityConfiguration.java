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
package org.jboss.portal.identity;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public interface IdentityConfiguration
{

   public static final String GROUP_COMMON = "common";

   public static final String GROUP_CONNECTION = "connection";

   public static final String GROUP_USER_CREATE_ATTRIBUTES = "userCreateAttibutes";

   public static final String GROUP_ROLE_CREATE_ATTRIBUTES = "roleCreateAttibutes";

   public static final String GROUP_USER_PROFILE_MAPPINGS = "userProfileMappings";

   public static final String CONNECTION_NAME = "connection-name";

   public static final String CONNECTION_CONTEXT_FACTORY = "context-factory";

   public static final String CONNECTION_HOST = "host";

   public static final String CONNECTION_PORT = "port";

   public static final String CONNECTION_ADMIN_DN = "admin-dn";

   public static final String CONNECTION_ADMIN_PASSWORD = "admin-password";

   public static final String CONNECTION_AUTHENTICATION = "authentication";

   public static final String USER_PRINCIPAL_PREFIX = "principalDNPrefix";

   public static final String USER_PRINCIPAL_SUFFIX = "principalDNSuffix";

   public static final String USER_UID_ATTRIBUTE_ID = "uidAttributeID";

   public static final String USER_PASSWORD_ATTRIBUTE_ID = "passwordAttributeID";

   public static final String USER_PASSWORD_ENCLOSE_WITH = "enclosePasswordWith";

   public static final String USER_PASSWORD_ENCODING = "passwordEncoding";

   public static final String USER_PASSWORD_UPDATE_ATTRIBUTES = "passwordUpdateAttributeValues";

   public static final String USER_SET_PASSWORD_AFTER_USER_CREATE = "setPasswordAfterUserCreate";

   public static final String USER_CONTEXT_DN = "userCtxDN";

   public static final String USER_CONTAINER_DN = USER_CONTEXT_DN;

   public static final String USER_SEARCH_FILTER = "userSearchFilter";

   public static final String USER_ALLOW_EMPTY_PASSWORDS = "allowEmptyPasswords";

   public static final String USER_USER_NAME_TO_LOWER_CASE = "userNameToLowerCase";

   //public static final String ROLE_CONTAINER_DN = "roleContainerDN";

   public static final String ROLE_RID_ATTRIBUTE_ID = "ridAttributeID";

   public static final String ROLE_DISPLAY_NAME_ATTRIBUTE_ID = "roleDisplayNameAttributeID";

   public static final String ROLE_SEARCH_FILTER = "roleSearchFilter";

   public static final String ROLE_CONTEXT_DN = "roleCtxDN";

   public static final String ROLE_CONTAINER_DN = ROLE_CONTEXT_DN;

   public static final String ROLE_DEFAULT_ADMIN_ROLE = "defaultAdminRole";

   public static final String MEMBERSHIP_ATTRIBUTE_ID = "membershipAttributeID";

   public static final String MEMBERSHIP_ATTRIBUTE_IS_DN = "membershipAttributeIsDN";

   public static final String MEMBERSHIP_MEMBERSHIP_ATTRIBUTE_REQUIRED = "membershipAttributeRequired";

   public static final String MEMBERSHIP_MEMBERSHIP_ATTRIBUTE_EMPTY_VALUE = "membershipAttributeEmptyValue";

   public static final String SEARCH_TIME_LIMIT = "searchTimeLimit";

   public static final String SEARCH_SCOPE = "searchScope";



   public Set getValues(String optionGroup, String option);

   public String getValue(String optionGroup, String option);

   public String getValue(String option);

   public void setValues(String optionGroup, String option, Set values);

   public void addValue(String optionGroup, String option, String value);

   public Map getOptions(String optionGroup);

   public void setOptions(String optionGroup, Map options);

   public void remoeOption(String optionGroup, String option);

   public Map getOptionGroups();


}
