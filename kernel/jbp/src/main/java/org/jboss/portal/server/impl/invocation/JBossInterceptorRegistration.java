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
package org.jboss.portal.server.impl.invocation;

import org.jboss.portal.jems.as.system.AbstractJBossService;

import javax.management.ObjectName;

/**
 * Register an interceptor on a stack factory. The main usage is for dynamic registration of an interceptor when the
 * interceptor cannot be specified in the stack factory dependencies.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossInterceptorRegistration extends AbstractJBossService
{

   /** . */
   private ObjectName interceptorName;

   /** . */
   private ObjectName stackFactory;

   public ObjectName getInterceptorName()
   {
      return interceptorName;
   }

   public void setInterceptorName(ObjectName interceptorName)
   {
      this.interceptorName = interceptorName;
   }

   public ObjectName getStackFactory()
   {
      return stackFactory;
   }

   public void setStackFactory(ObjectName stackFactory)
   {
      this.stackFactory = stackFactory;
   }

   protected void startService() throws Exception
   {
      try
      {
         server.invoke(
            stackFactory,
            "addInterceptor",
            new Object[]{interceptorName},
            new String[]{ObjectName.class.getName()});
      }
      finally
      {
         rebuildStackFactory();
      }
   }

   protected void stopService() throws Exception
   {
      try
      {
         server.invoke(
            stackFactory,
            "removeInterceptor",
            new Object[]{interceptorName},
            new String[]{ObjectName.class.getName()});
      }
      finally
      {
         rebuildStackFactory();
      }
   }

   private void rebuildStackFactory()
   {
      try
      {
         server.invoke(
            stackFactory,
            "rebuild",
            new Object[0],
            new String[0]);
      }
      catch (Exception e)
      {
         log.warn("Exception during rebuild of stack factory", e);
      }
   }
}

