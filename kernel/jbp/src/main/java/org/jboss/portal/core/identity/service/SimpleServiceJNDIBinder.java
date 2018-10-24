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

package org.jboss.portal.core.identity.service;

import org.jboss.portal.identity.ServiceJNDIBinder;
import org.jboss.util.naming.NonSerializableFactory;

import javax.naming.CompositeName;
import javax.naming.NameNotFoundException;

/**
 * Really dummy JNDI binder
 *
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 8786 $
 */
public class SimpleServiceJNDIBinder implements ServiceJNDIBinder
{
   public void bind(String jndiName, Object service) throws Exception
   {
      if (jndiName == null)
      {
         new IllegalArgumentException("Null JNDI name to bind");
      }
      if (service == null)
      {
         new IllegalArgumentException("Null service to bind to JNDI");
      }
      NonSerializableFactory.rebind(new CompositeName(jndiName), service, true);
   }

   public void unbind(String jndiName)
   {
      if (jndiName != null)
      {
         try
         {
            NonSerializableFactory.unbind(jndiName);
         }
         catch (NameNotFoundException ignore)
         {
         }
      }
   }
}