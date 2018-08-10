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

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.ContentInfo;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.server.ServerException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * This interceptor set the charset on the request that is used to decode the request parameters as well as setting the
 * <code>org.jboss.portal.server.util.HTTPStreamInfo</code> on the server response that can be used later by the
 * rendering process to create the response sent to the user.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10337 $
 */
public class ContentTypeInterceptor extends ServerInterceptor
{
   protected void invoke(ServerInvocation invocation) throws Exception, InvocationException
   {
      try
      {
         // Set UTF-8 for parameter decoding
         ServerInvocationContext context = invocation.getServerContext();
         HttpServletRequest req = context.getClientRequest();
         req.setCharacterEncoding("UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new ServerException("Cannot set request encoding", e);
      }

      // Configure the stream info
      ContentInfo info = new MarkupInfo(MediaType.TEXT_HTML, "UTF-8");
      ServerResponse resp = invocation.getResponse();
      resp.setContentInfo(info);

      // Continue invocation
      invocation.invokeNext();
   }
}
