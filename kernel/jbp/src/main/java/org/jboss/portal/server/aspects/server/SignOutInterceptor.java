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

import org.apache.log4j.Logger;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.web.RequestDispatchCallback;
import org.jboss.portal.web.ServletContainer;
import org.jboss.portal.web.ServletContainerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This interceptor implementation is used to keep track of all webapp the current user has used during its portal
 * session. When the invocation is tagged for a signout then it performs an additional task of invalidating the sessions
 * of the webapp that have been collected during the portal session as well as invalidating the current portal session.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class SignOutInterceptor extends ServerInterceptor
{

   /** . */
   private static final Invalidation invalidator = new Invalidation();

   /** . */
   private static final Logger log = Logger.getLogger(SignOutInterceptor.class);

   /** . */
   private static final String KEY = "org.jboss.portal.session.contexts";

   /** . */
   private static final ThreadLocal localContexts = new ThreadLocal()
   {
      protected Object initialValue()
      {
         return new HashSet();
      }
   };

   public static Set getSet()
   {
      return (Set)localContexts.get();
   }

   /** . */
   private ServletContainerFactory servletContainerFactory;

   public ServletContainerFactory getServletContainerFactory()
   {
      return servletContainerFactory;
   }

   public void setServletContainerFactory(ServletContainerFactory servletContainerFactory)
   {
      this.servletContainerFactory = servletContainerFactory;
   }

   protected void invoke(ServerInvocation invocation) throws Exception, InvocationException
   {
      try
      {
         getSet().clear();

         //
         invocation.invokeNext();
      }
      finally
      {
         after(invocation);
      }
   }

   private void after(ServerInvocation invocation)
   {
      // Put the contexts that have been used during this invocation into the global set
      ServerInvocationContext context = invocation.getServerContext();
      HttpSession session = context.getClientRequest().getSession();
      Set contexts = (Set)session.getAttribute(KEY);
      if (contexts == null)
      {
         contexts = new HashSet();
         session.setAttribute(KEY, contexts);
      }
      contexts.addAll(getSet());
      getSet().clear();

      // If the invocation has been set to signout then perform the invalidations
      if (invocation.getResponse().getWantSignOut())
      {
         // Get portal context
         ServletContext portalContext = session.getServletContext();

         //
         HttpServletRequest req = invocation.getServerContext().getClientRequest();

         //
         HttpServletResponse resp = invocation.getServerContext().getClientResponse();

         // Iterate over all the context that have been used
         for (Iterator i = contexts.iterator(); i.hasNext();)
         {
            String dispatchContextName = (String)i.next();

            // Get the context
            ServletContext dispatchContext = portalContext.getContext(dispatchContextName);

            // The context could be null if the web app has been removed after the web app has been tracked
            if (dispatchContext != null)
            {
               try
               {
                  ServletContainer servletContainer = servletContainerFactory.getServletContainer();

                  // Execute the command that invalidates the session
                  servletContainer.include(dispatchContext, req, resp, invalidator, null);
               }
               catch (Exception e)
               {
                  log.error("An error occured when trying to invalidate the session", e);
               }
            }
         }

         //Finally invalidate this session
         try
         {
            session.invalidate();
         }
         catch (IllegalStateException stateException)
         {
            if (stateException.toString().indexOf("invalidate: Session already invalidated") != -1)
            {
               //This means Tomcat SSO is probably turned on and part of invalidating the other war context's
               //session, this Portal's session has been invalidated as well

               //No need to do anything
            }
            else
            {
               //This is a real issue, don't eat this
               throw stateException;
            }
         }

         // Set information about logout 
         req.setAttribute("org.jboss.portal.logout", "true");
      }
   }

   public static class Invalidation implements RequestDispatchCallback
   {
      public Object doCallback(ServletContext servletContext, HttpServletRequest req, HttpServletResponse resp, Object handback) throws ServletException, IOException
      {
         HttpSession session = req.getSession(false);

         //
         if (session != null)
         {
            session.invalidate();
         }

         //
         return null;
      }
   }
}
