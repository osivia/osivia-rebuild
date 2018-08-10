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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class AbstractDelegatingResponse implements HttpServletResponse
{

   protected abstract HttpServletResponse getDelegate();

   public void addCookie(Cookie cookie)
   {
      getDelegate().addCookie(cookie);
   }

   public boolean containsHeader(String name)
   {
      return getDelegate().containsHeader(name);
   }

   public String encodeURL(String name)
   {
      return getDelegate().encodeURL(name);
   }

   public String encodeRedirectURL(String name)
   {
      return getDelegate().encodeRedirectURL(name);
   }

   public String encodeUrl(String name)
   {
      return getDelegate().encodeUrl(name);
   }

   public String encodeRedirectUrl(String name)
   {
      return getDelegate().encodeRedirectUrl(name);
   }

   public void sendError(int i, String name) throws IOException
   {
      getDelegate().sendError(i, name);
   }

   public void sendError(int i) throws IOException
   {
      getDelegate().sendError(i);
   }

   public void sendRedirect(String name) throws IOException
   {
      getDelegate().sendRedirect(name);
   }

   public void setDateHeader(String name, long l)
   {
      getDelegate().setDateHeader(name, l);
   }

   public void addDateHeader(String name, long l)
   {
      getDelegate().addDateHeader(name, l);
   }

   public void setHeader(String name, String name1)
   {
      getDelegate().setHeader(name, name1);
   }

   public void addHeader(String name, String name1)
   {
      getDelegate().addHeader(name, name1);
   }

   public void setIntHeader(String name, int i)
   {
      getDelegate().setIntHeader(name, i);
   }

   public void addIntHeader(String name, int i)
   {
      getDelegate().addIntHeader(name, i);
   }

   public void setStatus(int i)
   {
      getDelegate().setStatus(i);
   }

   public void setStatus(int i, String name)
   {
      getDelegate().setStatus(i, name);
   }

   public String getCharacterEncoding()
   {
      return getDelegate().getCharacterEncoding();
   }

   public String getContentType()
   {
      return getDelegate().getContentType();
   }

   public ServletOutputStream getOutputStream() throws IOException
   {
      return getDelegate().getOutputStream();
   }

   public PrintWriter getWriter() throws IOException
   {
      return getDelegate().getWriter();
   }

   public void setCharacterEncoding(String name)
   {
      getDelegate().setCharacterEncoding(name);
   }

   public void setContentLength(int i)
   {
      getDelegate().setContentLength(i);
   }

   public void setContentType(String name)
   {
      getDelegate().setContentType(name);
   }

   public void setBufferSize(int i)
   {
      getDelegate().setBufferSize(i);
   }

   public int getBufferSize()
   {
      return getDelegate().getBufferSize();
   }

   public void flushBuffer() throws IOException
   {
      getDelegate().flushBuffer();
   }

   public void resetBuffer()
   {
      getDelegate().resetBuffer();
   }

   public boolean isCommitted()
   {
      return getDelegate().isCommitted();
   }

   public void reset()
   {
      getDelegate().reset();
   }

   public void setLocale(Locale locale)
   {
      getDelegate().setLocale(locale);
   }

   public Locale getLocale()
   {
      return getDelegate().getLocale();
   }
}
