package org.osivia.portal.core.customization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;
import org.osivia.portal.api.ha.IHAService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSBinaryContent;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;

/**
 * Project customizer interceptor.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class ProjectCustomizerInterceptor extends ControllerInterceptor {

	/** Customization service. */
	private ICustomizationService customizationService;
	/** Portal URL factory. */
	private IPortalUrlFactory portalUrlFactory;
	/** CMS service locator. */
	private ICMSServiceLocator cmsServiceLocator;
	/** HA service */
	private IHAService HAService;
	/** HA service */
	private ITaskbarService taskbarService;



	/** logger */
	private static final Logger log = Logger.getLogger(ProjectCustomizerInterceptor.class);


	/** last reload */
	private long portalPropertiesReloaded = 0L;

	/**
	 * Constructor.
	 */
	public ProjectCustomizerInterceptor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ControllerResponse invoke(ControllerCommand controllerCommand) throws Exception, InvocationException {
		// Response
		ControllerResponse response = null;

		// Controller context
		ControllerContext controllerContext = controllerCommand.getControllerContext();
		// Portal controller context
		PortalControllerContext portalControllerContext = new PortalControllerContext(
				controllerContext.getServerInvocation().getServerContext().getClientRequest());
		// Locale
		Locale locale = portalControllerContext.getHttpServletRequest().getLocale();

		response = (ControllerResponse) controllerCommand.invokeNext();

		if (response instanceof UpdatePageResponse) {

			// Has been moved from AjaxResponseHandler because page must be initialized
			PortalObjectUtilsInternal.setPageId(controllerContext, ((UpdatePageResponse) response).getPageId());

			// update properties from ECM
			updatePortalProperties(portalControllerContext);

			Page page = (Page) controllerContext.getController().getPortalObjectContainer()
					.getObject(((UpdatePageResponse) response).getPageId());

			// Project customization configuration
			ProjectCustomizationConfiguration configuration = new ProjectCustomizationConfiguration(
					portalControllerContext, page);

			// Customizer attributes
			Map<String, Object> customizerAttributes = new HashMap<String, Object>();
			customizerAttributes.put(IProjectCustomizationConfiguration.CUSTOMIZER_ATTRIBUTE_CONFIGURATION,
					configuration);
			// Customizer context
			CustomizationContext context = new CustomizationContext(customizerAttributes, portalControllerContext,
					locale);


			// Customization call #1
			this.customizationService.customize(IProjectCustomizationConfiguration.CUSTOMIZER_ID, context);
			
			String redirectionURL = configuration.getRedirectionURL();
			
			// Redirection ProjectCustomizer INDEX (moved from specific customizer)
			
			// Search in TRASH MODE
			// if render parameters are modified whithout current navigation nor tasks
			// -> force the cms content on /documents and keep the render parameters
			
			if (controllerCommand instanceof InvokePortletWindowRenderCommand) {
				
				if (StringUtils.isEmpty(redirectionURL)) {
					String sNavId = page.getProperties().get("osivia.navigationId");
					String sSpaceId = page.getProperties().get("osivia.spaceId");
					if (StringUtils.equals(sNavId, sSpaceId)) {
						String activeId = taskbarService.getActiveId(portalControllerContext);
						if (StringUtils.equals(ITaskbarService.HOME_TASK_ID, activeId)) {
							CMSServiceCtx cmsContext = new CMSServiceCtx();
							cmsContext.setPortalControllerContext(portalControllerContext);
							ICMSService cmsService = this.cmsServiceLocator.getCMSService();
							String basePath = cmsService.getPathFromUniversalID(cmsContext, new UniversalID(sSpaceId));
							if (basePath.startsWith("/default-domain/UserWorkspace")) {
								String redirectionPath = basePath + "/documents";

								redirectionURL = portalUrlFactory.getViewContentUrl(portalControllerContext,
										cmsService.getUniversalIDFromPath(cmsContext, redirectionPath));

								// we need to propagate actual public parameters (e: subject when trash is
								// activated)
								// new view state ignored in ajax redirection mode

								String initialViewSTate = (String) controllerContext.getServerInvocation()
										.getServerContext().getClientRequest().getAttribute("initial.view.state");
								if (initialViewSTate != null)
									PageMarkerUtils.savePageState(controllerContext, initialViewSTate);
							}
						}
					}
				}
			}

            
            
            


			// Redirection

			if (StringUtils.isNotEmpty(redirectionURL)) {
				response = new RedirectionResponse(redirectionURL);
			}
		}

		return response;
	}

	/**
	 * Update properties from nuxeo
	 * 
	 * @param portalControllerContext
	 */
	private void updatePortalProperties(PortalControllerContext portalControllerContext) {

		String propertiesPath = System.getProperty("config.properties.path");

		if (propertiesPath != null) {

			if (!HAService.checkIfPortalParametersReloaded(portalPropertiesReloaded)) {
				

				CMSServiceCtx cmsContext = new CMSServiceCtx();

				cmsContext.setPortalControllerContext(portalControllerContext);
				cmsContext.setForcePublicationInfosScope("superuser_no_cache");
				cmsContext.setStreamingSupport(false);

				// CMS service
				ICMSService cmsService = this.cmsServiceLocator.getCMSService();

				InputStream streamC = null;

				try {
					CMSBinaryContent binaryContent = cmsService.getBinaryContent(cmsContext, "file", propertiesPath,
							"file:content");

					streamC = new FileInputStream(binaryContent.getFile());
					System.getProperties().load(streamC);
					
					portalPropertiesReloaded = System.currentTimeMillis();

				} catch (Exception e) {
					log.error("Canot load portal properties from Nuxeo :" + e.getMessage());
				} finally {
					try {
						if (streamC != null)
							streamC.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}


	/**
	 * Setter for customizationService.
	 *
	 * @param customizationService the customizationService to set
	 */
	public void setCustomizationService(ICustomizationService customizationService) {
		this.customizationService = customizationService;
	}

	/**
	 * Setter for portalUrlFactory.
	 * 
	 * @param portalUrlFactory the portalUrlFactory to set
	 */
	public void setPortalUrlFactory(IPortalUrlFactory portalUrlFactory) {
		this.portalUrlFactory = portalUrlFactory;
	}

	/**
	 * Setter for cmsServiceLocator.
	 *
	 * @param cmsServiceLocator the cmsServiceLocator to set
	 */
	public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
		this.cmsServiceLocator = cmsServiceLocator;
	}

	/**
	 * 
	 * Setter for HAService.
	 * 
	 * @param hAService
	 */
	public void setHAService(IHAService hAService) {
		HAService = hAService;
	}
	
	

	/**
	 * @param taskbarService
	 */
	public void setTaskbarService(ITaskbarService taskbarService) {
		this.taskbarService = taskbarService;
	}
}
