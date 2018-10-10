package org.jboss.portal.server.deployment.jboss;

import org.apache.log4j.Logger;
import org.jboss.deployment.DeploymentException;
import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.SubDeployerSupport;

import org.jboss.portal.common.net.URLFilter;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEvent;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.portlet.mc.PortletApplicationDeployer;
import org.jboss.portal.server.Server;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.Tomcat8WebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.portal.server.deployment.jboss.DeploymentFactory;
import org.jboss.portal.server.deployment.jboss.DeploymentFactoryContext;
import org.jboss.portal.server.deployment.jboss.PortalDeploymentInfo;
import org.jboss.portal.server.deployment.jboss.PortalDeploymentInfoContext;
import org.jboss.portal.server.deployment.jboss.ServerDeployerMBean;
import org.jboss.portal.web.ServletContainerFactory;
import org.jboss.portal.web.WebAppEvent;
import org.jboss.portal.web.WebAppLifeCycleEvent;
import org.jboss.portal.web.WebAppListener;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ServerDeployer extends SubDeployerSupport implements ServerDeployerMBean, WebAppListener, ManagedObjectRegistryEventListener {

	 /** The loggger. */
	   private final Logger log;

	   /** The server. */
	   private Server portalServer;

	   /** The factory contexts. */
	   private Map factoryContexts;

	   /** The portal deployment info context map. */
	   private Map infoContexts;

	   
	   private ServletContainerFactory servletContainerFactory;
	   
	   PortletApplicationDeployer appDeployer;
	   
	   
	public PortletApplicationDeployer getAppDeployer() {
		return appDeployer;
	   }

	public void setAppDeployer(PortletApplicationDeployer appDeployer) {
		this.appDeployer = appDeployer;
	}


	public ServletContainerFactory getServletContainerFactory() {
		return servletContainerFactory;
	}

	public void setServletContainerFactory(ServletContainerFactory servletContainerFactory) {
		this.servletContainerFactory = servletContainerFactory;
	}

	public ServerDeployer()
	   {
	      log = Logger.getLogger(ServerDeployer.class);
	      factoryContexts = Collections.synchronizedMap(new HashMap());
	      infoContexts = Collections.synchronizedMap(new HashMap());
	   }

	   public Set getFactories()
	   {
	      return null;
	   }

	   /** This should never be called for server deployment. */
	   public boolean accepts(DeploymentInfo di)
	   {
	      return false;
	   }

	   public Server getPortalServer()
	   {
	      return portalServer;
	   }

	   public void setPortalServer(Server portalServer)
	   {
	      this.portalServer = portalServer;
	   }

	   public ServerDeployer getDeployer()
	   {
	      return this;
	   }

	   public void registerFactory(String name, URLFilter filter, DeploymentFactory factory, URL setupURL)
	   {
	      log.debug("Registering factory " + name);

	      // Check against dual registration
	      if (factoryContexts.containsKey(name))
	      {
	         throw new IllegalArgumentException("Attempty to register a factory twice " + name);
	      }

	      // Add the factory
	      DeploymentFactoryContext factoryCtx = new DeploymentFactoryContext(name, filter, factory, setupURL);
	      factoryContexts.put(name, factoryCtx);
	      log.debug("Added factory " + name);

	      // Process the setup URL
	      if (setupURL != null && URLTools.exists(setupURL))
	      {
	         try
	         {
	            log.debug("Found valid setup url to deploy provided by factory " + name + " : " + setupURL);
	            PortalDeploymentInfo pdi = new PortalDeploymentInfo(setupURL, server);
	            pdi.deployer = this;
	            infoContexts.put(pdi.url, new PortalDeploymentInfoContext(pdi));
	         }
	         catch (DeploymentException e)
	         {
	            log.error("Failed to deploy setup url " + setupURL);
	         }
	      }

	      //
	      for (Iterator i = new ArrayList(infoContexts.values()).iterator(); i.hasNext();)
	      {
	         PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)i.next();
	         try
	         {
	            log.debug("Adding factory " + name + " to pdi " + pdiCtx.getInfo().url);
	            pdiCtx.add(factoryCtx, true);
	         }
	         catch (DeploymentException e)
	         {
	            log.error("Failed to deploy url " + pdiCtx.getInfo().url);
	         }
	      }
	   }

	   public void unregisterFactory(String name)
	   {
	      log.debug("Unregistering factory " + name);

	      //
	      DeploymentFactoryContext factoryCtx = (DeploymentFactoryContext)factoryContexts.remove(name);
	      if (factoryCtx == null)
	      {
	         throw new IllegalArgumentException("Cannot unregister non existing factory " + name);
	      }

	      for (Iterator i = new ArrayList(infoContexts.values()).iterator(); i.hasNext();)
	      {
	         PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)i.next();
	         pdiCtx.remove(factoryCtx, true);
	      }

	      URL setupURL = factoryCtx.getSetupURL();
	      if (setupURL != null && URLTools.exists(setupURL))
	      {
	         log.debug("Found valid setup url to undeploy provided by factory " + name + " : " + setupURL);

	               infoContexts.remove(setupURL);
	         

	      }

	      // Log the removal
	      log.debug("Removed factory " + name);
	   }


	   public DeploymentFactory findFactory(URL url)
	   {
	      throw new UnsupportedOperationException();
	   }

	   public void deploy(PortalWebApp pwa) throws DeploymentException
	   {
	      // Instrument the web app
	      instrument(pwa);

	      // Create the deployment object

	      URL url = Deployment.findWEBINFURL(pwa.getURL());

	      // Create our deployment info object and pass it to main deployer
	      PortalDeploymentInfo pdi = new PortalDeploymentInfo(url, null, pwa, null);
	      //pdi.ucl = pwa.getClassLoader();
	      pdi.deployer = this;

	      // Put it in the map
	      infoContexts.put(pdi.url, new PortalDeploymentInfoContext(pdi));

//	      // And let JBoss deploy it
//	      mainDeployer.deploy(pdi);
	   }
	   public void undeploy(PortalWebApp pwa) throws DeploymentException
	   {
	      URL url = Deployment.findWEBINFURL(pwa.getURL());

	      //
	      try
	      {
	         // Undeploy
	        // mainDeployer.undeploy(url);
	      }
	      catch (Exception e)
	      {
	         log.error("Error when undeploying portal web app", e);
	      }
	      finally
	      {
	         // Remove it in the map
	         infoContexts.remove(url);
	      }
	   }

	   /** Instrument the portal web app. */
	   private void instrument(PortalWebApp pwa)
	   {
	      try
	      {
	         // Instrument war file first
	         pwa.instrument();
	      }
	      catch (Exception e)
	      {
	         log.error("Cannot instrument the web application", e);
	      }
	   }

	
	@PostConstruct
	public void register()	{
		servletContainerFactory.getServletContainer().addWebAppListener(this);
		appDeployer.addListener(this);
	}
	
	// All web application
	@PreDestroy
	public void unregister()	{
		servletContainerFactory.getServletContainer().removeWebAppListener(this);
		appDeployer.removeListener(this);
	}
	  
	
	 public void create(URL url) throws DeploymentException
	   {
	      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(url);
	      pdiCtx.create();

	      //
	      //super.create(di);
	   }

	   public void start(URL url) throws DeploymentException
	   {
	      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(url);
	      pdiCtx.start();

	      //
	      //super.start(di);
	   }

	   public void stop(DeploymentInfo di) throws DeploymentException
	   {
	      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
	      pdiCtx.stop();

	      //
	      super.stop(di);
	   }

	   public void destroy(DeploymentInfo di) throws DeploymentException
	   {
	      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(di.url);
	      pdiCtx.destroy();

	      //
	      super.destroy(di);
	   }

	   protected void processNestedDeployments(URL url) throws DeploymentException
	   {
	      // We only list if it is a directory
	      PortalDeploymentInfoContext pdiCtx = (PortalDeploymentInfoContext)infoContexts.get(url);
	      for (Iterator j = factoryContexts.values().iterator(); j.hasNext();)
	      {
	         DeploymentFactoryContext factoryCtx = (DeploymentFactoryContext)j.next();
	         pdiCtx.add(factoryCtx, false);
	      }
	   }

	

	// Generic Web application
	@Override
	public void onEvent(WebAppEvent event) {

		log.info(event);
		if (event instanceof WebAppLifeCycleEvent) {

			WebAppLifeCycleEvent cycleEvent = (WebAppLifeCycleEvent) event;
			if (cycleEvent.getType() == WebAppLifeCycleEvent.ADDED) {
				try {
					Tomcat8WebApp pwa = new Tomcat8WebApp(event);
					deploy(pwa);

					URL url = Deployment.findWEBINFURL(pwa.getURL());
					processNestedDeployments(url);
					create(url);
					start(url);
				} catch (Exception e) {
					log.error("Can't deploy application", e);
				}

				
			}

			if (cycleEvent.getType() == WebAppLifeCycleEvent.REMOVED) {
				try {

					Tomcat8WebApp pwa = new Tomcat8WebApp(event);
					undeploy(pwa);
				} catch (Exception e) {
					log.error("Can't undeploy application", e);
				}
			}

		}

	}

	// Portets
	@Override
	public void onEvent(ManagedObjectRegistryEvent event) {
		log.info(event);
		
	}


}
