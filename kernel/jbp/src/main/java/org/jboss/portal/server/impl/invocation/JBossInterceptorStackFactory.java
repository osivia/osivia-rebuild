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
import org.jboss.portal.common.invocation.InterceptorStackFactory;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JBossInterceptorStackFactory extends AbstractJBossService implements InterceptorStackFactory
{
    @Autowired
    private ApplicationContext applicationContext;
    
   /** . */
   protected List interceptorNames;

   /** . */
   protected List dynamicInterceptorNames;

   /** . */
   protected InterceptorStack stack;


   public JBossInterceptorStackFactory()
   {
      interceptorNames = null;
      dynamicInterceptorNames = new ArrayList();
      stack = JBossInterceptorStack.EMPTY_STACK;
   }

   public List getInterceptorNames()
   {
      return interceptorNames;
   }

   public void setInterceptorNames(List interceptorNames)
   {
      this.interceptorNames = interceptorNames;
   }

   public List getDynamicInterceptorNames()
   {
      return Collections.unmodifiableList(dynamicInterceptorNames);
   }

   public InterceptorStack getInterceptorStack()
   {
      return stack;
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
      interceptorNames.remove(name);
   }

   public void startService() throws Exception
   {
      rebuild();
   }

   /** Rebuild the interceptor stack. */
   public void rebuild() throws Exception
   {
      List names = new ArrayList();

      //
      if (interceptorNames != null)
      {
         names.addAll(interceptorNames);
      }

      //
      names.addAll(dynamicInterceptorNames);

      //
      log.debug("Building interceptor stack " + getName());
      Interceptor[] interceptors = new Interceptor[names.size()];
      for (int i = 0; i < names.size(); i++)
      {
         String name = (String) names.get(i);
         log.debug("Adding interceptor " + name + " to the stack");
         interceptors[i] = (Interceptor) (applicationContext.getBean(name));
      }

      //
      stack = new JBossInterceptorStack(interceptors);
   }

   public void stopService()
   {
      this.stack = JBossInterceptorStack.EMPTY_STACK;
   }
}
