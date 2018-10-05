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

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public final class RuntimeContext
{
   private final String pwaId;
   private final ServletContext servletContext;
   private final String contextPath;
   private final ClassLoader classLoader;

   public RuntimeContext(String pwaId, ServletContext context, String contextPath, ClassLoader loader)
   {
      if (pwaId == null)
      {
         throw new IllegalArgumentException("pwaId is null");
      }
      if (context == null)
      {
         throw new IllegalArgumentException("servletContext is null");
      }
      if (contextPath == null)
      {
         throw new IllegalArgumentException("contextPath is null");
      }
      if (loader == null)
      {
         throw new IllegalArgumentException("class loader is null");
      }

      this.pwaId = pwaId;
      this.servletContext = context;
      this.classLoader = loader;
      this.contextPath = contextPath;
   }

   public String getAppId()
   {
      return pwaId;
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

   public String getContextPath()
   {
      return contextPath;
   }

   public ClassLoader getClassLoader()
   {
      return classLoader;
   }
}
