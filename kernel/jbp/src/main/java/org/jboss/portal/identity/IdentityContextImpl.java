/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity;

import org.jboss.portal.common.util.CopyOnWriteRegistry;

import java.util.Collection;

/**
 * Keeps references to all identity related modules to enable them interactions
 *
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class IdentityContextImpl implements IdentityContext {

   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityContextImpl.class);

   private final CopyOnWriteRegistry registry;

   public IdentityContextImpl()
   {
      registry = new CopyOnWriteRegistry();
   }

   public void register(Object object, String name) throws IdentityException
   {
      if (!registry.register(name, object))
      {
          throw new IdentityException("Cannot register object in IdentityContext with name: " + name);
      }
      if (log.isDebugEnabled()) log.debug("registering object: " + name + " ; " + object.getClass());
   }

   public void unregister(String name)
   {
      if (registry.unregister(name) == null)
      {
          log.error("Cannot unregister object from IdentityContext with name: " + name);
      }
      if (log.isDebugEnabled()) log.debug("unregistering object: " + name);
   }

   public Object getObject(String name) throws IdentityException
   {
      Object o = registry.getRegistration(name);
      if (o == null)
      {
         throw new IdentityException("No such mapping in IdentityContext: " + name);
      }
      return o;
   }

   public Collection getKeys()
   {
      return registry.getKeys();
   }

   public Collection getValues()
   {
      return registry.getRegistrations(); 
   }
}
