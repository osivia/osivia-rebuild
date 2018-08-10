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
import org.jboss.portal.portlet.container.PortletContainer;
import org.jboss.portal.portlet.container.ContainerPortletInvoker;
import org.jboss.portal.portlet.deployment.jboss.info.SessionInfo;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.session.SessionListener;
import org.jboss.portal.portlet.session.SubSession;

import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11077 $
 */
public class PortletSessionSynchronizationInterceptor extends PortletInvokerInterceptor
{
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      PortletContainer container = (PortletContainer)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
      PortletInfo portletInfo = container.getInfo();
      SessionInfo sessionInfo = portletInfo.getAttachment(SessionInfo.class);

      if (sessionInfo != null && Boolean.TRUE.equals(sessionInfo.getDistributed()))
      {
         SubSession ss = (SubSession)invocation.getAttribute("subsession");

         // If we detect an activation then we copy the content in the dispatched session
         if (ss != null)
         {
            ss.synchronizeWithDispatchedSession(invocation.getDispatchedRequest());
         }

         try
         {

            // Associate with thread after than the synchronization has been done
            // because we don't want to have the http session listener modify the sub session
            SessionListener.activate();

            //
            return super.invoke(invocation);
         }
         finally
         {
            // Clear association with thread
            List modifications = SessionListener.desactivate();

            // Set modifications for portal session synchronization
            invocation.setAttribute("subsession", modifications);
         }
      }
      else
      {
         return super.invoke(invocation);
      }
   }
}
