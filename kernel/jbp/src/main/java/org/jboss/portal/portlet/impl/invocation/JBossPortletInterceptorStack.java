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
package org.jboss.portal.portlet.impl.invocation;

import org.jboss.portal.portlet.PortletInvokerInterceptor;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class JBossPortletInterceptorStack implements PortletInterceptorStack
{

   /** . */
   public static final PortletInterceptorStack EMPTY_STACK = new JBossPortletInterceptorStack(new PortletInvokerInterceptor[0]);

   /** . */
   private final PortletInvokerInterceptor[] interceptors;

   public JBossPortletInterceptorStack(PortletInvokerInterceptor interceptor)
   {
      this.interceptors = new PortletInvokerInterceptor[]{interceptor};
   }

   public JBossPortletInterceptorStack(PortletInvokerInterceptor[] interceptors)
   {
      if (interceptors == null)
      {
         throw new IllegalArgumentException();
      }
      this.interceptors = interceptors;
   }

   public int getLength()
   {
      return interceptors.length;
   }

   public PortletInvokerInterceptor getInterceptor(int index) throws ArrayIndexOutOfBoundsException
   {
      return interceptors[index];
   }
}

