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
package org.jboss.portal.server.aspects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jboss.portal.common.invocation.Interceptor;
import org.jboss.portal.common.invocation.Invocation;
import org.jboss.portal.common.invocation.InvocationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11433 $
 */
public abstract class LockInterceptor implements Interceptor
{

   /** . */
   private Map map = new HashMap();

   /** . */
   private Lock mapLock = new ReentrantLock();

   public static class InternalLock
   {

      /** . */
      private final Object id;

      /** . */
      private final Lock lock = new ReentrantLock();

      /** . */
      private int waiters = 0;

      public InternalLock(Object id)
      {
         this.id = id;
      }

      Object invoke(Invocation invocation) throws Exception, InvocationException
      {
         lock.lock();
         try
         {
            return invocation.invokeNext();
         }
         finally
         {
            lock.unlock();
         }
      }
   }

   protected InternalLock acquire(Object lockId)
   {
      mapLock.lock();
      try
      {
         InternalLock lock;
         if (map.containsKey(lockId))
         {
            lock = (InternalLock)map.get(lockId);
         }
         else
         {
            lock = new InternalLock(lockId);
            map.put(lockId, lock);
         }
         lock.waiters++;
         return lock;
      }
      finally
      {
         mapLock.unlock();
      }
   }

   protected void release(InternalLock internalLock)
   {
      mapLock.lock();
      try
      {
         if (--internalLock.waiters == 0)
         {
            map.remove(internalLock.id);
         }
      }
      finally
      {
         mapLock.unlock();
      }
   }

   protected abstract Object getLockId(Invocation invocation);

   public Object invoke(Invocation invocation) throws Exception, InvocationException
   {
      Object lockId = getLockId(invocation);

      //
      if (lockId != null)
      {
         InternalLock internalLock = acquire(lockId);
         try
         {
            return internalLock.invoke(invocation);
         }
         finally
         {
            release(internalLock);
         }
      }
      else
      {
         return invocation.invokeNext();
      }
   }
}
