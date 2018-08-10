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
package org.jboss.portal.identity.ldap;

import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.service.MembershipModuleService;
import org.jboss.portal.identity.IdentityException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class LDAPMembershipModule extends MembershipModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPMembershipModule.class);

   private LDAPConnectionContext connectionContext;

   private LDAPUserModule userModule;

   private LDAPRoleModule roleModule;

   public void start() throws Exception
   {
      if (getConnectionJNDIName() == null)
      {
         throw new IdentityException("Cannot obtain ldap connection context JNDI name");
      }

      try
      {
         connectionContext = (LDAPConnectionContext)new InitialContext().lookup(getConnectionJNDIName());
      }
      catch (NamingException e)
      {
         log.error("Couldn't obtain connection context");
      }

      super.start();    //To change body of overridden methods use File | Settings | File Templates.
   }

   //************************************
   //******* Getters and Setters ********
   //************************************

   protected LDAPConnectionContext getConnectionContext() throws IdentityException
   {
      if (connectionContext == null)
      {
         try
         {
            this.connectionContext = (LDAPConnectionContext)getIdentityContext().getObject(IdentityContext.TYPE_CONNECTION_CONTEXT);
         }
         catch (IdentityException e)
         {
            throw new IdentityException("No LDAPConnectionContext available");
         }
      }
      return connectionContext;
   }



   protected LDAPUserModule getUserModule() throws IdentityException
   {

      if (userModule == null)
      {
         try
         {
            this.userModule = (LDAPUserModule)getIdentityContext().getObject(IdentityContext.TYPE_USER_MODULE);
         }
         catch (ClassCastException e)
         {
            throw new IdentityException("Not supported object as part of the context - must be LDAPUserModule", e);
         }
      }
      return userModule;
   }

   protected LDAPRoleModule getRoleModule() throws IdentityException
   {

      if (roleModule == null)
      {
         try
         {
            this.roleModule = (LDAPRoleModule)getIdentityContext().getObject(IdentityContext.TYPE_ROLE_MODULE);
         }
         catch (ClassCastException e)
         {
            throw new IdentityException("Not supported object as part of the context", e);
         }
      }
      return roleModule;
   }

   protected String getMemberAttributeID() throws IdentityException
   {
      String uid = getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_ATTRIBUTE_ID);
      if (uid == null)
      {
         return "member";
      }
      return uid;
   }

   protected boolean isUidAttributeIsDN() throws IdentityException
   {
      if (getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_ATTRIBUTE_IS_DN) == null)
      {
         return true;
      }

      return getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_ATTRIBUTE_IS_DN).equals("true");
   }

   protected boolean isMembershipAttributeRequired() throws IdentityException
   {
      if (getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_MEMBERSHIP_ATTRIBUTE_REQUIRED) == null)
      {
         return true;
      }

      return getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_MEMBERSHIP_ATTRIBUTE_REQUIRED).equals("true");
   }

   protected String getMembershipAttributeValue() throws IdentityException
   {
      String value = getIdentityConfiguration().getValue(IdentityConfiguration.MEMBERSHIP_ATTRIBUTE_ID);
      if (value == null)
      {
         return "cn=emptyMembershipValue";
      }
      return value;
   }

   public void setConnectionContext(LDAPConnectionContext connectionContext)
   {
      this.connectionContext = connectionContext;
   }

}
