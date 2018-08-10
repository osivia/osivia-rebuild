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
package org.jboss.portal.theme.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.portal.theme.LayoutException;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.xb.binding.UnmarshallerFactory;

import javax.management.MBeanServer;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Deploy rendersets contained in a WebApplication. <p>A Web Application can define render sets in their respective
 * descriptors. Portal RenderSets are defined in a descriptor called portal-renderSet.xml in the WEB-INF/layout folder.
 * Additionally, rendersets can be defined as part of a layout descriptor (portal-layouts.xml). Those cases are handled
 * by the layout deployment.</p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class LayoutFeaturesDeployment extends Deployment
{

   /** . */
   private static Logger log = Logger.getLogger(LayoutFeaturesDeployment.class);

   /** . */
   private final LayoutFeaturesDeploymentFactory factory;

   /** . */
   private boolean trace = false;

   public LayoutFeaturesDeployment(
      URL url,
      PortalWebApp pwa,
      MBeanServer mbeanServer,
      LayoutFeaturesDeploymentFactory factory)
   {
      super(url, pwa, mbeanServer);
      this.factory = factory;
      trace = log.isTraceEnabled();
   }

   /**
    * Create the resources from this portal web application.
    *
    * @throws org.jboss.deployment.DeploymentException
    *          if anything goes wrong
    */
   public void create() throws DeploymentException
   {
      if (trace)
      {
         log.trace("create new renderset(s) found in :  " + pwa.getId());
      }

      try
      {
         boolean isRenderSet = url.toString().endsWith("-renderSet.xml");

         // see if there are any named rendersets

         if (isRenderSet)
         {
            RuntimeContext ctx = new RuntimeContext(pwa.getId(), pwa.getServletContext(), pwa.getContextPath(), pwa.getClassLoader());
            LayoutService layoutService = factory.getLayoutService();
            InputStream stream = null;
            try
            {
               stream = IOTools.safeBufferedWrapper(url.openStream());
               if (isRenderSet)
               {
                  List renderSets = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(stream, new RenderSetMetaDataFactory(), null);
                  for (int i = 0; i < renderSets.size(); i++)
                  {
                     RenderSetMetaData renderSet = (RenderSetMetaData)renderSets.get(i);
                     layoutService.addRenderSet(ctx, renderSet);
                  }
               }
            }
            finally
            {
               IOTools.safeClose(stream);
            }
         }
         else
         {
            if (trace)
            {
               log.trace("can't handle deployment of url :" + url);
            }
         }

         if (trace)
         {
            log.trace("done creating new renderset(s) found in :  " + pwa.getId());
         }
      }
      catch (Exception e)
      {
         throw new DeploymentException(e);
      }
   }

   /**
    * Destroy (and remove) all the resources from this portal web application that are registered with the portal.
    *
    * @throws org.jboss.deployment.DeploymentException
    *          if anything goes wrong
    */
   public void destroy() throws DeploymentException
   {
      if (trace)
      {
         log.trace("destroying renderset(s) found in :  " + pwa.getId());
      }
      try
      {
         LayoutService service = factory.getLayoutService();
         service.removeRenderSets(pwa.getId());
      }
      catch (LayoutException e)
      {
         throw new DeploymentException(e);
      }
      if (trace)
      {
         log.trace("done destroying renderset(s) found in :  " + pwa.getId());
      }
   }
}
