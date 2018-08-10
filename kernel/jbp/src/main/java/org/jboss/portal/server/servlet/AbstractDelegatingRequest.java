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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class AbstractDelegatingRequest implements HttpServletRequest
{

   protected abstract HttpServletRequest getDelegate();

   public String getAuthType()
   {
      return getDelegate().getAuthType();
   }

   public Cookie[] getCookies()
   {
      return getDelegate().getCookies();
   }

   public long getDateHeader(String name)
   {
      return getDelegate().getDateHeader(name);
   }

   public String getHeader(String name)
   {
      return getDelegate().getHeader(name);
   }

   public Enumeration getHeaders(String name)
   {
      return getDelegate().getHeaders(name);
   }

   public Enumeration getHeaderNames()
   {
      return getDelegate().getHeaderNames();
   }

   public int getIntHeader(String name)
   {
      return getDelegate().getIntHeader(name);
   }

   public String getMethod()
   {
      return getDelegate().getMethod();
   }

   public String getPathInfo()
   {
      return getDelegate().getPathInfo();
   }

   public String getPathTranslated()
   {
      return getDelegate().getPathTranslated();
   }

   public String getContextPath()
   {
      return getDelegate().getContextPath();
   }

   public String getQueryString()
   {
      return getDelegate().getQueryString();
   }

   public String getRemoteUser()
   {
      return getDelegate().getRemoteUser();
   }

   public boolean isUserInRole(String name)
   {
      return getDelegate().isUserInRole(name);
   }

   public Principal getUserPrincipal()
   {
      return getDelegate().getUserPrincipal();
   }

   public String getRequestedSessionId()
   {
      return getDelegate().getRequestedSessionId();
   }

   public String getRequestURI()
   {
      return getDelegate().getRequestURI();
   }

   public StringBuffer getRequestURL()
   {
      return getDelegate().getRequestURL();
   }

   public String getServletPath()
   {
      return getDelegate().getServletPath();
   }

   public HttpSession getSession(boolean b)
   {
      return getDelegate().getSession(b);
   }

   public HttpSession getSession()
   {
      return getDelegate().getSession();
   }

   public boolean isRequestedSessionIdValid()
   {
      return getDelegate().isRequestedSessionIdValid();
   }

   public boolean isRequestedSessionIdFromCookie()
   {
      return getDelegate().isRequestedSessionIdFromCookie();
   }

   public boolean isRequestedSessionIdFromURL()
   {
      return getDelegate().isRequestedSessionIdFromURL();
   }

   public boolean isRequestedSessionIdFromUrl()
   {
      return getDelegate().isRequestedSessionIdFromUrl();
   }

   public Object getAttribute(String name)
   {
      return getDelegate().getAttribute(name);
   }

   public Enumeration getAttributeNames()
   {
      return getDelegate().getAttributeNames();
   }

   public String getCharacterEncoding()
   {
      return getDelegate().getCharacterEncoding();
   }

   public void setCharacterEncoding(String name) throws UnsupportedEncodingException
   {
      getDelegate().setCharacterEncoding(name);
   }

   public int getContentLength()
   {
      return getDelegate().getContentLength();
   }

   public String getContentType()
   {
      return getDelegate().getContentType();
   }

   public ServletInputStream getInputStream() throws IOException
   {
      return getDelegate().getInputStream();
   }

   public String getParameter(String name)
   {
      return getDelegate().getParameter(name);
   }

   public Enumeration getParameterNames()
   {
      return getDelegate().getParameterNames();
   }

   public String[] getParameterValues(String name)
   {
      return getDelegate().getParameterValues(name);
   }

   public Map getParameterMap()
   {
      return getDelegate().getParameterMap();
   }

   public String getProtocol()
   {
      return getDelegate().getProtocol();
   }

   public String getScheme()
   {
      return getDelegate().getScheme();
   }

   public String getServerName()
   {
      return getDelegate().getServerName();
   }

   public int getServerPort()
   {
      return getDelegate().getServerPort();
   }

   public BufferedReader getReader() throws IOException
   {
      return getDelegate().getReader();
   }

   public String getRemoteAddr()
   {
      return getDelegate().getRemoteAddr();
   }

   public String getRemoteHost()
   {
      return getDelegate().getRemoteHost();
   }

   public void setAttribute(String name, Object object)
   {
      getDelegate().setAttribute(name, object);
   }

   public void removeAttribute(String name)
   {
      getDelegate().removeAttribute(name);
   }

   public Locale getLocale()
   {
      return getDelegate().getLocale();
   }

   public Enumeration getLocales()
   {
      return getDelegate().getLocales();
   }

   public boolean isSecure()
   {
      return getDelegate().isSecure();
   }

   public RequestDispatcher getRequestDispatcher(String name)
   {
      return getDelegate().getRequestDispatcher(name);
   }

   public String getRealPath(String name)
   {
      return getDelegate().getRealPath(name);
   }

   public int getRemotePort()
   {
      return getDelegate().getRemotePort();
   }

   public String getLocalName()
   {
      return getDelegate().getLocalName();
   }

   public String getLocalAddr()
   {
      return getDelegate().getLocalAddr();
   }

   public int getLocalPort()
   {
      return getDelegate().getLocalPort();
   }
}
