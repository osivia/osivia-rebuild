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
import org.jboss.portal.common.xml.NullEntityResolver;
import org.jboss.portal.portlet.container.PortletApplicationContext;
import org.jboss.portal.portlet.container.PortletContainerContext;
import org.jboss.portal.portlet.container.PortletFilterContext;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.portlet.container.object.PortletApplicationObject;
import org.jboss.portal.portlet.container.object.PortletContainerObject;
import org.jboss.portal.portlet.container.object.PortletFilterObject;
import org.jboss.portal.portlet.deployment.PortletApplicationModelFactory;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossApplicationMetaData;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossPortletMetaData;
import org.jboss.portal.portlet.impl.container.PortletApplicationLifeCycle;
import org.jboss.portal.portlet.impl.container.PortletContainerLifeCycle;
import org.jboss.portal.portlet.impl.container.PortletFilterLifeCycle;
import org.jboss.portal.portlet.impl.info.ContainerFilterInfo;
import org.jboss.portal.portlet.impl.info.ContainerPortletInfo;
import org.jboss.portal.portlet.impl.jsr168.PortletApplicationImpl;
import org.jboss.portal.portlet.impl.jsr168.PortletContainerImpl;
import org.jboss.portal.portlet.impl.jsr168.PortletFilterImpl;
import org.jboss.portal.portlet.impl.metadata.PortletApplication10MetaData;
import org.jboss.portal.portlet.impl.metadata.PortletApplication20MetaData;
import org.jboss.portal.portlet.impl.metadata.portlet.PortletMetaData;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.xml.sax.EntityResolver;

import javax.management.MBeanServer;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12427 $
 */
public class PortletAppDeployment extends Deployment
{

   /** The existing configurations. */
   protected Map configurations;

   /** . */
   protected PortletApplication10MetaData portletAppMD;

   /** . */
   protected JBossApplicationMetaData jbossAppMD;

   /** . */
   protected PortletAppDeploymentFactory factory;

   /** . */
   protected PortletApplicationContextImpl portletApplicationContext;

   /** . */
   private PortletApplicationLifeCycle portletApplicationLifeCycle;

   /** . */
   private ManagedObjectRegistryEventListener listener;

   /** . */
   private String appId;

   public PortletAppDeployment(URL url, PortalWebApp pwa, ManagedObjectRegistryEventListener listener, MBeanServer mbeanServer, PortletAppDeploymentFactory factory)
   {
      super(url, pwa, mbeanServer);
      this.factory = factory;
      this.listener = listener;
   }

   public void create() throws DeploymentException
   {
      //
      try
      {
         portletAppMD = buildPortletApplicationMetaData();
         jbossAppMD = buildJBossApplicationMetaData();

         // TLD import needs portlet MD
         importTLD();

         //
         if (jbossAppMD.getId() == null)
         {
            log.debug("The portlet application does not have an explicit id value, will use the value provided by the jboss-app.xml instead");
            jbossAppMD.setId(pwa.getId());
         }

         //
         appId = jbossAppMD.getId();

         // Merge or provide defaults
         JBossApplicationMetaData standardJBossAppMD = factory.getStandardJBossApplicationMetaData();
         if (standardJBossAppMD != null)
         {
            log.debug("Found standard jboss app meta data");

            JBossPortletMetaData defaultJBossPortletMD = standardJBossAppMD.getPortlets().get("DefaultPortlet");
            if (defaultJBossPortletMD != null)
            {
               log.debug("Found default jboss portlet meta data");
               for (PortletMetaData portletMD : portletAppMD.getPortlets().values())
               {
                  String name = portletMD.getPortletName();
                  JBossPortletMetaData jbossPortletMD = jbossAppMD.getPortlets().get(name);
                  if (jbossPortletMD != null)
                  {
                     log.debug("Merging default jboss portlet meta data for " + name);

                     // Merge with app settings
//                     jbossPortletMD.merge(jbossAppMD); // todo: remove since it shouldn't be needed anymore with the adding of merge in factory...

                     // Merge with default portlet
                     jbossPortletMD.merge(defaultJBossPortletMD);
                  }
                  else
                  {
                     log.debug("Using default jboss portlet meta data for " + name);
                     jbossAppMD.getPortlets().put(name, (JBossPortletMetaData)defaultJBossPortletMD.clone());
                  }
               }
            }
         }
      }
      catch (Throwable e)
      {
         throw new DeploymentException("Cannot deploy portlet application", e);
      }
   }

