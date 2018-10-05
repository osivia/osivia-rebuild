/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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

package org.jboss.portal.theme.servlet;

import org.jboss.portal.common.util.ParameterValidation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author <a href="mailto:tomasz.szymanski@jboss.com">Tomasz Szymanski</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 */

public class DynaAjaxServlet extends HttpServlet
{
   private static final String ACTION = "action";
   private static final String[] POSSIBLE_ACTION_VALUES = new String[]{"windowremove", "windowmove"};
   private static final String DEFAULT_RESPONSE = "";

   public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
   {
      doPost(req, resp);
   }

   public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String actionValue = req.getParameter(ACTION);
      String response = ParameterValidation.sanitizeFromValues(actionValue, POSSIBLE_ACTION_VALUES, DEFAULT_RESPONSE);
      sendResp(resp, response);
   }

   private void sendResp(HttpServletResponse resp, String respData)
      throws IOException
   {
      resp.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
      resp.setDateHeader("Last-Modified", new Date().getTime());
      resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
      resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
      resp.setContentType("text/html");

      resp.getWriter().write(respData);
   }

}
