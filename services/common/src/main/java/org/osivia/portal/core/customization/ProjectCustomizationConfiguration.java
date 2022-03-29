package org.osivia.portal.core.customization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;

/**
 * Project customization configuration implementation.
 *
 * @author Cédric Krommenhoek
 * @see IProjectCustomizationConfiguration
 */
public class ProjectCustomizationConfiguration implements IProjectCustomizationConfiguration {

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Portal controller context. */
    private final PortalControllerContext portalControllerContext;
    /** Controller context. */
    private final ControllerContext controllerContext;
    /** Page. */
    private final Page page;
    /** HTTP servlet response. */
    private final HttpServletResponse httpServletResponse;
    /** Administrator indicator. */
    private final boolean administrator;


    /** Redirection URL. */
    private String redirectionURL;

    /** CMS service locator. */
    private final IPortalUrlFactory portalURLFactory;
    /**
     * Constructor.
     *
     * @param portalControllerContext portal controller context
     * @param page page
     */
    public ProjectCustomizationConfiguration(PortalControllerContext portalControllerContext, Page page) {
        super();
        this.portalControllerContext = portalControllerContext;
        this.page = page;

        // Controller context
        this.controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // HTTP client response
        this.httpServletResponse = controllerContext.getServerInvocation().getServerContext().getClientResponse();

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        
        this.portalURLFactory = Locator.getService(IPortalUrlFactory.MBEAN_NAME, IPortalUrlFactory.class);

        // Administrator indicator
        this.administrator = PageCustomizerInterceptor.isAdministrator(controllerContext);
    }


    /**
     * {@inheritDoc}
     */
    public String getCMSPath() {
        return this.getPublicationPath();
    }


    /**
     * {@inheritDoc}
     */
    public String getWebId() {
        // WebId
        String webId;

        if (this.page == null) {
            webId = null;
        } else {
        	String sNav =  this.page.getProperty("osivia.navigationId");
        	if( sNav != null)	{
        		UniversalID navId = new UniversalID(sNav);
        		webId = navId.getInternalID();
        	}	else
        		webId = null;
        }

        return webId;
    }


    /**
     * Get publication path.
     *
     * @return publication path
     */
    private String getPublicationPath() {
        // Publication path
        String publicationPath;

        if (this.page == null) {
            publicationPath = null;
        } else {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);
        	
            String sNav =  this.page.getProperty("osivia.navigationId");
            
            if( sNav != null) {
            	// Current CMS base path
            	UniversalID navId = new UniversalID(sNav);
            	if( navId.getRepositoryName().equals("nx"))	{
            	try {
					publicationPath = cmsService.getPathFromUniversalID(cmsContext, navId);
				} catch (CMSException e) {
					throw new RuntimeException(e);
				}
            	} else
            		publicationPath = null;
            }	else
            	publicationPath = null;
        }


        return publicationPath;
    }





    /**
     * {@inheritDoc}
     */
    public HttpServletRequest getHttpServletRequest() {
        return this.controllerContext.getServerInvocation().getServerContext().getClientRequest();
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletResponse getHttpServletResponse() {
        return this.httpServletResponse;
    }


    /**
     * {@inheritDoc}
     */
    public String getThemeName() {
        // Theme name
        String themeName;

        if (this.page == null) {
            themeName = null;
        } else {
            ThemeService themeService = this.controllerContext.getController().getPageService().getThemeService();
            String themeId = this.page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
            PortalTheme theme = themeService.getThemeById(themeId);
            themeName = theme.getThemeInfo().getName();
        }

        return themeName;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAdministrator() {
        return administrator;
    }


    /**
     * {@inheritDoc}
     */
    public void setRedirectionURL(String redirectionURL) {
        this.redirectionURL = redirectionURL;
    }



    /**
     * Getter for redirectionURL.
     *
     * @return the redirectionURL
     */
    public String getRedirectionURL() {
        return this.redirectionURL;
    }
    
    
    /**
     * Get URL to replay once redirection is done.
     *
     * @param redirectionURL redirection URL
     */
    public String buildRestorableURL() {
        String redirectionURL = null;

        redirectionURL = portalURLFactory.getBasePortalUrl(portalControllerContext);
        
        redirectionURL = getHttpServletRequest().getRequestURI().toString();
        redirectionURL += "?";
        if (StringUtils.isNotBlank(getHttpServletRequest().getQueryString()))
            redirectionURL +=  getHttpServletRequest().getQueryString();
        redirectionURL +=  "&InterceptedURL=true";


        return redirectionURL;
    }

}
