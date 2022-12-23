package org.osivia.portal.cms.portlets.edition.page.properties.controller;

import java.io.IOException;
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
public class PropertiesController extends GenericPortlet implements PortletContextAware, ApplicationContextAware   {

    private static final String OSIVIA_CMS_PROPAGATE_SELECTORS = "osivia.cms.propagateSelectors";

	private static final String OSIVIA_PAGE_CATEGORY = "osivia.pageCategory";
	


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
    private PropertiesFormValidator validator;
    
    /** The logger. */
    protected static Log logger = LogFactory.getLog(PropertiesController.class);

    /**
     * Constructor.
     */
    public PropertiesController() {
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
    public void updateProperties(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") PropertiesForm form, BindingResult result, SessionStatus sessionStatus) throws PortletException {


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


                    if (!StringUtils.equals(form.getId(), document.getInternalID())) {
                        // Change ID
                        ((AdvancedRepository) repository).setNewId(document.getInternalID(), form.getId());
                        document = getNewDocument(portalControllerContext, form.getId());
                    }


                    if (StringUtils.isNotEmpty(form.getLayoutId()))
                        document.getProperties().put(ThemeConstants.PORTAL_PROP_LAYOUT, form.getLayoutId());
                    else
                        document.getProperties().remove(ThemeConstants.PORTAL_PROP_LAYOUT);

                    if (StringUtils.isNotEmpty(form.getThemeId()))
                        document.getProperties().put(ThemeConstants.PORTAL_PROP_THEME, form.getThemeId());
                    else
                        document.getProperties().remove(ThemeConstants.PORTAL_PROP_THEME);
                    
                    String pageCategoryPrefix = System.getProperty(InternalConstants.SYSTEM_PROPERTY_PAGE_CATEGORY_PREFIX);

                    if (pageCategoryPrefix != null) {
	                    if (StringUtils.isNotEmpty(form.getCategory()))
	                        document.getProperties().put(OSIVIA_PAGE_CATEGORY, form.getCategory());
	                    else
	                        document.getProperties().remove(OSIVIA_PAGE_CATEGORY);
                    }
                    
                    // Selectors propagation
                    if (form.isSelectorsPropagation()) {
                    	document.getProperties().put(OSIVIA_CMS_PROPAGATE_SELECTORS, "1");
                    } else if (document.getProperties().get(OSIVIA_CMS_PROPAGATE_SELECTORS) != null) {
                    	document.getProperties().remove(OSIVIA_CMS_PROPAGATE_SELECTORS);
                    }
                    


                    ((AdvancedRepository) repository).updateDocument(form.getId(), document);

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
        
        } catch( CMSException e) {
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
        

        RepositoryDocument document = (RepositoryDocument)
            cmsService.getCMSSession(cmsContext).getDocument(new UniversalID(navigationId.getRepositoryName(), newId));

        
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
    public PropertiesForm getForm(PortletRequest request, PortletResponse response) throws PortletException {

        try {

            // Portal Controller context
            PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
            
            PropertiesForm form = this.applicationContext.getBean(PropertiesForm.class);

            Bundle bundle = bundleFactory.getBundle(portalCtx.getHttpServletRequest().getLocale());
            
            RepositoryDocument document = getDocument(portalCtx);
            
            // if spaceId associated to host, the modification of the ID of the main page
            // would lead to a unrecoverable error
            form.setModifiableId(true);
            UniversalID portalId =  PortalObjectUtils.getHostPortalID(portalCtx.getHttpServletRequest()) ;
            if( portalId != null && portalId.equals(document.getId()))	{
            	form.setModifiableId(false);
            }
            
            form.setId( document.getInternalID());
            
     
            String charteCtx = PortalObjectUtils.getHostCharteContext(portalCtx.getHttpServletRequest());
            
             //Layouts

            form.setLayoutId((String) document.getProperties().get(ThemeConstants.PORTAL_PROP_LAYOUT));

            Map<String, String> formLayouts = new LinkedHashMap<String, String>();


			formLayouts.put("", bundle.getString("MODIFY_PAGE_PROPERTIES_DEFAULT_ITEM"));
            
            @SuppressWarnings("unchecked")
            ArrayList<PortalLayout> layouts = new ArrayList<>((Collection<PortalLayout>) layoutService.getLayouts());
          
            Collections.sort(layouts, new Comparator<PortalLayout>() {
                @Override
                public int compare(PortalLayout o1, PortalLayout o2) {
                    return o1.getLayoutInfo().getName().compareTo( o2.getLayoutInfo().getName());
                }
            });
            

             for (PortalLayout layout : layouts) {
            	 if( charteCtx == null || layout.getLayoutInfo().getContextPath().equals(charteCtx))
                	formLayouts.put(layout.getLayoutInfo().getName(), layout.getLayoutInfo().getName());
            }
             
             

            form.setLayouts(formLayouts);
            
            // Themes
            
            form.setThemeId((String) document.getProperties().get(ThemeConstants.PORTAL_PROP_THEME));

            Map<String, String> formThemes = new LinkedHashMap<String, String>();

            formThemes.put("", bundle.getString("MODIFY_PAGE_PROPERTIES_DEFAULT_ITEM"));

            @SuppressWarnings("unchecked")
            ArrayList<PortalTheme> themes = new ArrayList<>((Collection<PortalTheme>) themeService.getThemes());
          
            Collections.sort(themes, new Comparator<PortalTheme>() {
                @Override
                public int compare(PortalTheme o1, PortalTheme o2) {
                    return o1.getThemeInfo().getName().compareTo( o2.getThemeInfo().getName());
                }
            });
            

            for (PortalTheme theme : themes) {
            	if( charteCtx == null || theme.getThemeInfo().getContextPath().equals(charteCtx))
            		formThemes.put(theme.getThemeInfo().getName(), theme.getThemeInfo().getName());
            }

            form.setThemes(formThemes);
            
            

            // Selectors propagation page indicator
            Boolean selectorsPropagation = "1".equals((String) document.getProperties().get(OSIVIA_CMS_PROPAGATE_SELECTORS));
            form.setSelectorsPropagation(selectorsPropagation);

            // categories (optional)
            String pageCategoryPrefix = System.getProperty(InternalConstants.SYSTEM_PROPERTY_PAGE_CATEGORY_PREFIX);

            if (pageCategoryPrefix != null) {

                String category = (String) document.getProperties().get(OSIVIA_PAGE_CATEGORY);
                form.setCategory(category);

                Map<String, String> categories = new LinkedHashMap<>();


                categories.put("", bundle.getString("MODIFY_PAGE_PROPERTIES_NO_CATEGORY_ITEM"));

                TreeSet<OrderedPageCategory> orderedCategories = new TreeSet<>();

                Properties properties = System.getProperties();
                Enumeration<Object> props = properties.keys();
                while (props.hasMoreElements()) {

                    String key = (String) props.nextElement();

                    if (key.startsWith(pageCategoryPrefix)) {
                        String curCategory = key.substring(pageCategoryPrefix.length());

                        int curOrder = 100;

                        try {
                            curOrder = Integer.parseInt(curCategory);
                        } catch (NumberFormatException e) {
                            // non orderable
                        }
                        String curLabel = (String) properties.get(key);

                        orderedCategories.add(new OrderedPageCategory(curOrder, curCategory, curLabel));

                    }
                }


                for (OrderedPageCategory pageCategory : orderedCategories) {
                    categories.put(pageCategory.getCode(), pageCategory.getLabel());
                }


                form.setCategories(categories);
            }           
            
            

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
