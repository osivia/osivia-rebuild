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
package org.jboss.portal.server.impl.invocation;

import org.jboss.portal.common.invocation.Interceptor;
import org.jboss.portal.common.invocation.InterceptorStack;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossInterceptorStack implements InterceptorStack
{

   /** . */
   public static final InterceptorStack EMPTY_STACK = new JBossInterceptorStack(new Interceptor[0]);

   /** . */
   private final Interceptor[] interceptors;

   public JBossInterceptorStack(Interceptor interceptor)
   {
      this.interceptors = new Interceptor[]{interceptor};
   }

   public JBossInterceptorStack(Interceptor[] interceptors)
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

   public Interceptor getInterceptor(int index) throws ArrayIndexOutOfBoundsException
   {
      return interceptors[index];
   }
}
