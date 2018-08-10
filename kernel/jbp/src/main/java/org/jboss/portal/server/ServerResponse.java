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

import org.jboss.portal.common.util.ContentInfo;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ServerResponse
{

   /** Default format which is relative and servlet encoded. */
   protected static final URLFormat DEFAULT_FORMAT = URLFormat.newInstance(true, true);

   /** The server request. */
   protected ServerRequest req;

   /** . */
   protected ContentInfo contentInfo;

   /** Indicate that the invocation requires a sign out of the current authenticated user. */
   boolean wantSignOut;

   /** . */
   protected ServerInvocationContext invocationCtx;

   public ServerResponse(ServerRequest req, ServerInvocationContext invocationCtx)
   {
      this.req = req;
      this.invocationCtx = invocationCtx;
   }

   public ContentInfo getContentInfo()
   {
      return contentInfo;
   }

   public void setContentInfo(ContentInfo contentInfo)
   {
      this.contentInfo = contentInfo;
   }

   public boolean getWantSignOut()
   {
      return wantSignOut;
   }

   public void setWantSignOut(boolean wantSignOut)
   {
      this.wantSignOut = wantSignOut;
   }

   public String renderURL(ServerURL url)
   {
      return invocationCtx.renderURL(url, invocationCtx.getURLContext(), DEFAULT_FORMAT);
   }

   public String renderURL(ServerURL url, URLFormat format)
   {
      if (format == null)
      {
         format = DEFAULT_FORMAT;
      }
      return invocationCtx.renderURL(url, invocationCtx.getURLContext(), format);
   }

   public String renderURL(ServerURL url, URLContext context, URLFormat format)
   {
      if (context == null)
      {
         context = invocationCtx.getURLContext();
      }
      if (format == null)
      {
         format = DEFAULT_FORMAT;
      }
      return invocationCtx.renderURL(url, context, format);
   }

   public String renderURL(ServerURL url, URLContext context)
   {
      if (context == null)
      {
         context = invocationCtx.getURLContext();
      }
      return invocationCtx.renderURL(url, context, DEFAULT_FORMAT);
   }
}
