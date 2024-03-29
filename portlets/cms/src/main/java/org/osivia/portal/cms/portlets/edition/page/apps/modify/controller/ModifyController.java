package org.osivia.portal.cms.portlets.edition.page.apps.modify.controller;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.*;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ui.layout.LayoutGroup;
import org.osivia.portal.api.ui.layout.LayoutItemsService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import javax.portlet.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * CMS portlet edition properties
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class ModifyController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

    /**
     * Application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Portlet context.
     */
    private PortletContext portletContext;


    /**
     * CMS service.
     */
    @Autowired
    private CMSService cmsService;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Layout items service.
     */
    @Autowired
    private LayoutItemsService layoutItemsService;


    /**
     * The logger.
     */
    protected static Log logger = LogFactory.getLog(ModifyController.class);

    /**
     * Constructor.
     */
    public ModifyController() {
        super();
    }


    /**
     * Default render mapping.
     *
     * @param request  render request
     * @param response render response
     * @return render view path
     * @throws PortalException
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortalException {
        return "view";
    }


    /**
     * Modify portlet action
     *
     * @throws IOException
     */
    @ActionMapping(name = "modifyPortlet")
    public void modifyPortlet(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        Document document = getDocument(portalControllerContext);

        ModuleRef srcModule = getModule(portalControllerContext, document);

        if (StringUtils.isNoneEmpty(form.getTitle()))
            srcModule.getProperties().put("osivia.title", form.getTitle());
        else
            srcModule.getProperties().remove("osivia.title");

        if (BooleanUtils.isTrue(form.isDisplayTitle()))
            srcModule.getProperties().remove("osivia.hideTitle");
        else
            srcModule.getProperties().put("osivia.hideTitle", "1");

        if (BooleanUtils.isTrue(form.isDisplayPanel()))
            srcModule.getProperties().put("osivia.bootstrapPanelStyle", BooleanUtils.toStringTrueFalse(true));
        else
            srcModule.getProperties().remove("osivia.bootstrapPanelStyle");

        if (BooleanUtils.isTrue(form.isHideIfEmpty()))
            srcModule.getProperties().put("osivia.hideEmptyPortlet", "1");
        else
            srcModule.getProperties().remove("osivia.hideEmptyPortlet");

        srcModule.getProperties().put("osivia.style", String.join(",", form.getStyles()));

        // Linked layout item identifier
        if (StringUtils.isEmpty(form.getLinkedLayoutItemId())) {
            srcModule.getProperties().remove(LayoutItemsService.LINKED_ITEM_ID_WINDOW_PROPERTY);
        } else {
            srcModule.getProperties().put(LayoutItemsService.LINKED_ITEM_ID_WINDOW_PROPERTY, form.getLinkedLayoutItemId());
        }


        updateDocument(portalControllerContext, document);

        String url = this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
        response.sendRedirect(url);
    }


    private ModuleRef getModule(PortalControllerContext portalControllerContext, Document document) throws CMSException {

        String targetWindow = WindowFactory.getWindow(portalControllerContext.getRequest()).getProperty("osivia.cms.edition.targetWindow");

        ModuleRef srcModule = null;

        if (document instanceof ModulesContainer) {
            // Search src module
            List<ModuleRef> modules = ((ModulesContainer) document).getModuleRefs();
            for (ModuleRef module : modules) {
                if (module.getWindowName().equals(targetWindow)) {
                    srcModule = module;
                    break;
                }
            }
        }

        return srcModule;
    }

    private Document getDocument(PortalControllerContext portalCtx) throws CMSException {

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String navigationId = window.getProperty("osivia.navigationId");

        if (navigationId == null)
            navigationId = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.navigationId");

        UniversalID id = new UniversalID(navigationId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        Document document = cmsService.getCMSSession(cmsContext).getDocument(id);

        return document;
    }

    private Space getTemplateSpace(PortalControllerContext portalCtx) throws CMSException {

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
        return space;
    }


    private void updateDocument(PortalControllerContext portalCtx, Document document) throws CMSException {

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String navigationId = window.getProperty("osivia.navigationId");

        if (navigationId == null)
            navigationId = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.navigationId");

        UniversalID id = new UniversalID(navigationId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();

        AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
        if (repository instanceof AdvancedRepository) {
            ((AdvancedRepository) repository).updateDocument(id.getInternalID(), (RepositoryDocument) document);
        }
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
     * Get form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public ModifyForm getForm(PortletRequest request, PortletResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        Document document = getDocument(portalCtx);

        ModuleRef srcModule = getModule(portalCtx, document);


        ModifyForm form = this.applicationContext.getBean(ModifyForm.class);
        form.setTitle(srcModule.getProperties().get("osivia.title"));
        form.setDisplayTitle(!StringUtils.equals(srcModule.getProperties().get("osivia.hideTitle"), "1"));
        form.setDisplayPanel(BooleanUtils.toBoolean(srcModule.getProperties().get("osivia.bootstrapPanelStyle")));
        form.setHideIfEmpty(StringUtils.equals(srcModule.getProperties().get("osivia.hideEmptyPortlet"), "1"));

        String sStyles = StringUtils.defaultString(srcModule.getProperties().get("osivia.style"));
        form.setStyles(Arrays.asList(sStyles.split(",")));

        // Linked layout item identifier
        String linkedLayoutItemId = srcModule.getProperties().get(LayoutItemsService.LINKED_ITEM_ID_WINDOW_PROPERTY);
        form.setLinkedLayoutItemId(linkedLayoutItemId);

        return form;
    }


    @ModelAttribute("stylesList")
    public List<String> getStyles(PortletRequest request, PortletResponse response) throws Exception {
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        return getTemplateSpace(portalCtx).getStyles();
    }


    /**
     * Get layout groups.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return layout groups
     */
    @ModelAttribute("layoutGroups")
    public List<LayoutGroup> getLayoutGroups(PortletRequest request, PortletResponse response) throws PortalException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.layoutItemsService.getGroups(portalControllerContext);
    }

}
