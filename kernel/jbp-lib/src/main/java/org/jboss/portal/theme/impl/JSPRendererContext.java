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
package org.jboss.portal.theme.impl;

import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.theme.render.ObjectRendererContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.RendererFactory;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.web.ServletContextDispatcher;

import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JSPRendererContext extends RendererContext
{

   /** The writer provided by the JSP tag lib, the tag lib will flush the writer at the right time. */
   private PrintWriter writer;

   public JSPRendererContext(
      ThemeContext themeContext,
      RendererFactory rendererFactory,
      ServletContextDispatcher dispatcher,
      MarkupInfo markupInfo)
   {
      super(themeContext, rendererFactory, dispatcher, markupInfo);
   }

   public PrintWriter getWriter()
   {
      return writer;
   }

   public void setWriter(PrintWriter writer)
   {
      this.writer = writer;
   }

   public void render(ObjectRendererContext ctx) throws RenderException, IllegalStateException
   {
      super.render(ctx);
   }
}
