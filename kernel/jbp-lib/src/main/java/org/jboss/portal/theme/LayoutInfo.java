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

import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.StateURIMetaData;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

/**
 * Info about the layout.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public final class LayoutInfo
{

   /** . */
   private final RuntimeContext ctx;

   /** . */
   private final PortalLayoutMetaData layoutMD;

   /** . */
   private final ServerRegistrationID registrationId;

   public LayoutInfo(RuntimeContext ctx, PortalLayoutMetaData layoutMD)
   {
      this.ctx = ctx;
      this.layoutMD = layoutMD;
      this.registrationId = ServerRegistrationID.createID(ServerRegistrationID.TYPE_LAYOUT, new String[]{ctx.getAppId(), layoutMD.getName()});
   }

   public ServerRegistrationID getRegistrationId()
   {
      return registrationId;
   }

   /**
    * Get the name of the portal web application that contains this layout.
    *
    * @return the name of the portal web application that contains this layout
    */
   public String getAppId()
   {
      return ctx.getAppId();
   }

   /**
    * Get the name of this layout.
    *
    * @return the name of the layout
    */
   public String getName()
   {
      return layoutMD.getName();
   }

   /**
    * Get the servlet context that contains this layout.
    *
    * @return the serlvet context of this layout
    */
   public ServletContext getServletContext()
   {
      return ctx.getServletContext();
   }

   /**
    * Get the generic URI for this layout. <p>The URI is the location of the layout resource inside the Servlet context
    * that contains it. The generic URI is the one that does not depend on a state. It is the most common URI for this
    * layout.</p>
    *
    * @return the URI for this layout
    * @see #getServletContext()
    * @see #getURI(String)
    */
   public String getURI()
   {
      return layoutMD.getURI();
   }

   /**
    * Get the uri, the location of the layout, relative to its context.
    *
    * @param state an optional key to further separate URIs (for example for maximized window state). If a layout uri
    *              was defined for the provided state, that uri will be returned, otherwise the generic URI for this
    *              layout will be returned. If null is provided, the result is the same as calling getURI()
    * @return the uri of the layout relative to its context
    * @see #getServletContext()
    * @see #getURI
    */
   public String getURI(String state)
   {
      if (state == null)
      {
         return getURI();
      }
      if (!layoutMD.getLayoutURIStateMap().isEmpty())
      {
         StateURIMetaData stateURI = (StateURIMetaData)layoutMD.getLayoutURIStateMap().get(state);
         if (stateURI != null)
         {
            return stateURI.getURI();
         }
         else
         {
            return null;
         }
      }
      return null;
   }

   /**
    * Get the context path for the servlet context (portal web application) that contains this layout.
    *
    * @return the context path of this layout
    */
   public String getContextPath()
   {
      return ctx.getContextPath();
   }

   /** @see PortalLayoutMetaData#getRegionNames */
   public List getRegionNames()
   {
      return Collections.unmodifiableList(layoutMD.getRegionNames());
   }

   public String toString()
   {
      return "PortalLayout: " + registrationId;
   }
}
