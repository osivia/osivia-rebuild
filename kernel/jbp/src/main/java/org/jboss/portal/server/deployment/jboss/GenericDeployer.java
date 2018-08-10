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
import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployerSupport;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class GenericDeployer
   extends SubDeployerSupport
   implements GenericDeployerMBean
{

   /** . */
   private String[] acceptedSuffixes;

   /** . */
   private ServerDeployerMBean serverDeployer;

   public ServerDeployerMBean getServerDeployer()
   {
      return serverDeployer;
   }

   public void setServerDeployer(ServerDeployerMBean serverDeployer)
   {
      this.serverDeployer = serverDeployer;
   }

   public String[] getAcceptedSuffixes()
   {
      return acceptedSuffixes;
   }

   public void setAcceptedSuffixes(String[] acceptedSuffixes)
   {
      this.acceptedSuffixes = acceptedSuffixes;
   }

   public boolean accepts(DeploymentInfo di)
   {
      String urlStr = di.url.toString();
      for (int i = 0; i < acceptedSuffixes.length; i++)
      {
         String suffix = acceptedSuffixes[i];
         if (urlStr.endsWith(suffix))
         {
            return true;
         }
      }
      return false;
   }

   public void init(DeploymentInfo di) throws DeploymentException
   {
//      DeploymentFactory factory = serverDeployer.findFactory(di.url);
//      if (factory == null)
//      {
//         // Warn
//      }
//      else
//      {
//         Deployment deployment = factory.newInstance(di.url, null, server);
//         di.metaData = deployment;
//      }
   }

   public void create(DeploymentInfo di) throws DeploymentException
   {
//      ((Deployment)di.metaData).create();
   }

   public void start(DeploymentInfo di) throws DeploymentException
   {
//      ((Deployment)di.metaData).start();
   }

   public void stop(DeploymentInfo di) throws DeploymentException
   {
//      ((Deployment)di.metaData).stop();
   }

   public void destroy(DeploymentInfo di) throws DeploymentException
   {
//      ((Deployment)di.metaData).destroy();
   }
}
