package org.osivia.portal.cms.portlets.edition.page.creation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
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
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.rename.controller.RenameFormValidator;
import org.osivia.portal.core.constants.InternalConstants;
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
 * Creation page controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class CreationController extends GenericPortlet implements PortletContextAware, ApplicationContextAware   {




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
    
    @Autowired
    private CreationFormValidator validator;
    
    /** The logger. */
    protected static Log logger = LogFactory.getLog(CreationController.class);

    /**
     * Constructor.
     */
    public CreationController() {
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
    public void createPage(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") CreationForm form, BindingResult result, SessionStatus sessionStatus) throws PortletException {


        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            if (!result.hasErrors()) {

                NavigationItem space = getSpace(portalControllerContext);

                boolean checkId = checkNewDocument(portalControllerContext, form.getId());

                if (!checkId) {
                    result.rejectValue("id", "MODIFY_PAGE_CREATION_ID_ALREADY_EXISTS");
                } else {

                    sessionStatus.setComplete();

                    // Portal controller context
                    CMSController ctrl = new CMSController(portalControllerContext);
                    CMSContext cmsContext = ctrl.getCMSContext();


                    AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, space.getSpaceId().getRepositoryName());


                    if( StringUtils.isNotEmpty(form.getModel()) && form.isNoModel() == false)    {
                        MemoryRepositoryPage model = (MemoryRepositoryPage) getDocument( portalControllerContext, form.getModel());
                        Map<String,Object> newProps = new LinkedHashMap<>();
                        newProps.putAll(model.getProperties());
                        newProps.put("dc:title",  form.getDisplayName());
                        repository.addPage(form.getId(),  form.getDisplayName(), form.getTarget(), newProps,model.getModuleRefs(), model.getACL());
                    }   else    {
                        Map<String,Object> newProps = new LinkedHashMap<>();
                        newProps.put("dc:title",  form.getDisplayName());
                        repository.addPage(form.getId(),  form.getDisplayName(), form.getTarget(), newProps,new ArrayList<>(), new ArrayList<>());
                    }
 

                    String url = this.portalUrlFactory.getViewContentUrl(portalControllerContext, cmsContext, new UniversalID(space.getSpaceId().getRepositoryName(), form.getId()));
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
        UniversalID spaceId = new UniversalID(window.getProperty("osivia.space.id"));
        
        
        CMSContext cmsContext = ctrl.getCMSContext();
        
        boolean check = false;

        try {
            cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(spaceId.getRepositoryName(), checkId));
        
        } catch( CMSException e) {
            check = true;
        }
        
        return check;
    }
    
    
    
    private Document getDocument(PortalControllerContext portalControllerContext, String docId) throws CMSException {
        
        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);

        
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        UniversalID spaceId = new UniversalID(window.getProperty("osivia.space.id"));
        
        
        CMSContext cmsContext = ctrl.getCMSContext();
  
        return   cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(spaceId.getRepositoryName(), docId));
        
    }
    
    
    

    /**
     * get Current space
     * 
     * @param portalCtx
     * @return
     * @throws CMSException
     */
    private NavigationItem getSpace(PortalControllerContext portalCtx) throws CMSException {
        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");


        UniversalID id = new UniversalID(spaceId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        NavigationItem document = cmsService.getCMSSession(cmsContext).getNavigationItem(id);

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
    public CreationForm getForm(PortletRequest request, PortletResponse response) throws PortletException {


        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        CreationForm form = this.applicationContext.getBean(CreationForm.class);


        return form;


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
    
    
    
    
    /**
     * Browse resource mapping.
     *
     * @param request resource request
     * @param response resource response
     */
    @ResourceMapping("browse")
    public void browse(ResourceRequest request, ResourceResponse response,@ModelAttribute("form") CreationForm form) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

  
        // Browse
        String data;
        try {
            data = browse(portalControllerContext, form, request.getParameter("treeName"));
        } catch (Exception e) {
            throw new PortletException(e);
        }


        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(data);
        printWriter.close();
    }    
    
    
    
    
    private String browse(PortalControllerContext portalControllerContext, CreationForm form, String treeName) throws CMSException, JSONException {
        // JSON array

        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

      
        if( "target".equals(treeName))  {

            JSONObject jsonObject = this.generateBrowseJSONObject(portalControllerContext, bundle, getSpace(portalControllerContext), true, form.getTarget());
            JSONArray jsonArray=new JSONArray();
                        
            if (jsonObject != null) {
                jsonArray.put(jsonObject);
            }

            return jsonArray.toString();
        }
        
        if( "model".equals(treeName))  {
            
            JSONObject jsonObject = this.generateBrowseJSONObject(portalControllerContext, bundle, getSpace(portalControllerContext), true, form.getModel());
            JSONArray jsonArray = jsonObject.getJSONArray("children");
            if( jsonArray == null)
                jsonArray = new JSONArray();
            return jsonArray.toString();
        }

        return null;

    }


    private JSONObject generateBrowseJSONObject(PortalControllerContext portalControllerContext, Bundle bundle, NavigationItem navItem, boolean root, String currentId) throws JSONException, CMSException {
        JSONObject object = new JSONObject();


        String nodeId = navItem.getDocumentId().getInternalID();
        if (root || !navItem.getChildren().isEmpty()) {
            object.put("expanded", "true");
        }
        object.put("title", "<div class=\"d-flex justify-content-between\">"+ "<div class=\"text-truncate\">"+navItem.getTitle()+"</div>" + "<div class=\"page-id\">["+navItem.getDocumentId().getInternalID()+"]</div> </div>");
        object.put("path", nodeId);
        
        if( nodeId.equals(currentId))
            object.put("active", true);

        // Children
        if (!navItem.getChildren().isEmpty()) {
            JSONArray childrenArray = new JSONArray();
            for (NavigationItem child : navItem.getChildren()) {
                JSONObject childObject = this.generateBrowseJSONObject(portalControllerContext, bundle, child, false, currentId);
                childrenArray.put(childObject);
            }

            object.put("children", childrenArray);
        }

        return object;
    }
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
