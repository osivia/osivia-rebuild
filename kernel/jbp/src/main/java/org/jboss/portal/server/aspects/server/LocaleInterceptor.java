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
package org.jboss.portal.server.aspects.server;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerRequest;

import java.util.Locale;

/**
 * The goal of this interceptor is to choose the locale for the user. This implementation is very simple as it reuses
 * the locale specified in the incominh HTTP request.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class LocaleInterceptor extends ServerInterceptor
{
   protected void invoke(ServerInvocation invocation) throws Exception, InvocationException
   {
      ServerRequest req = invocation.getRequest();
      ServerInvocationContext context = invocation.getServerContext();
      Locale locale = context.getClientRequest().getLocale();
      try
      {
         req.setLocales(new Locale[]{locale});
         invocation.invokeNext();
      }
      finally
      {
         req.setLocales(null);
      }
   }
}
