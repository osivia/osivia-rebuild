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
package org.jboss.portal.portlet.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.container.PortletContainer;
import org.jboss.portal.portlet.container.ContainerPortletInvoker;
import org.jboss.portal.portlet.container.managed.LifeCycleStatus;
import org.jboss.portal.portlet.container.managed.ManagedObject;
import org.jboss.portal.portlet.container.managed.ManagedObjectEvent;
import org.jboss.portal.portlet.container.managed.ManagedObjectLifeCycleEvent;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEvent;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossApplicationMetaData;
import org.jboss.portal.portlet.impl.container.PortletContainerLifeCycle;
import org.jboss.portal.portlet.security.PortletSecurityService;
import org.jboss.portal.server.config.ServerConfig;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.AbstractDeploymentFactory;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.xml.sax.EntityResolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo : remove unused ConfigService
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10337 $
 */
public class PortletAppDeploymentFactory extends AbstractDeploymentFactory
{

   /** . */
   protected static final Pattern urlPattern = Pattern.compile(".*/portlet\\.xml");

   /** . */
   protected ServerConfig config;

   /** . */
   protected PortletSecurityService portletSecurityService;

   /** . */
   protected String standardJBossApplicationMetaDataLocation;

   /** . */
   protected JBossApplicationMetaData standardJBossApplicationMetaData;

   /** . */
   protected EntityResolver jbossPortletEntityResolver;

   private PortletInvoker portletContainerInvoker;

   private InfoBuilderFactory coreInfoBuilderFactory;

   public EntityResolver getJBossPortletEntityResolver()
   {
      return jbossPortletEntityResolver;
   }

   public void setJBossPortletEntityResolver(EntityResolver jbossPortletEntityResolver)
   {
      this.jbossPortletEntityResolver = jbossPortletEntityResolver;
   }

   public void setCoreInfoBuilderFactory(InfoBuilderFactory coreInfoBuilderFactory)
   {
      this.coreInfoBuilderFactory = coreInfoBuilderFactory;
   }

   public InfoBuilderFactory getCoreInfoBuilderFactory()
   {
      return coreInfoBuilderFactory;
   }

   public ServerConfig getConfig()
   {
      return config;
   }

   public void setConfig(ServerConfig config)
   {
      this.config = config;
   }

   public String getStandardJBossApplicationMetaDataLocation()
   {
      return standardJBossApplicationMetaDataLocation;
   }

   public void setStandardJBossApplicationMetaDataLocation(String standardJBossApplicationMetaDataLocation)
   {
      this.standardJBossApplicationMetaDataLocation = standardJBossApplicationMetaDataLocation;
   }

   public JBossApplicationMetaData getStandardJBossApplicationMetaData()
   {
      return standardJBossApplicationMetaData;
   }

   public boolean acceptFile(URL url)
   {
      String urlAsFile = url.getFile();
      Matcher matcher = urlPattern.matcher(urlAsFile);
      return matcher.matches();
   }

   public Deployment newInstance(URL url, PortalWebApp pwa, MBeanServer mbeanServer) throws DeploymentException
   {
      return new PortletAppDeployment(url, pwa, bridgeToInvoker, mbeanServer, this);
   }

   @PostConstruct
   public void start() throws Exception
   {
      //
      loadStandardJBossApplicationMetaData();

      //
      super.start();
   }

   public void loadStandardJBossApplicationMetaData()
   {
      if (standardJBossApplicationMetaDataLocation != null)
      {
         InputStream in = null;
         try
         {
            in = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream(standardJBossApplicationMetaDataLocation));
            if (in != null)
            {
               JBossApplicationMetaDataFactory factory = createJBossApplicationMetaDataFactory();
               Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
               standardJBossApplicationMetaData = (JBossApplicationMetaData)unmarshaller.unmarshal(in, new ValueTrimmingFilter(factory), null);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         finally
         {
            IOTools.safeClose(in);
         }
      }
   }

   @PreDestroy
   public void stop()
   {
      super.stop();
   }

   public PortletSecurityService getPortletSecurityService()
   {
      return portletSecurityService;
   }

   public void setPortletSecurityService(PortletSecurityService portletSecurityService)
   {
      this.portletSecurityService = portletSecurityService;
   }

   /** Subclasses can provide a subclass of JBossApplicationMetaDataFactory. */
   public JBossApplicationMetaDataFactory createJBossApplicationMetaDataFactory()
   {
      return new JBossApplicationMetaDataFactory();
   }

   public PortletInvoker getPortletContainerInvoker()
   {
      return portletContainerInvoker;
   }

   public void setPortletContainerInvoker(PortletInvoker portletContainerInvoker)
   {
      this.portletContainerInvoker = portletContainerInvoker;
   }

   /** Bridge managed object event to add/remove portlet container in portlet container invoker. */
   protected final ManagedObjectRegistryEventListener bridgeToInvoker = new ManagedObjectRegistryEventListener()
   {
      public void onEvent(ManagedObjectRegistryEvent event)
      {
         if (event instanceof ManagedObjectEvent)
         {
            ManagedObjectEvent managedObjectEvent = (ManagedObjectEvent)event;
            ManagedObject managedObject = managedObjectEvent.getManagedObject();

            //
            if (managedObject instanceof PortletContainerLifeCycle)
            {
               PortletContainerLifeCycle portletContainerLifeCycle = (PortletContainerLifeCycle)managedObject;
               PortletContainer portletContainer = portletContainerLifeCycle.getPortletContainer();

               //
               if (managedObjectEvent instanceof ManagedObjectLifeCycleEvent)
               {
                  ManagedObjectLifeCycleEvent lifeCycleEvent = (ManagedObjectLifeCycleEvent)managedObjectEvent;

                  //
                  LifeCycleStatus status = lifeCycleEvent.getStatus();
                  //
                  if (status == LifeCycleStatus.STARTED)
                  {
                     ((ContainerPortletInvoker)portletContainerInvoker).addPortletContainer(portletContainer);
                  }
                  else
                  {
                     ((ContainerPortletInvoker)portletContainerInvoker).removePortletContainer(portletContainer);
                  }
               }
            }
         }
      }
   };

}
