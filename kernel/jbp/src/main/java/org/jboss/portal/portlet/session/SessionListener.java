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
package org.jboss.portal.portlet.session;

import org.jboss.logging.Logger;

import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener for various session events that modifies the subsession associated with the current thread of execution.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class SessionListener implements HttpSessionAttributeListener, HttpSessionListener
{

   /** . */
   private Logger log = Logger.getLogger(SessionListener.class);

   /** . */
   private boolean trace = log.isTraceEnabled();

   /** . */
   private static ThreadLocal local = new ThreadLocal();

   public void attributeAdded(HttpSessionBindingEvent event)
   {
      List modifications = (List)local.get();
      if (modifications != null)
      {
         String name = event.getName();
         // Only PortletScope attributes are synchronized
         if (PortletSessionUtil.decodeScope(name) == PortletSession.PORTLET_SCOPE)
         {
            if (trace)
            {
               String id = event.getSession().getId();
               log.trace("Adding attribute " + name + " to session " + id);
            }
            Object value = event.getValue();
            // ss.setAttribute(name, value);
            AttributeModification mod = new AttributeModification(name, value);
            modifications.add(mod);
         }
      }
   }

   public void attributeRemoved(HttpSessionBindingEvent event)
   {
      List modifications = (List)local.get();
      if (modifications != null)
      {
         String name = event.getName();
         // Only PortletScope attributes are synchronized
         if (PortletSessionUtil.decodeScope(name) == PortletSession.PORTLET_SCOPE)
         {
            if (trace)
            {
               String id = event.getSession().getId();
               log.trace("Removing attribute " + name + " to session " + id);
            }
//            ss.setAttribute(name, null);
            AttributeModification mod = new AttributeModification(name, null);
            modifications.add(mod);
         }
      }
   }

   public void attributeReplaced(HttpSessionBindingEvent event)
   {
      List modifications = (List)local.get();
      if (modifications != null)
      {
         String name = event.getName();
         // Only PortletScope attributes are synchronized
         if (PortletSessionUtil.decodeScope(name) == PortletSession.PORTLET_SCOPE)
         {
            if (trace)
            {
               String id = event.getSession().getId();
               log.trace("Replacing attribute " + name + " to session " + id);
            }
            // Get the value from the session as the event payload is the previous value
            Object value = event.getSession().getAttribute(name);
//            ss.setAttribute(name, value);
            AttributeModification mod = new AttributeModification(name, value);
            modifications.add(mod);
         }
      }
   }

   //

   public void sessionCreated(HttpSessionEvent event)
   {
      List modifications = (List)local.get();
      if (modifications != null)
      {
         if (trace)
         {
            String id = event.getSession().getId();
            log.trace("Creating session " + id);
         }
//         ss.create();
         modifications.add(SessionModification.SESSION_CREATED);
      }
   }

   public void sessionDestroyed(HttpSessionEvent event)
   {
      List modifications = (List)local.get();
      if (modifications != null)
      {
         if (trace)
         {
            String id = event.getSession().getId();
            log.trace("Destroying session " + id);
         }
//         ss.destroy();
         modifications.add(SessionModification.SESSION_DESTROYED);
      }
   }

   /** @throws IllegalStateException  */
   public static void activate() throws IllegalStateException
   {
      if (local.get() != null)
      {
         throw new IllegalStateException("Already active");
      }
      local.set(new ArrayList());
   }

   public static List desactivate() throws IllegalStateException
   {
      List modifications = (List)SessionListener.local.get();
      if (modifications == null)
      {
         throw new IllegalStateException("No active");
      }
      SessionListener.local.set(null);
      return modifications;
   }
}
