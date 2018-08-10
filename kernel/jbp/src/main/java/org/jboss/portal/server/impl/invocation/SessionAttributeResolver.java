/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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

import org.jboss.portal.common.invocation.AttributeResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Collections;
import java.security.Principal;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class SessionAttributeResolver implements AttributeResolver
{

   /** . */
   protected final HttpServletRequest req;

   /** . */
   protected final String prefix;

   public SessionAttributeResolver(
      HttpServletRequest req,
      String prefix,
      boolean principalScoped)
   {
      if (req == null)
      {
         throw new IllegalArgumentException();
      }
      if (prefix == null)
      {
         throw new IllegalArgumentException();
      }

      //
      if (principalScoped)
      {
         Principal principal = req.getUserPrincipal();
         if (principal != null)
         {
            prefix = prefix + principal.getName();
         }
      }

      //
      this.req = req;
      this.prefix =  prefix;
   }

   public Set getKeys()
   {
      HttpSession session = req.getSession(false);

      //
      if (session == null)
      {
         return Collections.EMPTY_SET;
      }

      //
      Set keys = new HashSet();
      for (Enumeration e = session.getAttributeNames();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();

         //
         if (key.startsWith(prefix))
         {
            keys.add(key);
         }
      }

      //
      return keys;
   }

   public Object getAttribute(Object o) throws IllegalArgumentException
   {
      HttpSession session = req.getSession(false);

      //
      if (session == null)
      {
         return null;
      }

      //
      return session.getAttribute(prefix + o);
   }

   public void setAttribute(Object o, Object o1) throws IllegalArgumentException
   {
      req.getSession().setAttribute(prefix + o, o1);
   }
}