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
package org.jboss.portal.web.jboss;

import org.jboss.portal.web.RequestDispatchCallback;
import org.jboss.portal.web.ServletContainer;
import org.jboss.portal.web.ServletContainerFactory;
import org.jboss.portal.web.command.CommandDispatcher;
import org.jboss.portal.web.impl.DefaultServletContainerFactory;
import org.jboss.portal.web.spi.ServletContainerContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JBossWeb implementation of the spi. It implements the <code>ServletContainerFactory</code> interface but it gets the
 * returned instance from the DefaultServletContainerFactory instance.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10281 $
 */
public class JBossWebContext implements ServletContainerContext, ServletContainerFactory
{

   /** . */
   private final CommandDispatcher dispatcher = new CommandDispatcher();

   /** . */
   private final ServletContainer servletContainer = DefaultServletContainerFactory.getInstance().getServletContainer();

   public JBossWebContext()
   {
      servletContainer.register(this);
   }

   public Object include(
      ServletContext targetServletContext,
      HttpServletRequest req,
      HttpServletResponse resp,
      RequestDispatchCallback callback,
      Object handback) throws ServletException, IOException
   {
      return dispatcher.include(targetServletContext, req, resp, callback, handback);
   }

   public void setCallback(Registration registration)
   {
   }

   public void unsetCallback(Registration registration)
   {
   }

   // ServletContainerFactory implementation ***************************************************************************

   public ServletContainer getServletContainer()
   {
      return servletContainer;
   }
}
