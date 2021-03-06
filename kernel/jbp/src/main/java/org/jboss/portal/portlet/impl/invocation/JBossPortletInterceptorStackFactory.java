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

import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.portlet.PortletInvokerInterceptor;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossPortletInterceptorStackFactory extends AbstractJBossService implements PortletInterceptorStackFactory
{

   /** . */
   public List<PortletInvokerInterceptor> interceptors;


   
   /** . */
   protected PortletInterceptorStack stack;

   public JBossPortletInterceptorStackFactory()
   {
	   interceptors = null;

   }

   public List<PortletInvokerInterceptor> getInterceptors()
   {
      return interceptors;
   }

   public void setInterceptors(List<PortletInvokerInterceptor> interceptors)
   {
      this.interceptors = interceptors;
   }



   public void startService() throws Exception
   {
      rebuild();
   }

   /** Rebuild the interceptor stack. */
   public void rebuild() throws Exception
   {



      //
      log.debug("Building interceptor stack " + getName());
      PortletInvokerInterceptor[] portletInterceptors = new PortletInvokerInterceptor[interceptors.size()];
      if (interceptors.size() == 1)
      {

         PortletInvokerInterceptor a = (PortletInvokerInterceptor)interceptors.get(0);
         portletInterceptors[0] = a;
      }
      for (int i = 0; i < interceptors.size()-1; i++)
      {

         PortletInvokerInterceptor a = (PortletInvokerInterceptor) interceptors.get(i);

         PortletInvokerInterceptor b = (PortletInvokerInterceptor) interceptors.get(i+1);
         a.setNext(b);
         portletInterceptors[i] = a;
         portletInterceptors[i+1] = b;
      }
      
      //
      stack = new JBossPortletInterceptorStack(portletInterceptors);

   }

   public void stopService()
   {
      this.stack = JBossPortletInterceptorStack.EMPTY_STACK;
   }

   public PortletInterceptorStack getInterceptorStack()
   {
      return stack;
   }
}
