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

import org.apache.log4j.Logger;
import org.jboss.deployment.DeploymentException;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.portal.server.deployment.PortalWebApp;

import javax.management.MBeanServer;
import java.net.URL;

/**
 * Extends the JBoss AS deployment info to have more contextual information.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PortalDeploymentInfo extends DeploymentInfo
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 4911467978162887964L;



   /** The optional web application containing that deployment info. */
   public final PortalWebApp pwa;

   public PortalDeploymentInfo(URL url, MBeanServer server) throws DeploymentException
   {
      this(url, null, null, server);
   }

   public PortalDeploymentInfo(URL url, DeploymentInfo parent, MBeanServer server) throws DeploymentException
   {
      this(url, parent, null, server);
   }

   public PortalDeploymentInfo(URL url, DeploymentInfo parent, PortalWebApp pwa, MBeanServer server) throws DeploymentException
   {
      super(url, parent, server);
      this.pwa = pwa;

   }

   public PortalDeploymentInfo(URL url, PortalDeploymentInfo parent, MBeanServer server) throws DeploymentException
   {
      this(url, parent, parent.pwa, server);
   }
}
