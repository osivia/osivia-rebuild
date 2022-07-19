package org.osivia.portal.cms.portlets.edition.page.apps.modify.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import org.apache.commons.lang3.BooleanUtils;
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
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.rename.controller.RenameForm;
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

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * CMS portlet edition properties
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class ModifyController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;
    

    @Autowired
    private IPortalUrlFactory portalUrlFactory;    

    /** Application context. */
    private ApplicationContext applicationContext;


    /** The logger. */
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
        if( templateId != null) {
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

        TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
        if (repository instanceof TestRepository) {
            ((TestRepository) repository).updateDocument(id.getInternalID(), (RepositoryDocument) document);
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
     * @param request portlet request
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

        return form;
    }

    
    
    @ModelAttribute("stylesList")
    protected List<String> getStyles(PortletRequest request, PortletResponse response) throws Exception {
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        return getTemplateSpace(portalCtx).getStyles();
    }

}
