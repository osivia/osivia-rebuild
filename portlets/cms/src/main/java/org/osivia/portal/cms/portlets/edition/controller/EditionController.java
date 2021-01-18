package org.osivia.portal.cms.portlets.edition.controller;

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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.WindowFactory;
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

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.producers.test.TestRepository;
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
     * @throws CMSException
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws CMSException {
        return "view";
    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "addPage")
    public void addPage(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

            String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (navigationId != null) {

                UniversalID id = new UniversalID(navigationId);
                
                CMSController ctrl = new CMSController(portalControllerContext);

                CMSContext cmsContext = ctrl.getCMSContext();


                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

                String newID = "" + System.currentTimeMillis();

                ((TestRepository) repository).addEmptyPage(newID, "" + System.currentTimeMillis(), id.getInternalID());


                String url = portalUrlFactory.getViewContentUrl(portalControllerContext, new UniversalID(id.getRepositoryName(), newID));
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
    public void setACL(ActionRequest request, ActionResponse response, @ModelAttribute("status") EditionStatus status) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);


            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);
                
                CMSController ctrl = new CMSController(portalControllerContext);

                CMSContext cmsContext = ctrl.getCMSContext();

                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository.getACL(id.getInternalID()).isEmpty())
                    ((TestRepository) repository).setACL(id.getInternalID(), Arrays.asList("group:members"));
                else
                    ((TestRepository) repository).setACL(id.getInternalID(), new ArrayList<String>());
                
                refreshStatus(portalControllerContext, ctrl, status);
                
            }
        } catch (PortalException   e) {
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


            addPortletToRegion(request, portalControllerContext,ctrl, "SampleInstance", "col-2", TestRepository.POSITION_END);
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
  
            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, id);
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
  
            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, id);
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

            addPortletToRegion(request, portalControllerContext, ctrl,"FragmentInstance", "logo", TestRepository.POSITION_BEGIN);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    protected void addPortletToRegion(ActionRequest request, PortalControllerContext portalControllerContext, CMSController ctrl, String portletName, String region, int position)
            throws CMSException {
        String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
        if (navigationId != null) {

            UniversalID id = new UniversalID(navigationId);

            CMSContext cmsContext = ctrl.getCMSContext();


            TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

            String windowID = "" + System.currentTimeMillis();
            
            Map<String,String> editionProperties = new ConcurrentHashMap<>();
            editionProperties.put("osivia.hideTitle", "1");

            ((TestRepository) repository).addWindow(windowID, windowID, portletName, region, position, id.getInternalID(), editionProperties);

        }
    }

    

    /**
     * publish sample
     */
    @ActionMapping(name = "publish")
    public void publish(ActionRequest request, ActionResponse response,   @ModelAttribute("status") EditionStatus status) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext);
        
        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();

                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof TestRepository) {
                    ((TestRepository) repository).publish(id.getInternalID());
                }

                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }
        
        
        refreshStatus(portalControllerContext, ctrl, status) ;

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

                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof TestRepository) {
                    String newID = "" + System.currentTimeMillis();

                    ((TestRepository) repository).addFolder(newID, newID, id.getInternalID());
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

                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof TestRepository) {
                    String newID = "" + System.currentTimeMillis();

                    ((TestRepository) repository).addDocument(newID, newID, id.getInternalID());
                }

                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }
    
    @ModelAttribute("status")
    public EditionStatus getStatus(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context 
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);
        CMSController ctrl = new CMSController(portalControllerContext); 

        // application form
        EditionStatus status = this.applicationContext.getBean(EditionStatus.class);

        refreshStatus( portalControllerContext, ctrl, status);

        return status;
    }


    protected void refreshStatus(PortalControllerContext portalControllerContext, CMSController ctrl ,EditionStatus status) throws PortletException {
        try {

            String contentId = WindowFactory.getWindow(portalControllerContext.getRequest()).getPageProperty("osivia.navigationId");
            if (contentId != null) {
                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = ctrl.getCMSContext();
                status.setPreview(cmsContext.isPreview());

                TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                status.setSupportPreview(repository.supportPreview());
                
                Map<String, String> locales = new HashMap<>();
                    for(Locale locale: repository.getLocales()) {
                    locales.put(locale.toString(), locale.getDisplayLanguage());
                }
                status.setLocales(locales);
                
                status.setPageEdition(repository.supportPageEdition());
                
                
                Personnalization personnalization = cmsService.getCMSSession(cmsContext).getPersonnalization( id);
                
                
                status.setSubtypes(personnalization.getSubTypes());
                status.setManageable(personnalization.isManageable());
                status.setModifiable(personnalization.isModifiable());
                
                status.setAcls(repository.getACL(id.getInternalID()));
                
                if( cmsContext.isPreview()) {
                    cmsContext.setPreview(false);
                    try {
                        cmsService.getCMSSession(cmsContext).getDocument( id);
                        status.setHavingPublication(true);
                    } catch (CMSException e) {
                        // Not found
                    }
                }
                
                status.setRemoteUser(portalControllerContext.getRequest().getRemoteUser());
                
                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
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
     * Get search filters form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public EditionForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        EditionForm form = this.applicationContext.getBean(EditionForm.class);
        try {
            form.setLocale( localService.getLocale(portalCtx));

            
        } catch (PortalException e) {
            throw new PortletException( e);
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

                    Document currentDoc = cmsService.getCMSSession(cmsContext).getDocument( id);
                    
                    localService.setLocale(portalCtx, form.getLocale());                    
                    
                    String url = portalUrlFactory.getViewContentUrl(portalCtx, currentDoc.getSpaceId());
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

                Document currentDoc = cmsService.getCMSSession(cmsContext).getDocument( id);
                
                String url = portalUrlFactory.getViewContentUrl(portalCtx, currentDoc.getSpaceId());
                response.sendRedirect(url);
            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        
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
