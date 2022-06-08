package org.osivia.portal.cms.portlets.edition.page.apps.delete.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
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

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Delete portlet controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")

public class DeleteController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

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
    IAppsService appServices;

    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;

    /** The logger. */
    protected static Log logger = LogFactory.getLog(DeleteController.class);

    /**
     * Constructor.
     */
    public DeleteController() {
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
     * Delete portlet
     */
    @ActionMapping(name = "deletePortlet")
    public void delete(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
            CMSController ctrl = new CMSController(portalControllerContext);


            deletePortlet(request, portalControllerContext, ctrl);

            String url = this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
            response.sendRedirect(url);
        } catch (PortalException e) {
            throw new PortletException(e);
        } catch (IOException e) {
            throw new PortletException(e);
        }
    }

    protected void deletePortlet(ActionRequest request, PortalControllerContext portalControllerContext, CMSController ctrl) throws CMSException {
        String navigationId = getNavigationId(request);
        if (navigationId != null) {
            UniversalID id = new UniversalID(navigationId);

            PortalWindow window = WindowFactory.getWindow(request);
            String windowName = window.getProperty("osivia.cms.edition.targetWindow");
            
            
            CMSContext cmsContext = ctrl.getCMSContext();
            Document document = cmsService.getCMSSession(cmsContext).getDocument(id);

            List<ModuleRef> modules;

            if (document instanceof ModulesContainer) {
                // Search src module
                modules = ((ModulesContainer) document).getModuleRefs();
            } else {
                modules = new ArrayList<>();
            }

            // Search src module
            ModuleRef srcModule = null;


            for (ModuleRef module : modules) {
                if (module.getWindowName().equals(windowName)) {
                    srcModule = module;
                    break;
                }
            }

            // Remove src module
            modules.remove(srcModule);


            TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
            if (repository instanceof TestRepository) {
                ((TestRepository) repository).updateDocument(id.getInternalID(), (RepositoryDocument) document);
            }

        }
    }


    private String getNavigationId(ActionRequest request) {
        PortalWindow window = WindowFactory.getWindow(request);
        String navigationId = window.getProperty("osivia.navigationId");
        if (navigationId == null)
            navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
        return navigationId;
    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
