/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.identity.ldap;

import org.jboss.portal.identity.service.UserProfileModuleService;
import org.jboss.portal.identity.IdentityException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class LDAPUserProfileModule extends UserProfileModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LDAPUserProfileModule.class);

   private LDAPConnectionContext connectionContext;

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

   protected LDAPConnectionContext getConnectionContext() throws IdentityException
   {
      if (connectionContext == null)
      {
         //this.connectionContext = (LDAPConnectionContext)getIdentityContext().getObject(IdentityContext.TYPE_CONNECTION_CONTEXT);
         throw new IdentityException("No LDAPConnectionContext available");
      }
      return connectionContext;
   }

   public void setConnectionContext(LDAPConnectionContext connectionContext)
   {
      this.connectionContext = connectionContext;
   }

}
