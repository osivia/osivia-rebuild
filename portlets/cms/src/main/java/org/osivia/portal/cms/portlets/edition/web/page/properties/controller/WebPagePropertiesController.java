package org.osivia.portal.cms.portlets.edition.web.page.properties.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.rename.controller.RenameFormValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Web page controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class WebPagePropertiesController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

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
    private LayoutService layoutService;

    @Autowired
    private ThemeService themeService;

    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;

    @Autowired
    private IBundleFactory bundleFactory;

    @Autowired
    private WebPagePropertiesFormValidator validator;

    /** The logger. */
    protected static Log logger = LogFactory.getLog(WebPagePropertiesController.class);

    /**
     * Constructor.
     */
    public WebPagePropertiesController() {
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
    public void updateProperties(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") WebPagePropertiesForm form, BindingResult result, SessionStatus sessionStatus) throws PortletException {


        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            if (!result.hasErrors()) {

                RepositoryDocument document;

                document = getDocument(portalControllerContext);

                boolean checkId = true;
                if (!StringUtils.equals(form.getId(), document.getInternalID())) {
                    checkId = checkNewDocument(portalControllerContext, form.getId());
                }

                if (!checkId) {
                    result.rejectValue("id", "MODIFY_PAGE_PROPERTIES_ID_ALREADY_EXISTS");
                } else {

                    sessionStatus.setComplete();

                    // Portal controller context
                    CMSController ctrl = new CMSController(portalControllerContext);
                    CMSContext cmsContext = ctrl.getCMSContext();


                    AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, document.getId().getRepositoryName());

                    /*
                     * if (!StringUtils.equals(form.getId(), document.getInternalID())) {
                     * // Change ID
                     * ((AdvancedRepository) repository).setNewId(document.getInternalID(), form.getId());
                     * document = getNewDocument(portalControllerContext, form.getId());
                     * }
                     */

                    UniversalID oldTemplateID = ((Templateable) document).getTemplateId();

                    if (StringUtils.isNotEmpty(form.getTemplateId())) {
                        if (oldTemplateID == null || !oldTemplateID.equals( new UniversalID(form.getTemplateId())))
                            repository.setTemplateId(document.getInternalID(), new UniversalID( form.getTemplateId()));
                    } else {
                        if (oldTemplateID != null) {
                            repository.setTemplateId(document.getInternalID(), null);
                        }
                    }


                    String url = this.portalUrlFactory.getViewContentUrl(portalControllerContext, cmsContext, new UniversalID(document.getId().getRepositoryName(), form.getId()));
                    response.sendRedirect(url);
                }
            }


        } catch (CMSException | IOException e) {
            throw new PortletException(e);
        }
    }


    private boolean checkNewDocument(PortalControllerContext portalControllerContext, String checkId) throws CMSException {

        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);


        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        UniversalID navigationId = new UniversalID(window.getProperty("osivia.properties.id"));


        CMSContext cmsContext = ctrl.getCMSContext();

        boolean check = false;

        try {
            cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(navigationId.getRepositoryName(), checkId));

        } catch (CMSException e) {
            check = true;
        }

        return check;
    }

    private RepositoryDocument getNewDocument(PortalControllerContext portalControllerContext, String newId) throws CMSException {

        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);


        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        UniversalID navigationId = new UniversalID(window.getProperty("osivia.properties.id"));


        CMSContext cmsContext = ctrl.getCMSContext();


        RepositoryDocument document = (RepositoryDocument) cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(navigationId.getRepositoryName(), newId));


        return document;
    }

    private RepositoryDocument getDocument(PortalControllerContext portalControllerContext) throws CMSException {

        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);


        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        String navigationId = window.getProperty("osivia.properties.id");


        CMSContext cmsContext = ctrl.getCMSContext();


        RepositoryDocument document = (RepositoryDocument) cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(navigationId));
        return document;
    }
    
    
    /**
     * get Current space
     * 
     * @param portalCtx
     * @return
     * @throws CMSException
     */
    private NavigationItem getTemplateSpace(PortalControllerContext portalCtx, UniversalID spaceId) throws CMSException {
 
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        cmsContext.setPreview(false);
        NavigationItem document = cmsService.getCMSSession(cmsContext).getNavigationItem(spaceId);

        return document;
    }
    
    


    /**
     * Browse resource mapping.
     *
     * @param request resource request
     * @param response resource response
     */
    @ResourceMapping("browse")
    public void browse(ResourceRequest request, ResourceResponse response, @ModelAttribute("form") WebPagePropertiesForm form) throws PortletException {
        // JSON array) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        try {

            UniversalID pageId;
            
            if( form.getTemplateId() != null)
                pageId = new UniversalID(form.getTemplateId());
            else
                pageId = null;
            
            // Browse
            String data;

            data = browse(portalControllerContext, pageId, form.getTemplateSpaceId());


            // Content type
            response.setContentType("application/json");

            // Content
            PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
            printWriter.write(data);
            printWriter.close();

        } catch (Exception e) {
            throw new PortletException(e);
        }
    }


    private String browse(PortalControllerContext portalControllerContext, UniversalID pageId, UniversalID templateSpaceId) throws CMSException, JSONException {
        // JSON array
        JSONArray jsonArray;
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());


        jsonArray = new JSONArray();


        JSONObject jsonObject = this.generateBrowseJSONObject(portalControllerContext, bundle, getTemplateSpace(portalControllerContext, templateSpaceId), true, pageId);
        if (jsonObject != null) {
            jsonArray.put(jsonObject);
        }


        return jsonArray.toString();

    }


    private JSONObject generateBrowseJSONObject(PortalControllerContext portalControllerContext, Bundle bundle, NavigationItem navItem, boolean root, UniversalID pageId) throws JSONException, CMSException {
        JSONObject object = new JSONObject();

        object.put("title", navItem.getTitle());
        object.put("href", portalUrlFactory.getViewContentUrl(portalControllerContext, navItem.getDocumentId()));
        object.put("path", navItem.getDocumentId().toString());
        String active = navItem.getDocumentId().equals(pageId) ? "true" : null;
        if( active != null)
            object.put("active", active);


        // Children
        if (!navItem.getChildren().isEmpty()) {
            JSONArray childrenArray = new JSONArray();
            for (NavigationItem child : navItem.getChildren()) {
                JSONObject childObject = this.generateBrowseJSONObject(portalControllerContext, bundle, child, false, pageId);
                childrenArray.put(childObject);
            }

            object.put("children", childrenArray);
        }

        return object;
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
    public WebPagePropertiesForm getForm(PortletRequest request, PortletResponse response) throws PortletException {

        try {

            // Portal Controller context
            PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

            WebPagePropertiesForm form = this.applicationContext.getBean(WebPagePropertiesForm.class);

            RepositoryDocument document = getDocument(portalCtx);

            form.setId(document.getInternalID());


            UniversalID templateID = ((Templateable) document).getTemplateId();
            if (templateID != null) {
                form.setTemplateId(templateID.toString());
                
            }

            form.setTemplateSpaceId(new UniversalID(System.getProperty("osivia.portal.default")));


            return form;

        } catch (

        PortalException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Form init binder.
     *
     * @param binder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.addValidators(this.validator);
    }

}
