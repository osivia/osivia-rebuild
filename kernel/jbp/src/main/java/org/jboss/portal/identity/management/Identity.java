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
package org.jboss.portal.identity.management;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.RoleModule;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Identity implements IdentityMBean
{
   private RoleModule roleModule;
   
   private UserModule userModule;
   
   public int getUserCount() throws Exception
   {
      if (userModule == null)
      {
         InitialContext ctx;
         try
         {
            ctx = new InitialContext();
            userModule = (UserModule)ctx.lookup("java:/portal/UserModule");
         }
         catch (NamingException e)
         {
            e.printStackTrace();
         }
      }
      
      int nbUsers = 0;
      InitialContext ctx;
      ctx = new InitialContext();
      UserTransaction tx = (UserTransaction)ctx.lookup("UserTransaction");
      tx.begin();
      nbUsers = userModule.getUserCount();
      tx.commit();
      return nbUsers;
   }

   public int getRoleCount() throws Exception
   {
      if (roleModule == null)
      {
         InitialContext ctx;
         try
         {
            ctx = new InitialContext();
            roleModule = (RoleModule)ctx.lookup("java:/portal/RoleModule");
         }
         catch (NamingException e)
         {
            e.printStackTrace();
         }
      }
      int nbRoles = 0;
      InitialContext ctx;
      ctx = new InitialContext();
      UserTransaction tx = (UserTransaction)ctx.lookup("UserTransaction");
      tx.begin();
      nbRoles = roleModule.getRolesCount();
      tx.commit();
      return nbRoles;
   }
}
