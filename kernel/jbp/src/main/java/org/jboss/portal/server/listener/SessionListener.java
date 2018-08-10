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
package org.jboss.portal.server.listener;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.server.aspects.server.SignOutInterceptor.Invalidation;
import org.jboss.portal.web.ServletContainer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This listener listens to the main portal session events. When the portal session timesout, all web applications'
 * sessions referenced by org.jboss.portal.server.aspects.server.SignOutInterceptor are destroyed
 *
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SessionListener implements HttpSessionListener
{
   /** . */
   private static final Invalidation invalidator = new Invalidation();

   /** . */
   private Logger log = Logger.getLogger(SessionListener.class);

   /** . */
   private static final String KEY = "org.jboss.portal.session.contexts";

   public void sessionCreated(HttpSessionEvent arg0)
   {
      // Nothing to do
   }

   public void sessionDestroyed(HttpSessionEvent arg0)
   {
      Set<String> contexts = (Set<String>)arg0.getSession().getAttribute(KEY);

      if (contexts != null)
      {
         MBeanServer server = MBeanServerLocator.locateJBoss();
         ObjectName objectName = null;
         ServletContainer servletContainer = null;

         try
         {
            objectName = new ObjectName("portal:service=ServletContainerFactory");
            servletContainer = (ServletContainer)server.getAttribute(objectName, "ServletContainer");
         }
         catch (Exception e1)
         {
            log.error("Error while destroying portlet webapp sessions");
         }

         // Iterate over all the context that have been used
         for (String dispatchContextName : contexts)
         {
            // Get the context
            ServletContext dispatchContext = arg0.getSession().getServletContext().getContext(dispatchContextName);

            // The context could be null if the web app has been removed after the web app has been tracked
            if (dispatchContext != null)
            {
               try
               {
                  // Execute the command that invalidates the session
                  servletContainer.include(dispatchContext, new TestHttpServletRequest(arg0.getSession()), new TestHttpServletResponse(), invalidator, null);
               }
               catch (Exception e)
               {
                  log.error("An error occured when trying to invalidate the sessions");
               }
            }
         }
      }
   }

   // request
   private static class TestHttpServletRequest implements HttpServletRequest
   {
      private Map attributes;

      private HttpSession session;

      public TestHttpServletRequest(HttpSession session)
      {
         attributes = new HashMap();
         this.session = session;
      }

      public String getAuthType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Cookie[] getCookies()
      {
         return new Cookie[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

      public long getDateHeader(String string)
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getHeader(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getHeaders(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getHeaderNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getIntHeader(String string)
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getMethod()
      {
         return "GET";
      }

      public String getPathInfo()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getPathTranslated()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContextPath()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getQueryString()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteUser()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isUserInRole(String string)
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Principal getUserPrincipal()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRequestedSessionId()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRequestURI()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public StringBuffer getRequestURL()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServletPath()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public HttpSession getSession(boolean b)
      {
         return session;
      }

      public HttpSession getSession()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdValid()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromCookie()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromURL()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromUrl()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Object getAttribute(String key)
      {
         return attributes.get(key);
      }

      public Enumeration getAttributeNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getCharacterEncoding()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setCharacterEncoding(String string) throws UnsupportedEncodingException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getContentLength()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContentType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public ServletInputStream getInputStream() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getParameter(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getParameterNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String[] getParameterValues(String string)
      {
         return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Map getParameterMap()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getProtocol()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getScheme()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServerName()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getServerPort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public BufferedReader getReader() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteAddr()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteHost()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setAttribute(String key, Object value)
      {
         attributes.put(key, value);
      }

      public void removeAttribute(String key)
      {
         attributes.remove(key);
      }

      public Locale getLocale()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getLocales()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isSecure()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public RequestDispatcher getRequestDispatcher(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRealPath(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getRemotePort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getLocalName()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getLocalAddr()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getLocalPort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}
   }

   // response
   private static class TestHttpServletResponse implements HttpServletResponse
   {
      private StringWriter writer;

      public TestHttpServletResponse()
      {

      }

      public void addCookie(Cookie cookie)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean containsHeader(String string)
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeURL(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeRedirectURL(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeUrl(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeRedirectUrl(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendError(int i, String string) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendError(int i) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendRedirect(String string) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setDateHeader(String string, long l)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addDateHeader(String string, long l)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setHeader(String string, String string1)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addHeader(String string, String string1)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setIntHeader(String string, int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addIntHeader(String string, int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setStatus(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setStatus(int i, String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getCharacterEncoding()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContentType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public ServletOutputStream getOutputStream() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public PrintWriter getWriter() throws IOException
      {
         writer = new StringWriter();
         return new PrintWriter(writer);
      }

      public String getResult()
      {
         return writer.toString();
      }

      public void setCharacterEncoding(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setContentLength(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setContentType(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setBufferSize(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getBufferSize()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void flushBuffer() throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void resetBuffer()
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isCommitted()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void reset()
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setLocale(Locale locale)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public Locale getLocale()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getResponseMarkup()
      {
         return null;  //To change body of created methods use File | Settings | File Templates.
      }

	@Override
	public void setContentLengthLong(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}
   }

}

