package org.osivia.portal.core.portalobjects;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalBridge;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.container.dynamic.DynamicTemplatePage;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortalBridgeService implements PortalBridge {


	private static final String OSIVIA_CMS_URL_MAPPING = "osivia.cms.url.mapping.";


	/** The logger. */
	protected static Log logger = LogFactory.getLog(PortalBridgeService.class);
	
	@Autowired
    private CMSService cmsService;


	public CMSService getCmsService() {
		return cmsService;
	}

	@Override
	public PortalObjectId getPageId(PortalControllerContext portalContext) {
		return PortalObjectUtilsInternal.getPageId(ControllerContextAdapter.getControllerContext(portalContext));
	}

	@Override
	public Page getPage(PortalControllerContext portalContext) {
		return PortalObjectUtilsInternal.getPage(ControllerContextAdapter.getControllerContext(portalContext));
	}

	@Override
	public Portal getPortal(PortalControllerContext portalContext) {
		return PortalObjectUtilsInternal.getPortal(ControllerContextAdapter.getControllerContext(portalContext));

	}

	@Override
	public boolean isAdmin(PortalControllerContext portalCtx) {
		return PageCustomizerInterceptor.isAdministrator(ControllerContextAdapter.getControllerContext(portalCtx));

	}

	@Override
	public Window getMaximizedWindow(PortalControllerContext portalCtx, Page page) {
		return PortalObjectUtilsInternal.getMaximizedWindow(ControllerContextAdapter.getControllerContext(portalCtx),
				page);
	}

	@Override
	public PortalObject getObject(PortalControllerContext portalCtx, PortalObjectId id)
			throws IllegalArgumentException {
		return PortalObjectUtilsInternal.getObject(ControllerContextAdapter.getControllerContext(portalCtx), id);
	}

	@Override
	public List<?> getDomains(PortalControllerContext portalCtx) throws IllegalArgumentException {

		return (List<?>) ControllerContextAdapter.getControllerContext(portalCtx).getAttribute(Scope.SESSION_SCOPE,
				InternalConstants.USER_DOMAINS_ATTRIBUTE);
	}

	@Override
	public String getPortalContextPath(PortalControllerContext portalCtx) {
		ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
		ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
		return serverContext.getPortalContextPath();
	}

	@Override
	public void setPortalSessionAttribute(PortalControllerContext portalCtx, String name, Object object) {

		ControllerContextAdapter.getControllerContext(portalCtx).setAttribute(Scope.PRINCIPAL_SCOPE, name, object);
	}

	@Override
	public Object getPortalSessionAttribute(PortalControllerContext portalCtx, String name) {
		return ControllerContextAdapter.getControllerContext(portalCtx).getAttribute(Scope.PRINCIPAL_SCOPE, name);
	}

	@Override
	public boolean isDefaultMemberPage(PortalControllerContext portalCtx) {

		Page page = getPage(portalCtx);
		if (page != null) {
			Portal portal = page.getPortal();
			HttpServletRequest request = portalCtx.getHttpServletRequest();
			String defaultPageId;

			if (request.getUserPrincipal() == null) {
				defaultPageId = portal.getProperties().get("portal.defaultPageId");
			} else {
				defaultPageId = portal.getProperties().get("portal.unprofiledPageId");
			}

			if (page instanceof DynamicTemplatePage) {
				PortalObjectId templateId = ((DynamicTemplatePage) page).getTemplateId();
				if (templateId != null) {
					String templateName = templateId.getPath().getLastComponentName();
					if (templateName.equals(defaultPageId))
						return true;
				}
			}

		}
		return false;
	}

	@Override
	public boolean isRefreshingPage() {
		return PageProperties.getProperties().isRefreshingPage();
	}

	@Override
	public String getHostErrorPageURI(HttpServletRequest request) {
		String hostName = request.getHeader("osivia-virtual-host");
		String errorPageUri = null;

		if (StringUtils.isNotEmpty(hostName)) {
			try {
				URI uri = new URI(hostName);
				String domain = uri.getHost();

				String sDefaultPortalId = System.getProperty(OSIVIA_CMS_URL_MAPPING + domain);
				if (StringUtils.isNotEmpty(sDefaultPortalId)) {
					UniversalID defaultPortalId = new UniversalID(sDefaultPortalId);
					String charteCtx = System.getProperty(
							"osivia.cms.repository." + defaultPortalId.getRepositoryName() + ".charte.context");
					if (StringUtils.isNotEmpty(charteCtx))
						errorPageUri = charteCtx + "/error/errorPage.jsp";
				}
			} catch (URISyntaxException e) {
				// do nothing
			}
		}

		return errorPageUri;
	}

	@Override
	public String getHostCharteContext(HttpServletRequest request) {
		String hostName = request.getHeader("osivia-virtual-host");
		String charteCtx = null;

		if (StringUtils.isNotEmpty(hostName)) {
			try {
				URI uri = new URI(hostName);
				String domain = uri.getHost();

				String sDefaultPortalId = System.getProperty(OSIVIA_CMS_URL_MAPPING + domain);
				if (StringUtils.isNotEmpty(sDefaultPortalId)) {
					UniversalID defaultPortalId = new UniversalID(sDefaultPortalId);
					charteCtx = System.getProperty(
							"osivia.cms.repository." + defaultPortalId.getRepositoryName() + ".charte.context");
				}
			} catch (URISyntaxException e) {
				logger.error("can't parse host :" + e.getMessage());
			}
		}
		return charteCtx;
	}
	
	
	   @Override
	    public String getHostTerritoryCode(HttpServletRequest request) {
	        String hostName = request.getHeader("osivia-virtual-host");
	        String territoryCode = null;

	        if (StringUtils.isNotEmpty(hostName)) {
	            try {
	                URI uri = new URI(hostName);
	                String domain = uri.getHost();

	                String sDefaultPortalId = System.getProperty(OSIVIA_CMS_URL_MAPPING + domain);
	                if (StringUtils.isNotEmpty(sDefaultPortalId)) {
	                    UniversalID defaultPortalId = new UniversalID(sDefaultPortalId);
	                    territoryCode = System.getProperty(
	                            "osivia.cms.repository." + defaultPortalId.getRepositoryName() + ".territorycode");
	                }
	            } catch (URISyntaxException e) {
	                logger.error("can't parse host :" + e.getMessage());
	            }
	        }
	        return territoryCode;
	    }
	

	@Override
	public UniversalID getHostPortalID(HttpServletRequest request) {

		String sDefaultPortal = null;

		String hostName = request.getHeader("osivia-virtual-host");
		if (StringUtils.isNotEmpty(hostName)) {
			try {
				URI uri = new URI(hostName);
				String domain = uri.getHost();

				sDefaultPortal = System.getProperty(OSIVIA_CMS_URL_MAPPING + domain);
			} catch (Exception e) {
				logger.error("can't parse host :" + e.getMessage());
			}
		}

		if (StringUtils.isNotEmpty(sDefaultPortal))
			return new UniversalID(sDefaultPortal);
		else
			return null;
	}

	@Override
	public boolean checkIfRepositoryIsCompatibleWithHost(HttpServletRequest servletRequest, String repository) {

		// Check whether current repository is associated with another host

		String hostName = servletRequest.getHeader("osivia-virtual-host");
		if (StringUtils.isNotEmpty(hostName)) {
			try {
				URI uri = new URI(hostName);
				String domain = uri.getHost();

				Properties properties = System.getProperties();
				for (Object propertyName : properties.keySet()) {
					if (propertyName instanceof String) {
						String sPropertyName = (String) propertyName;
						if (sPropertyName.startsWith(OSIVIA_CMS_URL_MAPPING)) {
							String domainProperty = sPropertyName.substring(OSIVIA_CMS_URL_MAPPING.length());
							UniversalID defaultPortalId = new UniversalID(properties.getProperty(sPropertyName));
							if (defaultPortalId.getRepositoryName().equals(repository)
									&& (!domain.equals(domainProperty)))
								return false;

						}
					}
				}
			} catch (URISyntaxException e) {
				logger.error("can't parse host :" + e.getMessage());
			}
		}

		return true;

	}

	@Override
	public boolean isPageRepositoryManager(PortalControllerContext portalCtx) {
		
		Boolean repositoryManager;
		UniversalID hostID = getHostPortalID(portalCtx.getHttpServletRequest());
		if( hostID != null)	{
			try {
				HttpServletRequest request = portalCtx.getHttpServletRequest();
				repositoryManager = (Boolean) request.getAttribute(Constants.ATTR_REPOSITORY_MANAGER+hostID.getRepositoryName());
				if( repositoryManager == null)	{
					repositoryManager = cmsService.getCMSSession(new CMSContext(portalCtx)).isManager( hostID.getRepositoryName());
					request.setAttribute(Constants.ATTR_REPOSITORY_MANAGER+hostID.getRepositoryName(), repositoryManager);
				}
			
			} catch (CMSException e) {
				repositoryManager = false;
			}
		}	else
			repositoryManager = false;
		return repositoryManager;
	}



}