   public void start() throws DeploymentException
   {

      log.debug("Starting installation");

      //
      InfoBuilder infoBuilder = factory.getCoreInfoBuilderFactory().createInfoBuilder(pwa, jbossAppMD, portletAppMD);

      infoBuilder.build();
      /*
      ContainerInfoBuilderContext builderContext = new ContainerInfoBuilderContextImpl(portletAppMD, pwa);
      ContainerInfoBuilder builder = new ContainerInfoBuilder(pwa.getContextPath(), portletAppMD, builderContext);
      builder.build();
      */
      //
      PortletApplicationObject portletApplicationObject = new PortletApplicationImpl(infoBuilder.getApplication());
      PortletApplicationContext portletApplicationContext = new PortletApplicationContextImpl(pwa);

      //
      portletApplicationLifeCycle = new PortletApplicationLifeCycle(
         listener,
         portletApplicationContext,
         portletApplicationObject);

      //
      for (ContainerFilterInfo filterInfo : infoBuilder.getApplication().getFilters().values())
      {
         PortletFilterObject portletFilterObject = new PortletFilterImpl(filterInfo);
         PortletFilterContext portletFilterContext = new PortletFilterContextImpl();

         //
         portletApplicationLifeCycle.addPortletFilter(portletFilterContext, portletFilterObject);
      }

      //
      for (PortletInfo portletInfo : infoBuilder.getPortlets())
      {
         ContainerPortletInfo cpi = (ContainerPortletInfo)portletInfo;
         PortletContainerObject portletContainerObject = new PortletContainerImpl(cpi);
         PortletContainerContext portletContainerContext = new PortletContainerContextImpl();

         //
         PortletContainerLifeCycle portletContainerLifeCycle = portletApplicationLifeCycle.addPortletContainer(portletContainerContext, portletContainerObject);

         // Now create deps
         for (String filterRef : cpi.getFilterRefs())
         {
            PortletFilterLifeCycle portletFilterLifeCycle = portletApplicationLifeCycle.getManagedPortletFilter(filterRef);

            //
            if (portletFilterLifeCycle != null)
            {
               portletApplicationLifeCycle.addDependency(portletFilterLifeCycle, portletContainerLifeCycle);
            }
            else
            {
               // todo
            }
         }
      }

      //
      portletApplicationLifeCycle.create();

      //
      portletApplicationLifeCycle.managedStart();

      //

      /*
            try
            {
               portletApplicationContext = new PortletApplicationContextImpl(
                  factory.getWebAppRegistry(),
                  factory.getPortletAPIFactory(),
                  factory.getPortletInfoFactory(),
                  portletAppMD,
                  jbossAppMD,
                  pwa.getServletContext(),
                  pwa.getClassLoader(),
                  pwa.getContextPath()
               );

               // Install portlet containers
               portletApplicationContext.startPortletApplication();
            }
            catch (Throwable e)
            {
               throw new DeploymentException("Cannot deploy portlet application", e);
            }

            // Configure security
            PortletSecurityService portletSecurityService = factory.getPortletSecurityService();
            if (portletSecurityService != null)
            {
               AuthorizationDomain authDomain = portletSecurityService.getAuthorizationDomain();
               DomainConfigurator domainConfigurator = authDomain.getConfigurator();

               //
               for (Iterator i = jbossAppMD.getPortlets().values().iterator(); i.hasNext();)
               {
                  JBossPortletMetaData portletMD = (JBossPortletMetaData)i.next();
                  SecurityConstraintMetaData securityConstraintMD = portletMD.getSecurityConstraint();

                  //
                  if (securityConstraintMD == null)
                  {
                     securityConstraintMD = new SecurityConstraintMetaData();
                     PolicyPermissionMetaData policyPermission = new PolicyPermissionMetaData();
                     policyPermission.setRoleName(SecurityConstants.UNCHECKED_ROLE_NAME);
                     policyPermission.getActions().add("view");
                     securityConstraintMD.getPolicyPermissions().put(SecurityConstants.UNCHECKED_ROLE_NAME, policyPermission);
                  }

                  //
                  Set bindings = new HashSet();
                  for (Iterator j = securityConstraintMD.getPolicyPermissions().values().iterator(); j.hasNext();)
                  {
                     PolicyPermissionMetaData policyPermissionMD = (PolicyPermissionMetaData)j.next();
                     RoleSecurityBinding binding = new RoleSecurityBinding(policyPermissionMD.getActions(), policyPermissionMD.getRoleName());
                     bindings.add(binding);
                  }

                  //
                  try
                  {
                     String portletId = pwa.getId() + "." + portletMD.getName();
                     domainConfigurator.setSecurityBindings(portletId, bindings);
                  }
                  catch (SecurityConfigurationException e)
                  {
                     log.error("Error in configuration", e);
                  }
               }
            }

      */
      //
      super.start();
   }

