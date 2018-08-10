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
package org.jboss.portal.server.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.server.deployment.PortalWebApp;

import javax.management.MBeanServer;
import java.net.URL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DeploymentContext
{

   /** . */
   private static final Logger log = Logger.getLogger(DeploymentContext.class);

   /** The factory that takes care of this deployment. */
   private final DeploymentFactoryContext factoryCtx;

   /** The deployment URL. */
   private final URL url;

   /** The provided deployment. */
   private Deployment deployment;

   /** The deployment short name. */
   private String name;

   /** . */
   private PortalWebApp pwa;

   /** . */
   private MBeanServer server;

   public DeploymentContext(DeploymentFactoryContext factoryCtx, PortalWebApp pwa, URL url, MBeanServer server)
   {
      this.factoryCtx = factoryCtx;
      this.pwa = pwa;
      this.url = url;
      this.server = server;
   }

   public URL getURL()
   {
      return url;
   }

   public DeploymentFactoryContext getFactoryContext()
   {
      return factoryCtx;
   }

   public void create() throws DeploymentException
   {
      log.debug("Instantiating deployment for url " + url + " by factory " + factoryCtx.getName());
      deployment = factoryCtx.getFactory().newInstance(url, pwa, server);
      name = Tools.getShortNameOf(deployment.getClass());

      //
      log.debug("Create step for deployment " + name + " for url " + url);
      deployment.create();
   }

   public void start() throws DeploymentException
   {
      if (deployment == null)
      {
         log.debug("Cannot start deployment of factory " + factoryCtx.getName() + " for url " + url + " because it is null");
      }
      else
      {
         log.debug("Start step for deployment " + name + " for url " + url);
         deployment.start();
      }
   }

   public void stop() throws DeploymentException
   {
      if (deployment == null)
      {
         log.debug("Cannot stop deployment of factory " + factoryCtx.getName() + " for url " + url + " because it is null");
      }
      else
      {
         log.debug("Stop step for deployment " + name + " for url " + url);
         deployment.stop();
      }
   }

   public void destroy() throws DeploymentException
   {
      if (deployment == null)
      {
         log.debug("Cannot destroy deployment of factory " + factoryCtx.getName() + " for url " + url + " because it is null");
      }
      else
      {
         log.debug("Destroy step for deployment " + name + " for url " + url);
         deployment.destroy();
      }
   }
}
