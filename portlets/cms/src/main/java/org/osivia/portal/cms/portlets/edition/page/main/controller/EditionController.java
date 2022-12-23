package org.osivia.portal.cms.portlets.edition.page.main.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.HTMLWriter;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositorySpace;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ha.IHAService;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.nuxeo.api.NxControllerMock;
import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class EditionController implements PortletContextAware, ApplicationContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;

    /** Application context. */
    private ApplicationContext applicationContext;

    @Autowired
    private IPortalUrlFactory portalUrlFactory;


    @Autowired
    private IPreviewModeService previewModeService;

    @Autowired
    private ILocaleService localService;

    /** Locale property editor. */
    @Autowired
    private LocalePropertyEditor localPropertyEditor;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;
    
    @Autowired
    private IHAService haService;

    /** The logger. */
    protected static Log logger = LogFactory.getLog(EditionController.class);

    /**
     * Constructor.
     */
    public EditionController() {
        super();
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


        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
        UniversalID id = new UniversalID(contentId);
        if (previewModeService.isEditionMode(portalControllerContext)) {

            CMSController ctrl = new CMSController(portalControllerContext);

            CMSContext cmsContext = ctrl.getCMSContext();
            Document document = cmsService.getCMSSession(cmsContext).getDocument(id);

            NativeRepository userRepository = cmsService.getUserRepository(cmsContext, id.getRepositoryName());

            if (!userRepository.supportPreview() || cmsContext.isPreview()) {

                if (document instanceof MemoryRepositoryPage || document instanceof MemoryRepositorySpace) {

                    // Display tools

                    HttpServletRequest httpRequest = (HttpServletRequest) request.getAttribute(Constants.PORTLET_ATTR_HTTP_REQUEST);
                    PortletURL actionUrl = response.createActionURL();
                    httpRequest.setAttribute("osivia.cms.edition.url", actionUrl.toString());


                }
            }
        }

        try {
            request.setAttribute("status", getStatus(request, response));
        } catch (PortletException e) {
            throw new PortalException(e);
        }

        return "view";
    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "drop")
    public void drop(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String region = request.getParameter("region");
        String source = request.getParameter("source");
        String targetWindow = request.getParameter("targetWindow");

        String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
        if (contentId != null) {

            UniversalID id = new UniversalID(contentId);
            CMSController ctrl = new CMSController(portalControllerContext);

            CMSContext cmsContext = ctrl.getCMSContext();
            Document document = cmsService.getCMSSession(cmsContext).getDocument(id);

            if (document instanceof ModulesContainer) {

                ModuleRef srcModule = null;

                // Search src module
                List<ModuleRef> modules = ((ModulesContainer) document).getModuleRefs();
                for (ModuleRef module : modules) {
                    if (module.getWindowName().equals(source)) {
                        srcModule = module;
                        break;
                    }
                }

                // compute region
                if (region == null) {
                    for (ModuleRef module : modules) {
                        if (module.getWindowName().equals(targetWindow)) {
                            region = module.getRegion();
                            break;
                        }
                    }
                }


                // Remove src module
                modules.remove(srcModule);


                // re-insert src module
                ModuleRef newModule = new ModuleRef(srcModule.getWindowName(), region, srcModule.getModuleId(), srcModule.getProperties());

                int iInsertion = -1;

                if (targetWindow != null) {
                    iInsertion = -1;
                    for (ModuleRef module : modules) {
                        if (targetWindow != null && StringUtils.equals(module.getWindowName(), targetWindow)) {
                            iInsertion++;
                            break;
                        }
                        iInsertion++;
                    }
                } else {
                    if (region != null) {
                        int iInsertionRegion = 0;
                        for (ModuleRef module : modules) {
                            if (StringUtils.equals(module.getRegion(), region)) {
                                iInsertion = iInsertionRegion + 1;
                            }
                            iInsertionRegion++;
                        }
                    }
                }

                if (iInsertion != -1)
                    modules.add(iInsertion, newModule);
                else
                    modules.add(newModule);


                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository instanceof AdvancedRepository) {
                    ((AdvancedRepository) repository).updateDocument(id.getInternalID(), (RepositoryDocument) document);
                }


            }

            logger.info(document.getTitle());
        }


    }

    /**
     * Add page sample
     */
    @ActionMapping(name = "addPage")
    public void addPage(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            NxControllerMock nuxeoController = new NxControllerMock(portalControllerContext);

            String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (navigationId != null) {

                UniversalID id = new UniversalID(navigationId);

                CMSController ctrl = new CMSController(portalControllerContext);

                CMSContext cmsContext = ctrl.getCMSContext();


                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

                String newID = "" + System.currentTimeMillis();
                

                ((AdvancedRepository) repository).addDocument(newID, "page",  "" + System.currentTimeMillis(), id.getInternalID());


                String url = portalUrlFactory.getViewContentUrl(portalControllerContext, ctrl.getCMSContext(), new UniversalID(id.getRepositoryName(), newID));
                response.sendRedirect(url);


            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }

    }
    
    
    /**
     * Add space
     */
    @ActionMapping(name = "addSpace")    
    public void addSpace(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            NxControllerMock nuxeoController = new NxControllerMock(portalControllerContext);

            String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (navigationId != null) {

                UniversalID id = new UniversalID(navigationId);

                CMSController ctrl = new CMSController(portalControllerContext);

                CMSContext cmsContext = ctrl.getCMSContext();


                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

                String newID = "" + System.currentTimeMillis();
                

                ((AdvancedRepository) repository).addDocument(newID, "space",  "" + System.currentTimeMillis(), id.getInternalID());


                String url = portalUrlFactory.getViewContentUrl(portalControllerContext, ctrl.getCMSContext(), new UniversalID(id.getRepositoryName(), newID));
                response.sendRedirect(url);


            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }

    }

    /**
     * Add page sample
     */
    @ActionMapping(name = "setACL")
    public void setACL(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);


            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSController ctrl = new CMSController(portalControllerContext);

                CMSContext cmsContext = ctrl.getCMSContext();

                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository.getACL(id.getInternalID()).isEmpty())
                    ((AdvancedRepository) repository).setACL(id.getInternalID(), Arrays.asList("group:members"));
                else
                    ((AdvancedRepository) repository).setACL(id.getInternalID(), new ArrayList<String>());


            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "addPortlet")
    public void addPortlet(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
            CMSController ctrl = new CMSController(portalControllerContext);


            addPortletToRegion(request, portalControllerContext, ctrl, "SampleInstance", "col-2", AdvancedRepository.POSITION_END);
        } catch (PortalException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Reload sample file repository
     */
    @ActionMapping(name = "reload")
    public void reloadIdx(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            CMSController ctrl = new CMSController(portalControllerContext);
            CMSContext cmsContext = ctrl.getCMSContext();


            AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, System.getProperty("osivia.repository.default"));

            ((AdvancedRepository) repository).reloadDatas();


        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "switchMode")
    public void switchMode(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {

            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            UniversalID id = new UniversalID(contentId);

            previewModeService.changePreviewMode(portalControllerContext, id);

            // update cms context
            CMSController ctrl = new CMSController(portalControllerContext);
            CMSContext cmsCtx = ctrl.getCMSContext();
            cmsCtx.setPreview(previewModeService.isPreviewing(portalControllerContext, id));

            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, cmsCtx, id);
            response.sendRedirect(url);

        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }
    }

    /**
     * Add page sample
     */
    @ActionMapping(name = "setLocale")
    public void setLocale(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {

            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            UniversalID id = new UniversalID(contentId);

            previewModeService.changePreviewMode(portalControllerContext, id);

            // Reload cms context
            CMSController ctrl = new CMSController(portalControllerContext);
            CMSContext cmsCtx = ctrl.getCMSContext();

            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, cmsCtx, id);
            response.sendRedirect(url);

        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Add portlet sample
     */
    @ActionMapping(name = "addPortletNav")
    public void addPortletNav(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
            CMSController ctrl = new CMSController(portalControllerContext);

            addPortletToRegion(request, portalControllerContext, ctrl, "FragmentInstance", "nav", AdvancedRepository.POSITION_BEGIN);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    protected void addPortletToRegion(ActionRequest request, PortalControllerContext portalControllerContext, CMSController ctrl, String portletName, String region, int position) throws CMSException {
        String navigationId = getNavigationId(request);
        if (navigationId != null) {

            UniversalID id = new UniversalID(navigationId);

            CMSContext cmsContext = ctrl.getCMSContext();


            AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

            String windowID = "" + System.currentTimeMillis();

            Map<String, String> editionProperties = new ConcurrentHashMap<>();
            editionProperties.put("osivia.hideTitle", "1");

            ((AdvancedRepository) repository).addWindow(windowID, windowID, portletName, region, position, id.getInternalID(), editionProperties);

        }
    }


    private String getNavigationId(ActionRequest request) {
        PortalWindow window = WindowFactory.getWindow(request);
        String navigationId = window.getProperty("osivia.navigationId");
        if (navigationId == null)
            navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
        return navigationId;
    }


    /**
     * publish sample
     */
    @ActionMapping(name = "publish")
    public void publish(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);

        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();

                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository instanceof AdvancedRepository) {
                    ((AdvancedRepository) repository).publish(id.getInternalID());
                }


            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }


    }
    
    
    /**
     * unpublish sample
     */
    @ActionMapping(name = "unpublish")
    public void unpublish(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);

        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();

                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository instanceof AdvancedRepository) {
                    ((AdvancedRepository) repository).unpublish(id.getInternalID());
                }


            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }


    }


    /**
     * add folder
     */
    @ActionMapping(name = "addFolder")
    public void addFolder(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);

        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();

                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository instanceof AdvancedRepository) {
                    String newID = "" + System.currentTimeMillis();

                    ((AdvancedRepository) repository).addDocument(newID, "folder", newID, id.getInternalID());
                }


            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }

    /**
     * add document
     */
    @ActionMapping(name = "addDocument")
    public void addDocument(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);

        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();

                AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if (repository instanceof AdvancedRepository) {
                    String newID = "" + System.currentTimeMillis();

                    ((AdvancedRepository) repository).addDocument(newID, "file", newID, id.getInternalID());
                }


            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    public EditionStatus getStatus(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);

        // application form
        EditionStatus status = this.applicationContext.getBean(EditionStatus.class);

        refreshStatus(portalControllerContext, ctrl, status);

        return status;
    }


    protected void addToolbarItem(Element toolbar, String url, String target, String title, String icon) {
        addToolbarItem(toolbar, url, target, title, icon, false);
    }

    protected void addToolbarItem(Element toolbar, String url, String target, String title, String icon, boolean menu) {
        // Base HTML classes
        String baseHtmlClasses = "btn";

        if (menu)
            baseHtmlClasses = "dropdown-item";

        // Item
        Element item;
        if (StringUtils.isEmpty(url)) {
            item = DOM4JUtils.generateLinkElement("#", null, null, baseHtmlClasses + " disabled", null, icon);
        } else {
            // Data attributes
            Map<String, String> data = new HashMap<>();
            String onClick = null;

            if ("#osivia-modal".equals(target)) {
                data.put("target", "#osivia-modal");
                data.put("load-url", url);
                data.put("title", title);

                url = "javascript:";
                target = null;
            } else if ("modal".equals(target)) {
                data.put("bs-toggle", "modal");

                target = null;
            } else if ("dropdown-link".equals(target)) {
                // HACKISH : link are not supported be dropdowns !
                onClick = "bilto(event)";
                target = null;
            }


            item = DOM4JUtils.generateLinkElement(url, target, onClick, baseHtmlClasses, null, icon);

            // Title
            DOM4JUtils.addAttribute(item, "title", title);

            // Data attributes
            for (Map.Entry<String, String> entry : data.entrySet()) {
                DOM4JUtils.addDataAttribute(item, entry.getKey(), entry.getValue());
            }
        }

        // Text
        Element text = DOM4JUtils.generateElement("span", "d-none d-xl-inline", title);
        item.add(text);

        toolbar.add(item);
    }


    protected void refreshStatus(PortalControllerContext portalControllerContext, CMSController ctrl, EditionStatus status) throws PortletException {
        try {

            // Internationalization bundle
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

            String navigationId = WindowFactory.getWindow(portalControllerContext.getRequest()).getPageProperty("osivia.navigationId");

            // Toolbar
            Element container = DOM4JUtils.generateDivElement(null);
            Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);
            container.add(toolbar);

            if (navigationId != null) {
                UniversalID id = new UniversalID(navigationId);

                CMSContext cmsContext = ctrl.getCMSContext();


                Document document = cmsService.getCMSSession(cmsContext).getDocument(id);
                
                Document space = cmsService.getCMSSession(cmsContext).getDocument(document.getSpaceId());


                Boolean isAdministrator = (Boolean) portalControllerContext.getRequest().getAttribute(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME);




                String templatePath = (String) portalControllerContext.getRequest().getAttribute("osivia.edition.templatePath");
                String cmsTemplatePath = (String) portalControllerContext.getRequest().getAttribute("osivia.edition.cmsTemplatePath");


                Personnalization personnalization = cmsService.getCMSSession(cmsContext).getPersonnalization(id);
                if( personnalization.isManageable())
                	getConfigurationMenu(portalControllerContext, ctrl, bundle, toolbar, id, isAdministrator, templatePath, cmsTemplatePath);


                status.setPreview(cmsContext.isPreview());

                NativeRepository userRepository = cmsService.getUserRepository(cmsContext, id.getRepositoryName());

                if (userRepository instanceof AdvancedRepository) {

                    AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                    status.setSupportPreview(repository.supportPreview());

                    Map<String, String> locales = new HashMap<>();
                    for (Locale locale : repository.getLocales()) {
                        locales.put(locale.toString(), locale.getDisplayLanguage());
                    }
                    status.setLocales(locales);

                    status.setPageEdition(repository.supportPageEdition());



                    status.setSubtypes(personnalization.getSubTypes());
                    status.setManageable(personnalization.isManageable());
                    status.setModifiable(personnalization.isModifiable());
                    status.setLiveSpace(BooleanUtils.isTrue(((Boolean)space.getProperties().get("osivia.connect.liveSpace"))));


                    if (status.isManageable()) {
                        getSpaceConfiguration(portalControllerContext, ctrl, bundle, toolbar, document, isAdministrator, cmsContext, repository);
                    }

                    if( getTemplateID( portalControllerContext,  id,  templatePath,  cmsTemplatePath) == null )	{
	                    if (status.isModifiable()) {
	                        getEditionMenu(portalControllerContext, ctrl, status, bundle, toolbar, id, cmsContext, document, repository, personnalization);
	                    }
                    }


                }
            }

            status.setRemoteUser(portalControllerContext.getRequest().getRemoteUser());


            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            HTMLWriter htmlWriter;
            try {
                htmlWriter = new HTMLWriter(stream);

                htmlWriter.write(container);
                htmlWriter.close();
            } catch (Exception e) {
                throw new PortletException(e);
            }

            status.setToolbar(new String(stream.toByteArray()));


        } catch (

        PortalException e) {
            throw new PortletException(e);
        }
    }


    private void getConfigurationMenu(PortalControllerContext portalControllerContext, CMSController ctrl, Bundle bundle, Element toolbar, UniversalID id, Boolean isAdministrator, String templatePath, String cmsTemplatePath) throws PortalException {
        Element configurationList;

        Element menu = DOM4JUtils.generateDivElement("dropdown");
        Element menuTitle = DOM4JUtils.generateLinkElement("#", null, null, "btn dropdown-toggle no-ajax-link", null, "halflings halflings-wrench");
        menuTitle.addAttribute(new QName("id"), "dropdownConfigurationMenu");
        Element text = DOM4JUtils.generateElement("span", "d-none d-xl-inline", bundle.getString("MODIFY_MENU_CONFIGURATION"));
        menuTitle.add(text);

            DOM4JUtils.addDataAttribute(menu, "bs-toggle", "dropdown");

        menu.add(menuTitle);

        configurationList = DOM4JUtils.generateDivElement("dropdown-menu");

        configurationList.addAttribute(new QName("aria-labelledby"), "dropdownConfigurationMenu");
        menu.add(configurationList);


        Element menuRoot = DOM4JUtils.generateDivElement("");
        menuRoot.add(menu);

        toolbar.add(menuRoot);


        Map<String, String> properties = new HashMap<>();
        String title = bundle.getString("MODIFY_REPOSITORY_ACTION");
        properties.put("osivia.title", title);

        String manageRepositoriesUrl = portalUrlFactory.getStartPortletInNewPage(portalControllerContext, "EditionRepositoryPortletInstance", title, "EditionRepositoryPortletInstance", properties,
                new HashMap<>());
        this.addToolbarItem(configurationList, manageRepositoriesUrl, "dropdown-link", bundle.getString("MODIFY_REPOSITORY_ACTION"), "glyphicons glyphicons-basic-server", true);


        // init caches
        PortletURL initCacheUrl = ((RenderResponse) portalControllerContext.getResponse()).createActionURL();
        initCacheUrl.setParameter(ActionRequest.ACTION_NAME, "initCaches");

        this.addToolbarItem(configurationList, initCacheUrl.toString(), "dropdown-link", bundle.getString("INIT_CACHES_ACTION"), "glyphicons glyphicons-basic-sync", true);

        
        
        addTemplateLink(portalControllerContext, ctrl, bundle, configurationList, id, templatePath, cmsTemplatePath);



    }


    private void getEditionMenu(PortalControllerContext portalControllerContext, CMSController ctrl, EditionStatus status, Bundle bundle, Element toolbar, UniversalID id, CMSContext cmsContext, Document document, AdvancedRepository repository,
            Personnalization personnalization) throws PortalException {


        if (!repository.supportPreview() || cmsContext.isPreview()) {

            // Text
            Element menu = DOM4JUtils.generateDivElement("dropdown");

            Element menuTitle = DOM4JUtils.generateLinkElement("#", null, null, "btn dropdown-toggle no-ajax-link", null, "glyphicons glyphicons-basic-monitor");
            menuTitle.addAttribute(new QName("id"), "dropdownEditionMenu");
            Element text = DOM4JUtils.generateElement("span", "d-none d-xl-inline", bundle.getString("MODIFY_MENU_EDITION"));
            menuTitle.add(text);
            DOM4JUtils.addDataAttribute(menu, "bs-toggle", "dropdown");
            menu.add(menuTitle);

            Element editionList = DOM4JUtils.generateDivElement("dropdown-menu");
            editionList.addAttribute(new QName("aria-labelledby"), "dropdownEditionMenu");

            menu.add(editionList);

            Element menuRoot = DOM4JUtils.generateDivElement("");
            menuRoot.add(menu);

            toolbar.add(menuRoot);


            // "${status.pageEdition && ( not status.supportPreview || status.preview ) && fn:containsIgnoreCase(status.subtypes, 'page') }">
            boolean hasPageSubtype = false;
            for (String subType : personnalization.getSubTypes()) {
                if (StringUtils.equalsIgnoreCase(subType, "Page"))
                    hasPageSubtype = true;
            }


            if ((!repository.supportPreview() || cmsContext.isPreview()) && hasPageSubtype) {
                if (portalControllerContext.getResponse() instanceof RenderResponse) {
                    PortletURL createPageUrl = ((RenderResponse) portalControllerContext.getResponse()).createActionURL();
                    createPageUrl.setParameter(ActionRequest.ACTION_NAME, "addPage");
                    this.addToolbarItem(editionList, createPageUrl.toString(), "dropdown-link", bundle.getString("MODIFY_PAGE_CREATE_ACTION"), "glyphicons glyphicons-basic-square-empty-plus", true);
                }
            }


            if (document instanceof Space || document instanceof Page) {
                if( !repository.supportPreview())   {
                    Map<String, String> properties = new HashMap<>();
                    ctrl.addContentRefToProperties(properties, "osivia.properties.id", document.getId());
    
                    String renameUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "EditionPagePropertiesPortletInstance", properties, PortalUrlType.MODAL);
                    this.addToolbarItem(editionList, renameUrl, "#osivia-modal", bundle.getString("MODIFY_PAGE_PROPERTIES_ACTION"), "glyphicons glyphicons-basic-pencil", true);
                }   else    {
                    Map<String, String> properties = new HashMap<>();
                    ctrl.addContentRefToProperties(properties, "osivia.properties.id", document.getId());
    
                    String renameUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "EditionWebPagePropertiesPortletInstance", properties, PortalUrlType.MODAL);
                    this.addToolbarItem(editionList, renameUrl, "#osivia-modal", bundle.getString("MODIFY_PAGE_PROPERTIES_ACTION"), "glyphicons glyphicons-basic-pencil", true);                    
                }
            }

            // Rename URL
            Map<String, String> properties = new HashMap<>();
            ctrl.addContentRefToProperties(properties, "osivia.rename.id", id);


            String renameUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "RenameInstance", properties, PortalUrlType.MODAL);
            this.addToolbarItem(editionList, renameUrl, "#osivia-modal", bundle.getString("MODIFY_PAGE_RENAME_ACTION"), "glyphicons glyphicons-basic-text", true);


            String deleteContentUrl;
            properties = new HashMap<>();
            ctrl.addContentRefToProperties(properties, "osivia.delete.id", id);

            deleteContentUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "DeleteContentPortletInstance", properties, PortalUrlType.MODAL);
            this.addToolbarItem(editionList, deleteContentUrl, "#osivia-modal", bundle.getString("MODIFY_PAGE_DELETE_ACTION"), "glyphicons glyphicons glyphicons-basic-bin", true);


            if (document instanceof MemoryRepositoryPage || document instanceof MemoryRepositorySpace) {
                if (status.isManageable()) {
                    Map<String, String> aclsProperties = new HashMap<>();
                    ctrl.addContentRefToProperties(aclsProperties, "osivia.acls.id", document.getId());

                    String aclsUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "EditionPageAclsPortletInstance", aclsProperties, PortalUrlType.MODAL);
                    this.addToolbarItem(editionList, aclsUrl, "#osivia-modal", bundle.getString("MODIFY_PAGE_ACLS_ACTION"), "glyphicons glyphicons-basic-lock", true);
                }
            }


            Map<String, String> treeProperties = new HashMap<>();

            ctrl.addContentRefToProperties(treeProperties, "osivia.space.id", document.getSpaceId());
            ctrl.addContentRefToProperties(treeProperties, "osivia.move.id", document.getId());

            String treeUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "EditionTreeInstance", treeProperties, PortalUrlType.MODAL);
            this.addToolbarItem(editionList, treeUrl, "#osivia-modal", bundle.getString("MODIFY_MOVE_ACTION"), "glyphicons glyphicons-basic-list", true);


            if (cmsContext.isPreview()) {
                cmsContext.setPreview(false);
                try {
                    cmsService.getCMSSession(cmsContext).getDocument(id);
                    status.setHavingPublication(true);
                } catch (CMSException e) {
                    // Not found
                } finally {
                    cmsContext.setPreview(true);
                }
            }

            if (repository.supportPreview() && personnalization.isModifiable() && cmsContext.isPreview()) {
                if (portalControllerContext.getResponse() instanceof RenderResponse) {
                    PortletURL publishURL = ((RenderResponse) portalControllerContext.getResponse()).createActionURL();
                    publishURL.setParameter(ActionRequest.ACTION_NAME, "publish");
                    this.addToolbarItem(editionList, publishURL.toString(), "dropdown-link", bundle.getString("MODIFY_PAGE_ACCESS_PUBLISH"), "glyphicons glyphicons-basic-globe", true);
                }
            }
            
            if( status.isHavingPublication())   {
                if (portalControllerContext.getResponse() instanceof RenderResponse) {
                    PortletURL publishURL = ((RenderResponse) portalControllerContext.getResponse()).createActionURL();
                    publishURL.setParameter(ActionRequest.ACTION_NAME, "unpublish");
                    this.addToolbarItem(editionList, publishURL.toString(), "dropdown-link", bundle.getString("MODIFY_PAGE_ACCESS_UNPUBLISH"), "glyphicons glyphicons-basic-globe-data", true);
                }                
            }
        }
    }


    private void getSpaceConfiguration(PortalControllerContext portalControllerContext, CMSController ctrl, Bundle bundle, Element toolbar, Document document, Boolean isAdministrator, CMSContext cmsContext, AdvancedRepository repository) throws PortalException {
        Element configurationList;

            Element menu = DOM4JUtils.generateDivElement("dropdown");
            Element menuTitle = DOM4JUtils.generateLinkElement("#", null, null, "btn dropdown-toggle no-ajax-link", null, "glyphicons glyphicons-basic-folder");
            menuTitle.addAttribute(new QName("id"), "spaceConfigurationMenu");
            Element text = DOM4JUtils.generateElement("span", "d-none d-xl-inline", bundle.getString("MODIFY_MENU_SPACE"));
            menuTitle.add(text);

            DOM4JUtils.addDataAttribute(menu, "bs-toggle", "dropdown");

            menu.add(menuTitle);

            configurationList = DOM4JUtils.generateDivElement("dropdown-menu");

            configurationList.addAttribute(new QName("aria-labelledby"), "spaceConfigurationMenu");
            menu.add(configurationList);


            Element menuRoot = DOM4JUtils.generateDivElement("");
            menuRoot.add(menu);

            toolbar.add(menuRoot);


            // Only one space if association with host
            if( PortalObjectUtils.getHostPortalID(portalControllerContext.getHttpServletRequest()) == null)	{

	            if ((!repository.supportPreview() || cmsContext.isPreview()) ) {
	                if (portalControllerContext.getResponse() instanceof RenderResponse) {
	                    PortletURL createSpaceUrl = ((RenderResponse) portalControllerContext.getResponse()).createActionURL();
	                    createSpaceUrl.setParameter(ActionRequest.ACTION_NAME, "addSpace");
	                    this.addToolbarItem(configurationList, createSpaceUrl.toString(), "dropdown-link", bundle.getString("MODIFY_SPACE_CREATE_ACTION"), "glyphicons glyphicons-basic-square-empty-plus", true);
	                }
	            }
            }

            
            // Space modification
            String modifySpaceUrl;

            String title = bundle.getString("MODIFY_SPACE_MODIFY_TITLE");

            // Window properties
            Map<String, String> spaceProperties = new HashMap<>();
            spaceProperties.put("osivia.title", title);
            spaceProperties.put("osivia.hideTitle", "1");
            ctrl.addContentRefToProperties(spaceProperties, "osivia.space.id", document.getSpaceId());


            modifySpaceUrl = portalUrlFactory.getStartPortletInNewPage(portalControllerContext, "EditionModifySpaceInstance", title, "EditionModifySpaceInstance", spaceProperties, new HashMap<>());


            this.addToolbarItem(configurationList, modifySpaceUrl, "dropdown-link", bundle.getString("MODIFY_SPACE_MODIFY_LINK"), "glyphicons glyphicons-basic-pencil", true);


            Map<String, String> treeProperties = new HashMap<>();

            ctrl.addContentRefToProperties(treeProperties, "osivia.space.id", document.getSpaceId());
            ctrl.addContentRefToProperties(treeProperties, "osivia.browse.id", document.getId());

            String treeUrl = portalUrlFactory.getStartPortletUrl(portalControllerContext, "EditionTreeInstance", treeProperties, PortalUrlType.MODAL);
            this.addToolbarItem(configurationList, treeUrl, "#osivia-modal", bundle.getString("MODIFY_BROWSE_ACTION"), "glyphicons glyphicons-basic-list", true);

    }


    private void addTemplateLink(PortalControllerContext portalControllerContext, CMSController ctrl, Bundle bundle, Element toolbar, UniversalID id, String templatePath, String cmsTemplatePath) {
    	
    	UniversalID templateID = getTemplateID( portalControllerContext,   id,  templatePath,  cmsTemplatePath);
    	
        if (templateID != null) {

            CMSContext cmsTemplateContext = ctrl.getCMSContext();
            cmsTemplateContext.setPreview(false);

            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, cmsTemplateContext, templateID);
            this.addToolbarItem(toolbar, url, "dropdown-link", bundle.getString("MODIFY_PAGE_ACCESS_TO_TEMPLATE"), "glyphicons glyphicons-basic-thumbnails", true);
        }

    	
    }
    
    
    private UniversalID getTemplateID(PortalControllerContext portalControllerContext, UniversalID id, String templatePath, String cmsTemplatePath) {
    	 UniversalID templateID = null;
    	
    	if (templatePath != null) {
            String templateTokens[] = templatePath.split(":");
            String templatePathItems[] = templateTokens[1].split("/");


            // Portal template
            UniversalID portalTemplateId = new UniversalID(templateTokens[0], templatePathItems[templatePathItems.length - 1]);

            // Get real content (ignore root item)
            UniversalID templateExtractedContentId;
            if (portalTemplateId.getInternalID().equals("root")) {
                String extractedInternalId = templatePathItems[templatePathItems.length - 2].split("__")[0];
                templateExtractedContentId = new UniversalID(templateTokens[0], extractedInternalId);
            } else
                templateExtractedContentId = portalTemplateId;


            if (id.equals(templateExtractedContentId)) {
                if (cmsTemplatePath != null) {
                    // CMS template
                    String cmsTemplateTokens[] = cmsTemplatePath.split(":");
                    templateID = new UniversalID(cmsTemplateTokens[0], cmsTemplateTokens[1].substring(cmsTemplateTokens[1].lastIndexOf("/") + 1));
                } else {
                    templateID = null;
                }
            } else {
                templateID = portalTemplateId;
            }
        }
    	
    	return templateID;
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
     * Get search filters form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public EditionForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        EditionForm form = this.applicationContext.getBean(EditionForm.class);
        try {
            form.setLocale(localService.getLocale(portalCtx));


        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return form;
    }


    /**
     * Update locale.
     *
     * @param form the form
     */

    @ActionMapping(name = "submit", params = "update-locale")
    public void updateLocale(PortletRequest request, ActionResponse response, @ModelAttribute("form") EditionForm form) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);


        try {
            String contentId = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.navigationId");
            if (contentId != null) {
                UniversalID id = new UniversalID(contentId);
                CMSController ctrl = new CMSController(portalCtx);
                CMSContext cmsContext = ctrl.getCMSContext();

                Document currentDoc = cmsService.getCMSSession(cmsContext).getDocument(id);

                localService.setLocale(portalCtx, form.getLocale());

                cmsContext.setLocale(form.getLocale());

                String url = portalUrlFactory.getViewContentUrl(portalCtx, cmsContext, currentDoc.getSpaceId());
                response.sendRedirect(url);
            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);

        }


    }


    
    
    /**
     * Update locale.
     *
     * @param form the form
     */

    @ActionMapping(name = "home")
    public void home(PortletRequest request, ActionResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        try {
            String contentId = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.navigationId");
            if (contentId != null) {
                UniversalID id = new UniversalID(contentId);
                CMSController ctrl = new CMSController(portalCtx);
                CMSContext cmsContext = ctrl.getCMSContext();

                Document currentDoc = cmsService.getCMSSession(cmsContext).getDocument(id);

                String url = portalUrlFactory.getViewContentUrl(portalCtx, ctrl.getCMSContext(), currentDoc.getSpaceId());
                response.sendRedirect(url);
            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);

        }
    }

    
    /**
     * Update locale.
     *
     * @param form the form
     */

    @ActionMapping(name = "initCaches")
    public void initCaches(PortletRequest request, ActionResponse response) throws PortletException {

      	haService.initPortalParameters();
      	try {
      		// Refresh portlets
			response.sendRedirect("/refresh");
		} catch (IOException e) {
			throw new PortletException();
		}

    }
    
    
    /**
     * Search filters form init binder.
     *
     * @param binder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.registerCustomEditor(Locale.class, this.localPropertyEditor);

    }

}