   public void stop() throws DeploymentException
   {

      /*
            PortletSecurityService portletSecurityService = factory.getPortletSecurityService();
            if (portletSecurityService != null)
            {
               AuthorizationDomain authDomain = portletSecurityService.getAuthorizationDomain();
               DomainConfigurator domainConfigurator = authDomain.getConfigurator();
               for (Iterator i = portletAppMD.getPortlets().values().iterator(); i.hasNext();)
               {
                  PortletMetaData portletMD = (PortletMetaData)i.next();

                  //
                  try
                  {
                     String portletId = pwa.getId() + "." + portletMD.getId();
                     domainConfigurator.removeSecurityBindings(portletId);
                  }
                  catch (SecurityConfigurationException e)
                  {
                     log.error("Error in configuration", e);
                  }
               }
            }
      */
      //
      super.stop();
   }

   public void destroy() throws DeploymentException
   {
      portletApplicationLifeCycle.destroy();

//      portletApplicationContext.stopPortletApplication();
   }

   /** Import the portlet jsp tag TLD in the deployed application. */
   protected void importTLD()
   {
      InputStream sourceTLD = null;
      try
      {

         String tldName;
         if (portletAppMD instanceof PortletApplication20MetaData)
         {
            sourceTLD = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/portlet_2_0.tld"));
            tldName = "portlet_2_0.tld";
         }
         else
         {
            sourceTLD = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/portlet.tld"));
            tldName = "portlet.tld";
         }
         pwa.importFile("/WEB-INF", tldName, sourceTLD, false);
      }
      catch (IOException e)
      {
         log.warn("Cannot copy TLD file to the portlet application", e);
      }
      finally
      {
         IOTools.safeClose(sourceTLD);
      }
   }

   protected PortletApplication10MetaData buildPortletApplicationMetaData() throws Exception
   {
      InputStream in = null;
      try
      {
         in = IOTools.safeBufferedWrapper(url.openStream());
         PortletApplicationModelFactory factory = new PortletApplicationModelFactory();
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
         PortletApplication10MetaData portletAppMD = (PortletApplication10MetaData)unmarshaller.unmarshal(in, new ValueTrimmingFilter(factory), null);
         portletAppMD.setId(pwa.getId());
         return portletAppMD;
      }
      finally
      {
         IOTools.safeClose(in);
      }
   }

   protected JBossApplicationMetaData buildJBossApplicationMetaData()
   {
      JBossApplicationMetaData jbossAppMD = new JBossApplicationMetaData();
      ServletContext servletContext = pwa.getServletContext();
      String contextPath = pwa.getContextPath();
      InputStream in = null;
      try
      {
         in = IOTools.safeBufferedWrapper(servletContext.getResourceAsStream("/WEB-INF/jboss-portlet.xml"));
         if (in != null)
         {
            log.info("Parsing " + contextPath + "/jboss-portlet.xml");

            //
            JBossApplicationMetaDataFactory factory = this.factory.createJBossApplicationMetaDataFactory();
            Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
            EntityResolver entityResolver = this.factory.getJBossPortletEntityResolver();
            if (entityResolver == null)
            {
               log.debug("Could not obtain entity resolver for jboss-portlet.xml");
               entityResolver = new NullEntityResolver();
            }
            else
            {
               log.debug("Obtained entity resolver " + entityResolver + " for jboss-portlet.xml");
            }
            unmarshaller.setEntityResolver(entityResolver);
            jbossAppMD = (JBossApplicationMetaData)unmarshaller.unmarshal(in, new ValueTrimmingFilter(factory), null);
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
      return jbossAppMD;
   }

   public String getAppId()
   {
      return appId;
   }
}
