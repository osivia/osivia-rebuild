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
package org.jboss.portal.server;

import org.jboss.portal.common.invocation.Invocation;
import org.jboss.portal.common.invocation.InvocationContext;
import org.jboss.portal.common.invocation.Scope;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ServerInvocation extends Invocation
{

   /** . */
   public static final Scope PRINCIPAL_SCOPE = Scope.PRINCIPAL_SCOPE;

   /** . */
   public static final Scope SESSION_SCOPE = Scope.SESSION_SCOPE;

   /** . */
   public static final Scope REQUEST_SCOPE = Scope.REQUEST_SCOPE;

   /** The request. */
   private ServerRequest req;

   /** The response. */
   private ServerResponse resp;

   /** The invocation context. */
   private ServerInvocationContext ctx;

   public ServerInvocation(ServerInvocationContext ctx)
   {
      if (ctx == null)
      {
         throw new IllegalArgumentException();
      }
      this.ctx = ctx;
   }

   public ServerInvocationContext getServerContext()
   {
      return ctx;
   }

   public InvocationContext getContext()
   {
      return ctx;
   }

   public ServerRequest getRequest()
   {
      return req;
   }

   public void setRequest(ServerRequest req)
   {
      this.req = req;
   }

   public ServerResponse getResponse()
   {
      return resp;
   }

   public void setResponse(ServerResponse resp)
   {
      this.resp = resp;
   }
}
