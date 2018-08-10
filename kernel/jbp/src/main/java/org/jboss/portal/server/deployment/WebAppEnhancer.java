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
package org.jboss.portal.server.deployment;

import org.jboss.mx.util.MBeanProxy;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.server.config.ServerConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.management.ObjectName;
import java.util.Iterator;

/**
 * The role of this object is to modify the web application so it is possible to invoke it by request distpatching.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class WebAppEnhancer extends WebAppIntercepter
{

   /** . */
   private ServerConfig config;

   public ServerConfig getConfig()
   {
      return config;
   }

   public void setConfig(ServerConfig config)
   {
      this.config = config;
   }

   protected void deploy(PortalWebApp pwa)
   {
      try
      {
         // Instrument war file first
         pwa.instrument();

         // Inject proxies in the servlet context
         Document desc = pwa.getDescriptor();
         if (desc != null)
         {
            Element jbossAppElt = desc.getDocumentElement();
            for (Iterator i = XMLTools.getChildren(jbossAppElt, "service").iterator(); i.hasNext();)
            {
               Element serviceElt = (Element)i.next();

               //
               log.debug("About to inject a service in the servlet context of " + pwa.getURL());

               //
               Element serviceNameElt = XMLTools.getUniqueChild(serviceElt, "service-name", true);
               Element serviceClassElt = XMLTools.getUniqueChild(serviceElt, "service-class", true);
               Element serviceRefElt = XMLTools.getUniqueChild(serviceElt, "service-ref", true);
               String serviceName = XMLTools.asString(serviceNameElt);
               String serviceClass = XMLTools.asString(serviceClassElt);
               String serviceRef = XMLTools.asString(serviceRefElt);

               //
               if (serviceRef.startsWith(":"))
               {
                  log.debug("Detecting a relative service reference " + serviceRef + " prepending it with " + config.getDomain());
                  serviceRef = config.getDomain() + serviceRef;
               }

               //
               Class proxyClass = pwa.getClassLoader().loadClass(serviceClass);
               ObjectName objectName = ObjectName.getInstance(serviceRef);
               Object proxy = MBeanProxy.get(proxyClass, objectName, server);

               //
               log.debug("Want to inject " + serviceRef + " with class " + proxy + " and name " + serviceName);
               pwa.getServletContext().setAttribute(serviceName, proxy);
            }
         }

      }
      catch (Exception e)
      {
         log.error("Cannot instrument the web application", e);
      }
   }

   protected void undeploy(PortalWebApp jwa)
   {
   }
}
