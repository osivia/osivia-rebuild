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
package org.jboss.portal.server.servlet;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class CommandFilter implements Filter
{

   private static final Logger log = Logger.getLogger(CommandFilter.class);

   public void init(FilterConfig cfg) throws ServletException
   {
   }

   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
   {
      Object cmd = req.getAttribute(FilterCommand.REQ_ATT_KEY);
      if (cmd != null)
      {
         try
         {
            req.removeAttribute(FilterCommand.REQ_ATT_KEY);
            Method methods = cmd.getClass().getMethod(
               "execute",
               new Class[]{
                  HttpServletRequest.class,
                  HttpServletResponse.class,
                  FilterChain.class});
            methods.invoke(cmd, new Object[]{req, resp, chain});
         }
         catch (NoSuchMethodException e)
         {
            throw new ServletException("No execute method found on the filter command", e);
         }
         catch (InvocationTargetException e)
         {
            // Log the wrappee
            Throwable wrappee = e.getTargetException();
            log.error("Exception in command invocation", wrappee);

            // Rethrow the checked ServletException
            if (wrappee instanceof ServletException)
            {
               ServletException se = (ServletException)wrappee;
               if (se.getCause() == null && se.getRootCause() != null)
               {
                  se.initCause(se.getRootCause());
               }
               throw (ServletException)wrappee;
            }
            // Rethrow the checked IOException
            if (wrappee instanceof IOException)
            {
               throw (IOException)wrappee;
            }
            // Rethrow RuntimeException
            if (wrappee instanceof RuntimeException)
            {
               throw (RuntimeException)wrappee;
            }
            // Rethrow Error
            if (wrappee instanceof Error)
            {
               throw (Error)wrappee;
            }
            // Here we wrap it and rethrow
            ServletException se = new ServletException("The invoked command threw an exception", wrappee);
            se.initCause(wrappee);
            throw se;
         }
         catch (IllegalAccessException e)
         {
            ServletException se = new ServletException("Unexpected IllegalAccessException during command invocation", e);
            se.initCause(e);
            throw se;
         }
      }
      else
      {
         // Just invoke the next in the chain
         chain.doFilter(req, resp);
      }
   }

   public void destroy()
   {
   }
}
