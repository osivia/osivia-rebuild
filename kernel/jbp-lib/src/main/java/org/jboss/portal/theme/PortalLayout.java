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

import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.web.ServletContextDispatcher;

import java.io.Writer;

/**
 * Implementation of a <code>PortalLayout</code>. <p>An instance of this class represents the meta data of a portal
 * layout.</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>.
 * @version <tt>$Revision: 8784 $</tt>
 */
public abstract class PortalLayout
{

   /** . */
   protected LayoutInfo info;

   /** . */
   protected LayoutServiceInfo serviceInfo;

   public void init(LayoutServiceInfo serviceInfo, LayoutInfo info)
   {
      this.info = info;
      this.serviceInfo = serviceInfo;
   }

   public void destroy()
   {
      info = null;
      serviceInfo = null;
   }

   public LayoutInfo getLayoutInfo()
   {
      return info;
   }

   public LayoutServiceInfo getServiceInfo()
   {
      return serviceInfo;
   }

   /**
    * Provides a render context that will render its markup in the response provided by the servlet context dispatcher.
    *
    * @param themeContext the theme context
    * @param markupInfo   the markup info
    * @param dispatcher   the way to dispatch to the layout and receive the produced markup
    * @return a render context
    */
   public abstract RendererContext getRenderContext(
      ThemeContext themeContext,
      MarkupInfo markupInfo,
      ServletContextDispatcher dispatcher);

   /**
    * Provides a render context that will render its markup in the provided writer.
    *
    * @param themeContext the theme context
    * @param markupInfo   the markup info
    * @param dispatcher   the way to dispatch to the layout
    * @param writer       the writer that will receive the produced markup
    * @return a render context
    */
   public abstract RendererContext getRenderContext(
      ThemeContext themeContext,
      MarkupInfo markupInfo,
      ServletContextDispatcher dispatcher,
      Writer writer);
}
