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
   protected List<ObjectName> interceptorNames;

   /** . */
   protected List<ObjectName> dynamicInterceptorNames;
   
   /** . */
   protected PortletInterceptorStack stack;

   public JBossPortletInterceptorStackFactory()
   {
      interceptorNames = null;
      dynamicInterceptorNames = new ArrayList<ObjectName>();
   }

   public List<ObjectName> getInterceptorNames()
   {
      return interceptorNames;
   }

   public void setInterceptorNames(List<ObjectName> interceptorNames)
   {
      this.interceptorNames = interceptorNames;
   }

   public List<ObjectName> getDynamicInterceptorNames()
   {
      return Collections.unmodifiableList(dynamicInterceptorNames);
   }

   /**
    * Add's the supplied name to the list of interceptor names.
    *
    * @param name the intercptor's ObjectName.
    * @throws Exception
    */
   public void addInterceptor(ObjectName name) throws Exception
   {
      dynamicInterceptorNames.add(name);
   }

   /**
    * Remove's the supplied name from the list of interceptor names.
    *
    * @param name the intercptor's ObjectName.
    * @throws Exception
    */
   public void removeInterceptor(ObjectName name) throws Exception
   {
      dynamicInterceptorNames.remove(name);
   }

   public void startService() throws Exception
   {
      rebuild();
   }

   /** Rebuild the interceptor stack. */
   public void rebuild() throws Exception
   {
      List<ObjectName> names = new ArrayList<ObjectName>();

      //
      if (interceptorNames != null)
      {
         names.addAll(interceptorNames);
      }

      //
      names.addAll(dynamicInterceptorNames);

      //
      log.debug("Building interceptor stack " + getName());
      PortletInvokerInterceptor[] interceptors = new PortletInvokerInterceptor[names.size()];
      if (names.size() == 1)
      {
         ObjectName name = names.get(0);
         log.debug("Adding interceptor " + name + " to the stack");
         PortletInvokerInterceptor a = (PortletInvokerInterceptor)server.getAttribute(name, "ManagedResource");
         interceptors[0] = a;
      }
      for (int i = 0; i < names.size()-1; i++)
      {
         ObjectName name = names.get(i);
         log.debug("Adding interceptor " + name + " to the stack");
         PortletInvokerInterceptor a = (PortletInvokerInterceptor)server.getAttribute(name, "ManagedResource");
         name = names.get(i+1);
         log.debug("Adding interceptor " + name + " to the stack");
         PortletInvokerInterceptor b = (PortletInvokerInterceptor)server.getAttribute(name, "ManagedResource");
         a.setNext(b);
         interceptors[i] = a;
         interceptors[i+1] = b;
      }
      
      //
      stack = new JBossPortletInterceptorStack(interceptors);

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
