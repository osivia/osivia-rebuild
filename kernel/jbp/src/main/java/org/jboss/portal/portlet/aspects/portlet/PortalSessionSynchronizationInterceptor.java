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

import org.apache.log4j.Logger;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.session.SubSession;
import org.jboss.portal.portlet.spi.PortletInvocationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11077 $
 */
public class PortalSessionSynchronizationInterceptor extends PortletInvokerInterceptor
{

   /** . */
   private final Logger log = Logger.getLogger(PortalSessionSynchronizationInterceptor.class);

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      HttpServletRequest req = null;

      // We try to access the client request whenever this is possible
      // This should be better implemented and the task of using the portal session
      // Should be left up to the caller whenever it is possible
      try
      {
         PortletInvocationContext ctx = invocation.getContext();
         Method getClientRequestMethod = ctx.getClass().getMethod("getClientRequest", new Class[0]);
         req = (HttpServletRequest)getClientRequestMethod.invoke(ctx, new Object[0]);
      }
      catch (Exception ignore)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Was not able to access the client request from request context");
         }
      }

      if (req != null)
      {
         String id = invocation.getInstanceContext().getId();
         String key = "jboss.portlet.session." + id;

         // Try to get a sub session from the portal session
         SubSession ss = null;
         HttpSession session = req.getSession(false);
         if (session != null)
         {
            ss = (SubSession)session.getAttribute(key);
         }

         //
         try
         {
            // Set the sub session for the portlet synchronization
            if (ss != null && ss.isActivated())
            {
               invocation.setAttribute("subsession", ss);
            }

            //
            return super.invoke(invocation);
         }
         finally
         {
            List modifications = (List)invocation.getAttribute("subsession");

            // If we have any modifications propagate them
            if (modifications != null)
            {
               //
               invocation.removeAttribute("subsession");

               //
               if (ss == null)
               {
                  ss = new SubSession(key);
               }

               //
               ss.synchronizeWithPortalSession(req, modifications, key);
            }
         }
      }
      else
      {
         return super.invoke(invocation);
      }
   }
}
