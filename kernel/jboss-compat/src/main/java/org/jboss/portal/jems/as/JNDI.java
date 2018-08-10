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
package org.jboss.portal.jems.as;

import org.jboss.util.naming.NonSerializableFactory;

import javax.naming.CompositeName;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
 * Various JNDI stuff.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JNDI
{

   /**
    * Encapsulate JNDI binding operation into one single class, hidding non usefull JNDI complexity. It has been
    * designed to fit with service life cycle.
    * <p/>
    * <code>
    * <p/>
    * private String jndiName; private JNDI.Binding jndiBinding;
    * <p/>
    * public void startService() throws Exception { ... if (this.jndiName != null) { jndiBinding = new
    * JNDI.Binding(jndiName, this); jndiBinding.bind(); } ... }
    * <p/>
    * public void stopService() throws Exception { ... if (jndiBinding != null) { jndiBinding.unbind(); jndiBinding =
    * null; } ... } </code>
    */
   public static class Binding
   {

      /** . */
      private final String jndiName;

      /** . */
      private String unbindJNDIName;

      /** . */
      private Object o;

      public Binding(String jndiName, Object o)
      {
         if (jndiName == null)
         {
            throw new IllegalArgumentException("No JNDI name provided");
         }
         if (o == null)
         {
            throw new IllegalArgumentException("No object to bind for JNDI name " + jndiName);
         }
         this.jndiName = jndiName;
         this.o = o;
      }

      /**
       * Attempt to perform binding.
       *
       * @throws NamingException on failure
       */
      public void bind() throws NamingException
      {
         NonSerializableFactory.rebind(new CompositeName(jndiName), o, true);
         unbindJNDIName = jndiName;
      }

      /** Unbinds in a safe manner. */
      public void unbind()
      {
         if (unbindJNDIName != null)
         {
            try
            {
               NonSerializableFactory.unbind(unbindJNDIName);
            }
            catch (NameNotFoundException ignore)
            {
            }
         }
      }
   }
}
