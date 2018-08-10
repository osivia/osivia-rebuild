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

import org.jboss.deployment.DeploymentException;
import org.jboss.portal.server.deployment.jboss.ServerDeployerMBean;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class WebAppAdapter
   extends WebAppIntercepter
{

   /** The specific portal web app deployer. */
   private ServerDeployerMBean deployer;

   public ServerDeployerMBean getDeployer()
   {
      return deployer;
   }

   public void setDeployer(ServerDeployerMBean deployer)
   {
      this.deployer = deployer;
   }

   protected void deploy(PortalWebApp pwa)
   {
      try
      {
         deployer.deploy(pwa);
      }
      catch (DeploymentException e)
      {
//         JBossTestAgent.record(e);
      }
   }

   protected void undeploy(PortalWebApp pwa)
   {
      try
      {
         deployer.undeploy(pwa);
      }
      catch (DeploymentException e)
      {
//         JBossTestAgent.record(e);
      }
   }
}
