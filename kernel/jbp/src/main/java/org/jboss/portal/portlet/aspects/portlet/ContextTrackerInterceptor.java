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
package org.jboss.portal.portlet.aspects.portlet;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * This interceptor get the current webapp context path and adds it to the set of the collected context path.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11077 $
 */
public class ContextTrackerInterceptor extends PortletInvokerInterceptor
{
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      //
      String contextPath = (String)invocation.getDispatchedRequest().getAttribute("javax.servlet.include.context_path");

      // Add it to the request context path set
      org.jboss.portal.server.aspects.server.SignOutInterceptor.getSet().add(contextPath);

      // Invoke next command
      return super.invoke(invocation);
   }
}
