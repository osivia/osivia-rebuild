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
import org.jboss.portal.common.invocation.InterceptorStackFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.common.util.Exceptions;
import org.jboss.portal.server.RequestController;
import org.jboss.portal.server.RequestControllerDispatcher;
import org.jboss.portal.server.RequestControllerFactory;
import org.jboss.portal.server.Server;
import org.jboss.portal.server.ServerException;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.server.ServerResponse;
import org.jboss.portal.server.impl.ServerInvocationContextImpl;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.web.WebRequest;
import org.jboss.portal.web.endpoint.EndPointRequest;
import org.jboss.portal.web.endpoint.EndPointServlet;
import org.osivia.portal.api.locator.Locator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The main servlet of the portal. This servlet must be properly configured with the servlet mapping style it is
 * mapped.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11068 $
 */
public class PortalServlet extends HttpServlet
{

   /** Describes a default servlet mapping. */
   private static final int DEFAULT_SERVLET_MAPPING = 0;

   /** Describes a root path mapping. */
   private static final int ROOT_PATH_MAPPING = 1;

   /** Describes a path mapping. */
   private static final int PATH_MAPPING = 2;

   /** The logger. */
   protected Logger log = Logger.getLogger(getClass());

   /** The server. */
   private Server server;

   /** The interceptor stack. */
   private InterceptorStackFactory interceptorStack;

   /** Are we or not the default servlet ? */
   private boolean asDefaultServlet;

   /** The controller for this servlet. */
   private RequestControllerFactory controllerFactory;

   /** The controller name. */
   private String controllerFactoryName;

   /** Configure the as default servlet. */
   public void init() throws ServletException
   {
      asDefaultServlet = getAsDefaultServletInitValue();
      controllerFactoryName = getServletConfig().getInitParameter("controllerFactoryName");
   }

   @Autowired
   private ApplicationContext applicationContext;
   
   /**
    *
    */
   protected final Server getServer()
   {
      if (server == null)
      {
         try
         {
            server = Locator.getService("portal:service=Server",Server.class);
         }
         catch (Exception e)
         {
            String msg = "Cannot get portal server";
            log.error(msg, e);
            throw new IllegalStateException(msg);
         }
      }
      return server;
   }

   protected final InterceptorStackFactory getInterceptorStackFactory()
   {
      if (interceptorStack == null)
      {
         try
         {
            interceptorStack = Locator.getService( "portal:service=InterceptorStackFactory,type=Server", InterceptorStackFactory.class);
         }
         catch (Exception e)
         {
            String msg = "Cannot get interceptor stack";
            log.error(msg, e);
            throw new IllegalStateException(msg);
         }
      }
      return interceptorStack;
   }

   protected final RequestControllerFactory getControllerFactory()
   {
      if (controllerFactory == null)
      {
         try
         {
            controllerFactory = (RequestControllerFactory)Locator.getService( controllerFactoryName, RequestControllerFactory.class);
         }
         catch (Exception e)
         {
            String msg = "Cannot get controller " + controllerFactoryName;
            log.error(msg, e);
            throw new IllegalStateException(msg);
         }
      }
      return controllerFactory;
   }

   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      //
      String servletPath = req.getServletPath();
      String requestURI = req.getRequestURI();
      String contextPath = req.getContextPath();

      // Determine the mapping we have
      int mapping = DEFAULT_SERVLET_MAPPING;
      if (!asDefaultServlet)
      {
         if (servletPath.length() == 0)
         {
            mapping = ROOT_PATH_MAPPING;
         }
         else
         {
            mapping = PATH_MAPPING;
         }
      }

      // Determine the host for this request
      String portalHost = req.getServerName();

      // Determine the request path
      String portalRequestPath = null;
      String portalContextPath = null;
      switch (mapping)
      {
         case DEFAULT_SERVLET_MAPPING:
            portalRequestPath = requestURI.substring(contextPath.length());
            portalContextPath = requestURI.substring(0, contextPath.length());
            break;
         case ROOT_PATH_MAPPING:
            portalRequestPath = requestURI.substring(contextPath.length());
            portalContextPath = requestURI.substring(0, contextPath.length());
            break;
         case PATH_MAPPING:
            portalRequestPath = requestURI.substring(contextPath.length() + servletPath.length());
            portalContextPath = requestURI.substring(0, contextPath.length() + servletPath.length());
            break;
      }

      // Apply the url decoding
      portalRequestPath = URLTools.decodeXWWWFormURL(portalRequestPath);
      portalContextPath = URLTools.decodeXWWWFormURL(portalContextPath);

      //
      URLContext urlContext = URLContext.newInstance(req.isSecure(), req.getRemoteUser() != null);

      //
      WebRequest webReq = new EndPointRequest(req, portalRequestPath, portalContextPath, EndPointServlet.ROOT_PATH_MAPPING);

      // ***************
      // ***************
      // ***************
      // ***************

      //
      Server server = getServer();

      //
      ServerInvocationContext invocationCtx = new ServerInvocationContextImpl(
         req,
         resp,
         webReq,
         portalHost,
         portalRequestPath,
         portalContextPath,
         urlContext);

      //
      ServerRequest request = new ServerRequest(invocationCtx);
      request.setServer(server);

      //
      ServerResponse response = new ServerResponse(request, invocationCtx);

      //
      ServerInvocation invocation = new ServerInvocation(invocationCtx);
      invocation.setRequest(request);
      invocation.setResponse(response);

      //
      RequestControllerFactory controllerFactory = getControllerFactory();
      RequestController controller = controllerFactory.createRequestController(invocation);
      invocation.setHandler(new RequestControllerDispatcher(controller));

      //
      try
      {
         InterceptorStackFactory stack = getInterceptorStackFactory();
         invocation.invoke(stack.getInterceptorStack());
      }
      catch (ServerException e)
      {
         log.error("Server exception", e);
         Throwable nested = Exceptions.unwrap(e);
         throw new ServletException(nested);
      }
      catch (InvocationException e)
      {
         log.error("Invocation exception", e);
         Throwable nested = Exceptions.unwrap(e);
         throw new ServletException(nested);
      }
      catch (ServletException e)
      {
         throw e;
      }
      catch (IOException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         log.error("Unexpected exception", e);
         Throwable nested = Exceptions.unwrap(e);
         throw new ServletException(nested);
      }
   }

   /**
    * Return the value for the servlet mapping. This implementation get the value from an init parameter of the servlet
    * called <b>asDefaultServlet</b>
    */
   protected boolean getAsDefaultServletInitValue()
   {
      ServletConfig config = getServletConfig();
      String value = config.getInitParameter("asDefaultServlet");
      if ("true".equalsIgnoreCase(value))
      {
         log.debug("Servlet loaded as default servlet mapping");
         return true;
      }
      else if ("false".equalsIgnoreCase(value))
      {
         log.debug("Servlet loaded as path mapping servlet");
         return false;
      }
      else
      {
         log.warn("Servlet mapping cannot be determined with init parameter value=" + value);
         return false;
      }
   }
}
