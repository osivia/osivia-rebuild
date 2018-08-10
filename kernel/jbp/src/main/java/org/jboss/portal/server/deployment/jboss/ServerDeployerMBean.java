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
import org.jboss.deployment.SubDeployerMBean;
import org.jboss.portal.common.net.URLFilter;
import org.jboss.portal.server.Server;
import org.jboss.portal.server.deployment.PortalWebApp;

import java.net.URL;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public interface ServerDeployerMBean extends SubDeployerMBean
{
   /**
    */
   ServerDeployer getDeployer();

   /**
    */
   Server getPortalServer();

   /**
    */
   void setPortalServer(Server server);

   /**
    */
   void deploy(PortalWebApp pwa) throws DeploymentException;

   /**
    */
   void undeploy(PortalWebApp pwa) throws DeploymentException;

   /**
    */
   Set getFactories();

   /**
    * @param name     the factory name
    * @param filter   filter urls
    * @param factory  create deployment
    * @param setupURL an optional setup URL
    */
   void registerFactory(
      String name,
      URLFilter filter,
      DeploymentFactory factory,
      URL setupURL);

   /** @param name the factory name to unregister */
   void unregisterFactory(String name);


   /** Locate a deployment info factory based on the url. If no factory is found it returns null. */
   DeploymentFactory findFactory(URL url);

}
