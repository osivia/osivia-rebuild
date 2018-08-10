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

import org.jboss.invocation.MarshalledValue;
import org.jboss.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A subsession keep tracks of the content of a given portlet session.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class SubSession implements Externalizable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -3584568048652090636L;

   /** . */
   private static final Logger log = Logger.getLogger(SubSession.class);

   /** . */
   private boolean trace = log.isTraceEnabled();

   /** Signals that the session has been activated. */
   private boolean activated;

   /** The session content. */
   private Map map;

   /** The id. */
   private String id;

   public SubSession(String id)
   {
      if (id == null)
      {
         throw new IllegalArgumentException();
      }
      this.id = id;
      this.activated = false;
   }

   public SubSession()
   {
   }

   public String getId()
   {
      return id;
   }

   public boolean isActivated()
   {
      return activated;
   }

   public void setActivated(boolean activated)
   {
      this.activated = activated;
   }

   public Set getAttributeNames()
   {
      if (map != null)
      {
         return map.keySet();
      }
      else
      {
         return Collections.EMPTY_SET;
      }
   }

   public Object getAttribute(String name)
   {
      trace("getAttribute: trying to get attribute named: '" + name + "'");
      if (map != null)
      {
         return map.get(name);
      }
      else
      {
         trace("getAttribute: no existing attributes.");
         return null;
      }
   }

   public void setAttribute(String name, Object value)
   {
      if (map == null)
      {
         map = new HashMap();
         trace("setAttribute: no existing attributes, creating attribute map.");
      }
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("Must pass a valid, non-null attribute to set the attribute value.");
      }
      if (value == null)
      {
         trace("setAttribute: removing attribute named: '" + name + "'");
         map.remove(name);
      }
      else
      {
         trace("setAttribute: set attribute named: '" + name + "' to value: '" + value + "'");
         map.put(name, value);
      }
   }

   public void create()
   {
      trace("create");
      if (map != null)
      {
         trace("create: was expecting no map, had to clear it");
         map.clear();
      }
      else
      {
         trace("create: attribute map created");
         map = new HashMap();
      }
   }

   public void destroy()
   {
      trace("destroy");
      if (map == null)
      {
         trace("destroy: was expecting an attribute map");
      }
      else
      {
         trace("destroy: attribute map destroyed");
         map = null;
      }
   }

   /**
    * @param portalRequest
    * @param key
    */
   public void synchronizeWithPortalSession(HttpServletRequest portalRequest, List modifications, String key)
   {
      // Apply changes
      for (Iterator i = modifications.iterator(); i.hasNext();)
      {
         Modification mod = (Modification)i.next();
         if (mod instanceof AttributeModification)
         {
            try
            {
               AttributeModification attrMod = (AttributeModification)mod;
               setAttribute(attrMod.getName(), new MarshalledValue(attrMod.getValue()));
            }
            catch (IOException e)
            {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
         }
         else if (mod == SessionModification.SESSION_CREATED)
         {
            create();
         }
         else
         {
            destroy();
         }
      }

      //
      if (modifications.isEmpty() == false)
      {
         // Performs an explicit set
         HttpSession session = portalRequest.getSession();
         session.setAttribute(key, this);
      }
   }

   /**
    * Synchronize the content with the session held by the dispatched request. This method will use the thread context
    * classloader to unserialize the content of the session.
    *
    * @param dispatchedRequest
    */
   public void synchronizeWithDispatchedSession(HttpServletRequest dispatchedRequest)
   {
      if (activated)
      {
         trace("synchronizeWithDispatchedSession: session was activated, synchronizing...");
         activated = false;

         //
         HttpSession session = dispatchedRequest.getSession();

         //
         trace("synchronizeWithDispatchedSession: removing existing attributes");
         ArrayList names = new ArrayList();
         for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();)
         {
            String name = (String)e.nextElement();
            names.add(name);
         }
         for (int i = 0; i < names.size(); i++)
         {
            String name = (String)names.get(i);
            trace("synchronizeWithDispatchedSession: removing attribute named: '" + name + "' with value: '"
               + session.getAttribute(name) + "' from existing session");
            session.removeAttribute(name);
         }

         //
         for (Iterator i = map.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry)i.next();
            String name = (String)entry.getKey();

            //
            try
            {
               MarshalledValue marshalledValue = (MarshalledValue)entry.getValue();
               Object value = marshalledValue.get();

               //
               session.setAttribute(name, value);
               trace("synchronizeWithDispatchedSession: setting attribute name: '" + name + "' to value: '" + value
                  + "'");
            }
            catch (Exception e)
            {
               log.error("synchronizeWithDispatchedSession: couldn't unmarshall value for attribute named: '" + name
                  + "'. Session won't be properly replicated!", e);
            }
         }
      }
      else
      {
         trace("synchronizeWithDispatchedSession: session was not activated, did nothing");
      }
   }

//   private void marshallAttributesAndUpdateSessionIfNeeded(HttpSession session)
//   {
//      if (map != null)
//      {
//         for (Iterator i = map.entrySet().iterator(); i.hasNext();)
//         {
//            Map.Entry entry = (Map.Entry)i.next();
//            String name = (String)entry.getKey();
//            MarshalledValue marshalledValue = (MarshalledValue)entry.getValue();
//
//            //
//            try
//            {
//               Object value = marshalledValue.get();
//               entry.setValue(value); // replace marshalled value by original one
//               trace("synchronizeWithDispatchedSession: setting attribute name: '" + name + "' to value: '" + value
//                  + "'");
//
//               // update session if requested
//               if (session != null)
//               {
//                  session.setAttribute(name, value);
//               }
//            }
//            catch (Exception e)
//            {
//               log.error("synchronizeWithDispatchedSession: couldn't unmarshall value for attribute named: '" + name
//                  + "'. Session won't be properly replicated!", e);
//            }
//         }
//      }
//   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      id = in.readUTF();
      map = (Map)in.readObject();
      activated = true;
      if (trace)
      {
         log("SubSession deserialized");
      }
   }

   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeUTF(id);
      out.writeObject(map);
      if (trace)
      {
         log("SubSession serialized");
      }
   }

   private void log(String prefix)
   {
      StringBuffer tmp = new StringBuffer(prefix).append(" [");
      for (Iterator i = map.keySet().iterator(); i.hasNext();)
      {
         String key = (String)i.next();
         tmp.append(key).append("(").append(map.get(key)).append(")").append(i.hasNext() ? "," : "");
      }
      tmp.append("]");
      trace(tmp.toString());
   }

   private void trace(String message)
   {
      if (trace)
      {
         log.trace(message);
      }
   }
}
