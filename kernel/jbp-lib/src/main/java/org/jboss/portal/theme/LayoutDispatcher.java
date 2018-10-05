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
package org.jboss.portal.theme;

import org.jboss.portal.server.servlet.FilterCommand;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.web.RequestDispatchCallback;
import org.jboss.portal.web.ServletContextDispatcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Dispatches the request to the target layout. The major side effect is to change the context path returned by the
 * request to the value returned by <code>PortalLayout#getContextPath()</code> so the layout can safely use the
 * getContextPath in order to designates resources located in the same web application.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class LayoutDispatcher implements RequestDispatchCallback
{

   private final PageRendererContext markupResult;
   private final RendererContext rendererContext;
   private final String layoutURI;
   private final LayoutInfo layoutInfo;

   /**
    * @param rendererContext
    * @throws IllegalArgumentException if the layout is null
    */
   public LayoutDispatcher(
      RendererContext rendererContext,
      PageRendererContext result,
      String layoutURI,
      LayoutInfo layoutInfo)
      throws IllegalArgumentException
   {

      if (result == null)
      {
         throw new IllegalArgumentException("No null response allowed here");
      }

      if (rendererContext == null)
      {
         throw new IllegalArgumentException("No render context provided");
      }
      if (layoutURI == null)
      {
         throw new IllegalArgumentException("No null layout allowed here");
      }

      this.markupResult = result;
      this.rendererContext = rendererContext;
      this.layoutURI = layoutURI;
      this.layoutInfo = layoutInfo;
   }

   public void include() throws IOException, ServletException
   {
      try
      {
         ServletContextDispatcher dispatcher = rendererContext.getDispatcher();
         dispatcher.include(layoutInfo.getServletContext(), this, null);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public Object doCallback(ServletContext dispatchedServletContext, HttpServletRequest dispatchedRequest, HttpServletResponse dispatchedResponse, Object handback) throws ServletException, IOException
   {
      try
      {
         RequestDispatcher dispatcher = dispatchedServletContext.getRequestDispatcher(layoutURI);

         //
         dispatchedRequest.setAttribute(LayoutConstants.ATTR_RENDERCONTEXT, rendererContext);
         dispatchedRequest.setAttribute(FilterCommand.REQ_ATT_KEY, this);
         dispatchedRequest.setAttribute(LayoutConstants.ATTR_PAGE, markupResult);

         //
         HttpServletRequest requestWrapper = new HttpServletRequestWrapper(dispatchedRequest)
         {
            public String getContextPath()
            {
               return layoutInfo.getContextPath();
            }
         };

         //
         dispatcher.include(requestWrapper, dispatchedResponse);
      }
      finally
      {
         dispatchedRequest.removeAttribute(LayoutConstants.ATTR_PAGE);
         dispatchedRequest.removeAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
      }

      //
      return null;
   }
}
