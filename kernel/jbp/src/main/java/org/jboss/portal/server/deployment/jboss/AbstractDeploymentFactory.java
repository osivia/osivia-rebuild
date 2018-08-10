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

import org.jboss.logging.Logger;
import org.jboss.portal.common.net.URLFilter;
import org.jboss.portal.common.util.Tools;

import java.net.URL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10284 $
 */
public abstract class AbstractDeploymentFactory implements DeploymentFactory, URLFilter
{

   /** The server deployer. */
   protected ServerDeployerMBean deployer;

   /** True if the service lifecycle controls the registration against the server deployer. */
   protected boolean registrationControlledByService;

   /** The location of the optional setup file. */
   protected String setupLocation;

   /** The setup url constructed from the setup location. */
   protected URL setupURL;

   /** The short name. */
   protected final String name;

   /** The logger. */
   protected final Logger log = Logger.getLogger(getClass());

   /** The default constructor initialize the service with the registration controlled by the service. */
   protected AbstractDeploymentFactory()
   {
      super();
      deployer = null;
      registrationControlledByService = true;
      setupLocation = null;
      name = Tools.getShortNameOf(getClass());
   }

   public String getName()
   {
      return name;
   }

   /** Accept only the WEB-INF directory by default. */
   public boolean acceptDir(URL url)
   {
      return url.getPath().endsWith("/WEB-INF/");
   }

   public String getSetupLocation()
   {
      return setupLocation;
   }

   public void setSetupLocation(String setupLocation)
   {
      this.setupLocation = setupLocation;
   }

   public boolean isRegistrationControlledByService()
   {
      return registrationControlledByService;
   }

   public void setRegistrationControlledByService(boolean registrationControlledByService)
   {
      this.registrationControlledByService = registrationControlledByService;
   }

   public ServerDeployerMBean getDeployer()
   {
      return deployer;
   }

   public void setDeployer(ServerDeployerMBean deployer)
   {
      this.deployer = deployer;
   }

   /**
    * Register the factory against the server deployer and kick off the deployment of the urls that this factory can
    * deploy.
    */
   public void registerFactory()
   {
      deployer.registerFactory(
         name,
         this,
         this,
         setupURL
      );
   }

   /**
    * Unregister the factory against the server deployer and shutdown the deployment of the urls that this factory can
    * deploy.
    */
   public void unregisterFactory()
   {
      deployer.unregisterFactory(name);
   }

   public URL getSetupURL()
   {
      return setupURL;
   }

   /**
    * Set the setup url with the resource obtained from the thread context class loader using the value of setup
    * location if this one is not null.
    */
   public void create() throws Exception
   {
      if (setupLocation != null)
      {
         setupURL = Thread.currentThread().getContextClassLoader().getResource(setupLocation);
      }
   }

   /** Register the factory only if it is controlled by the service lifecycle. */
   public void start() throws Exception
   {
      if (registrationControlledByService)
      {
         registerFactory();
      }
   }

   /** Unregister the factory only if it is controlled by the service lifecycle. */
   public void stop()
   {
      if (registrationControlledByService)
      {
         unregisterFactory();
      }
   }

   /** Set to null the setup url. */
   public void destroy() throws Exception
   {
      setupURL = null;
   }
}
