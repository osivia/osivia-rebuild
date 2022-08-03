package org.osivia.portal.cms.portlets.edition.page.acls.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.service.GroupService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Update page controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class AclsController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

    private static final String _ANONYMOUS = "_anonymous_";


    private static final String GROUP_PREFIX = "group:";


    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;

    /** Application context. */
    private ApplicationContext applicationContext;

    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;

    @Autowired
    private IBundleFactory bundleFactory;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(AclsController.class);

    /**
     * Constructor.
     */
    public AclsController() {
        super();
    }

    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
    }


    /**
     * Default render mapping.
     *
     * @param request render request
     * @param response render response
     * @param count count request parameter.
     * @return render view path
     * @throws PortalException
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortalException {
        return "view";
    }


    /**
     * Submit action
     */
    @ActionMapping(name = "submit")
    public void updateProperties(ActionRequest request, ActionResponse response, AclsForm form) throws PortletException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            // Portal controller context
            CMSController ctrl = new CMSController(portalControllerContext);
            CMSContext cmsContext = ctrl.getCMSContext();

            RepositoryDocument document;

            document = getDocument(portalControllerContext);


            NativeRepository userRepository = cmsService.getUserRepository(cmsContext, document.getId().getRepositoryName());

            if (userRepository instanceof AdvancedRepository) {

                List<Profile> profiles = getSpaceProfiles(portalControllerContext);
                List<String> acls = new ArrayList<>();

                for (String formProfile : form.getProfiles()) {
                    for (Profile profile : profiles) {
                        if (formProfile.equals(profile.getName())) {
                            if( ! profile.getRole().equals(_ANONYMOUS))
                                acls.add(GROUP_PREFIX + profile.getRole());
                            else
                                acls.add( profile.getRole());
                        }

                    }

                }

                ((AdvancedRepository) userRepository).setACL(document.getId().getInternalID(), acls);
            }


            String url = this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
            response.sendRedirect(url);

        } catch (CMSException | IOException e) {
            throw new PortletException(e);
        }
    }


    private List<Profile> getSpaceProfiles(PortalControllerContext portalCtx) throws CMSException {

        Document document = getDocument(portalCtx);
        document.getSpaceId();

        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        Space space = (Space) cmsService.getCMSSession(cmsContext).getDocument(document.getSpaceId());
        UniversalID templateId = space.getTemplateId();
        if (templateId != null) {
            CMSContext cmsTemplateContext = ctrl.getCMSContext();
            cmsTemplateContext.setPreview(false);
            Page page = (Page) cmsService.getCMSSession(cmsTemplateContext).getDocument(templateId);
            space = (Space) cmsService.getCMSSession(cmsTemplateContext).getDocument(page.getSpaceId());
        }

        List<Profile> profiles = space.getProfiles();
        profiles.add(new Profile(bundleFactory.getBundle(portalCtx.getHttpServletRequest().getLocale()).getString("MODIFY_PAGE_ACLS_ANONYMOUS_LABEL"), _ANONYMOUS, "", ""));
        return profiles;
    }


    @ModelAttribute("profilesList")
    protected List<Profile> getProfilesList(PortletRequest request, PortletResponse response) throws Exception {
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        List<Profile> profiles = getSpaceProfiles(portalCtx);
        profiles.removeIf(profile -> StringUtils.isEmpty(profile.getRole()));


        return profiles;
    }


    private RepositoryDocument getDocument(PortalControllerContext portalControllerContext) throws CMSException {

        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);


        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        String navigationId = window.getProperty("osivia.acls.id");


        CMSContext cmsContext = ctrl.getCMSContext();


        RepositoryDocument document = (RepositoryDocument) cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(navigationId));
        return document;
    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Properties form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public AclsForm getForm(PortletRequest request, PortletResponse response) throws PortletException {

        try {

            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            RepositoryDocument document = getDocument(portalControllerContext);


            CMSController ctrl = new CMSController(portalControllerContext);
            CMSContext cmsContext = ctrl.getCMSContext();

            AclsForm form = this.applicationContext.getBean(AclsForm.class);

            NativeRepository userRepository = cmsService.getUserRepository(cmsContext, document.getId().getRepositoryName());

            if (userRepository instanceof AdvancedRepository) {
                List<String> formProfiles = new ArrayList<>();

                List<String> acls = ((AdvancedRepository) userRepository).getACL(document.getId().getInternalID());

                List<Profile> profiles = getSpaceProfiles(portalControllerContext);
                for (String acl : acls) {

                    String role;
                    if (acl.startsWith(GROUP_PREFIX)) {
                        role = acl.substring(GROUP_PREFIX.length());
                    } else {
                        role = acl;
                    }


                    for (Profile profile : profiles) {
                        if (StringUtils.equals(role, profile.getRole())) {
                            formProfiles.add(profile.getName());
                        }
                    }
               }


                form.setProfiles(formProfiles);
            }

            return form;

        } catch (Exception e) {
            throw new PortletException(e);
        }
    }


}
