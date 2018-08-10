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
import org.jboss.portal.server.deployment.PortalWebApp;

import javax.management.MBeanServer;
import java.net.URL;

/**
 * A deployment factory creates deployment for a specific kind of objects.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public interface DeploymentFactory
{
   /**
    * Create a new instance of the deployment.
    *
    * @param url         the URL of the file to deploy
    * @param pwa         the web application that contains the file to deploy or null if it does not exist
    * @param mbeanServer the JMX mbean server
    * @return a deployment ready to be processed
    * @throws DeploymentException if the deployment cannot be created
    */
   Deployment newInstance(URL url, PortalWebApp pwa, MBeanServer mbeanServer) throws DeploymentException;
}
