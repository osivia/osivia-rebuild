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
package org.jboss.portal.jems.as.system;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.jboss.system.ServiceMBeanSupport;

/**
 * JBoss service integration helper.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class AbstractJBossService extends ServiceMBeanSupport
{
	
	@PostConstruct 
	protected void pc()	{
		try {
			startService();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

   public AbstractJBossService()
   {

   }

   public AbstractJBossService(final Class type)
   {
      super(type);
   }

   public AbstractJBossService(final String category)
   {
      super(category);
   }

   public AbstractJBossService(final Logger log)
   {
      super(log);
   }

   public final int getState()
   {
      return super.getState();
   }

   public final String getStateString()
   {
      return super.getStateString();
   }

   public final void create() throws Exception
   {
      super.create();
   }

   public final void start() throws Exception
   {
      super.start();
   }

   public final void stop()
   {
      super.stop();
   }

   public final void destroy()
   {
      super.destroy();
   }
}
