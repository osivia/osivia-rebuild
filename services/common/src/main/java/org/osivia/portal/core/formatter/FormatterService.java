/**
 *
 */
package org.osivia.portal.core.formatter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.springframework.stereotype.Service;

/**
 * Formatter Service
 *
 * @see ControllerInterceptor
 * @see IFormatter
 */
@Service( "osivia:service=FormatterService")
public class FormatterService implements IFormatter {

    /** Default icon location. */
    private static final String DEFAULT_ICON_LOCATION = "/portal-core/images/portletIcon_Default1.gif";

    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;

    /** Instance container. */
    private InstanceContainer instanceContainer;
    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;

    /** Portal authorization manager factory. */
    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;


    /**
     * Default constructor.
     */
    public FormatterService() {
        super();
    }


    /**
     * Static access to CMS service.
     *
     * @return CMS service
     * @throws Exception
     */
    public static ICMSService getCMSService() throws Exception {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }


  

    /**
     * {@inheritDoc}
     */
    public String formatRequestFilteringPolicyList(PortalObject po, String policyName, String selectedPolicy) throws Exception {
        Map<String, String> policies = new LinkedHashMap<String, String>();

        policies.put(InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL, "Contenus du portail courant");
        policies.put(InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER, "Tous les contenus");

        String inheritedFilteringPolicy = po.getParent().getProperty(InternalConstants.PORTAL_PROP_NAME_CMS_REQUEST_FILTERING_POLICY);

        String inheritedLabel = null;

        if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL.equals(inheritedFilteringPolicy)) {
            inheritedLabel = "Contenus du portail courant";
        } else if (InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER.equals(inheritedFilteringPolicy)) {
            inheritedLabel = "Tous les contenus";
        } else {
            String portalType = po.getProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE);
            if (InternalConstants.PORTAL_TYPE_SPACE.equals(portalType)) {
                inheritedLabel = "Contenus du portail courant";
            } else {
                inheritedLabel = "Tous les contenus";
            }
        }


        inheritedLabel = "Hérité du portail [" + inheritedLabel + "]";

        // Select
        Element select = DOM4JUtils.generateElement(HTMLConstants.SELECT, "form-control", null);
        DOM4JUtils.addAttribute(select, HTMLConstants.ID, "cms-filter");
        DOM4JUtils.addAttribute(select, HTMLConstants.NAME, policyName);

        // Default option
        Element defaultOption = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, inheritedLabel);
        DOM4JUtils.addAttribute(defaultOption, HTMLConstants.VALUE, StringUtils.EMPTY);
        if (StringUtils.isEmpty(selectedPolicy)) {
            DOM4JUtils.addAttribute(defaultOption, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
        }
        select.add(defaultOption);

        for (Entry<String, String> entry : policies.entrySet()) {
            // Option
            Element option = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, entry.getValue());
            DOM4JUtils.addAttribute(option, HTMLConstants.VALUE, entry.getKey());
            if (entry.getKey().equals(selectedPolicy)) {
                DOM4JUtils.addAttribute(option, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
            }
            select.add(option);
        }

        return DOM4JUtils.write(select);
    }


    /**
     * {@inheritDoc}
     */
    public String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception {
        return "";
    }


    /**
     * {@inheritDoc}
     */
    public String formatDisplayLiveVersionList(CMSServiceCtx cmxCtx, PortalObject po, String versionName, String selectedVersion) throws Exception {
        Map<String, String> versions = new LinkedHashMap<String, String>();

        versions.put("1", "Live");
        versions.put("2", "Live et Publiée");

        String inheritedLabel = null;

        // Calcul du label hérité

        if (inheritedLabel == null) {
            Page page = null;

            if (po instanceof Page) {
                page = (Page) po;
            }
            if (po instanceof Window) {
                page = (Page) po.getParent();
            }

            String spacePath = page.getProperty("osivia.cms.basePath");

            if (spacePath != null) {
                // Publication par path

                CMSItem publishSpaceConfig = getCMSService().getSpaceConfig(cmxCtx, spacePath);
                if (publishSpaceConfig != null) {

                    String displayLiveVersion = publishSpaceConfig.getProperties().get("displayLiveVersion");

                    if (displayLiveVersion != null) {
                        inheritedLabel = versions.get(displayLiveVersion);
                    }
                }
            } else {
                // Heriatge page parent
                String parentVersion = po.getParent().getProperty(Constants.WINDOW_PROP_VERSION);
                if (parentVersion != null) {

                    inheritedLabel = versions.get(parentVersion);

                }
            }
        }

        if (inheritedLabel == null) {
            inheritedLabel = "Publié";
        }


        inheritedLabel = "Hérité [" + inheritedLabel + "]";

        versions.put("__inherited", inheritedLabel);


        // Select
        Element select = DOM4JUtils.generateElement(HTMLConstants.SELECT, "form-control", null);
        DOM4JUtils.addAttribute(select, HTMLConstants.ID, "cms-version");
        DOM4JUtils.addAttribute(select, HTMLConstants.NAME, versionName);
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            DOM4JUtils.addAttribute(select, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
        }

        if (!versions.isEmpty()) {
            // Default option
            Element defaultOption = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, "Publiée");
            DOM4JUtils.addAttribute(defaultOption, HTMLConstants.VALUE, StringUtils.EMPTY);
            if (StringUtils.isEmpty(selectedVersion)) {
                DOM4JUtils.addAttribute(defaultOption, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
            }
            select.add(defaultOption);

            for (Entry<String, String> entry : versions.entrySet()) {
                // Option
                Element option = DOM4JUtils.generateElement(HTMLConstants.OPTION, null, entry.getValue());
                DOM4JUtils.addAttribute(option, HTMLConstants.VALUE, entry.getKey());
                if (entry.getKey().equals(selectedVersion)) {
                    DOM4JUtils.addAttribute(option, HTMLConstants.SELECTED, HTMLConstants.INPUT_SELECTED);
                }
                select.add(option);
            }
        }

        return DOM4JUtils.write(select);
    }


    /**
     * {@inheritDoc}
     */
    public String formatHtmlSafeEncodingId(PortalObjectId id) throws IOException {
        if (id == null) {
            return null;
        } else {
            String safestFormat = id.toString(PortalObjectPath.SAFEST_FORMAT);
            return URLEncoder.encode(safestFormat, CharEncoding.UTF_8);
        }
    }


   




    /**
     * Getter for portalObjectContainer.
     *
     * @return the portalObjectContainer
     */
    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    /**
     * Setter for portalObjectContainer.
     *
     * @param portalObjectContainer the portalObjectContainer to set
     */
    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

   

    /**
     * Getter for portalAuthorizationManagerFactory.
     *
     * @return the portalAuthorizationManagerFactory
     */
    public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
        return this.portalAuthorizationManagerFactory;
    }

    /**
     * Setter for portalAuthorizationManagerFactory.
     *
     * @param portalAuthorizationManagerFactory the portalAuthorizationManagerFactory to set
     */
    public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
        this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
    }

    /**
     * Getter for internationalizationService.
     *
     * @return the internationalizationService
     */
    public IInternationalizationService getInternationalizationService() {
        return this.internationalizationService;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

}
