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
package org.jboss.portal.server;

import org.jboss.portal.common.invocation.InvocationContext;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public interface ServerInvocationContext extends InvocationContext
{
   /**
    * Return the request that made the connection to the server.
    *
    * @return the http request.
    */
   HttpServletRequest getClientRequest();

   /**
    * Return the response that will be used by the server.
    *
    * @return the http response
    */
   HttpServletResponse getClientResponse();

   /**
    * Return the parameter map decoded form the query string.
    *
    * @returns the query parameter map
    */
   ParameterMap getQueryParameterMap();

   /**
    * Return the parameter map for the body if the request was a POST with the content type x-www-formurlencoded
    * otherwise return null.
    *
    * @return the body parameter map
    */
   ParameterMap getBodyParameterMap();

   /**
    * Return the normalized media type of the request or null if none has been provided by the client.
    *
    * @return the media type
    */
   String getMediaType();

   /**
    * Return the url context of this request.
    *
    * @return the url context
    */
   URLContext getURLContext();

   /**
    * Return the value of the portal request path for this request.
    *
    * @return the portal request path
    */
   String getPortalRequestPath();

   /**
    * Return the value of the portal context path for this request.
    *
    * @return the portal context path
    */
   String getPortalContextPath();

   /**
    * Return the portal host value for this request.
    *
    * @return the portal host
    */
   String getPortalHost();

   /**
    * Renders an URL.
    *
    * @param url     the url structure
    * @param context the url context
    * @param format  the url format
    * @return the encoded url
    */
   String renderURL(ServerURL url, URLContext context, URLFormat format);
}
