package org.osivia.portal.cms.portlets.rename.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang3.StringUtils;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class RenameController implements PortletContextAware {

    /** Portlet context. */
    private PortletContext portletContext;

    @Autowired
    private ApplicationContext applicationContext;

    /** CMS service. */
    @Autowired
    private CMSService cmsService;
    
    @Autowired
    private RenameFormValidator validator;

    @Autowired
    private IPortalUrlFactory portalUrlFactory;   
    
    /**
     * Constructor.
     */
    public RenameController() {
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
        PortalControllerContext portalCtx = new PortalControllerContext(portletContext, request, response);
        CMSController ctrl = new CMSController(portalCtx);

        String contentId = WindowFactory.getWindow(request).getProperty("osivia.rename.id");
        if( contentId != null)  {
            UniversalID id = new UniversalID(contentId);
            Document doc = cmsService.getCMSSession(ctrl.getCMSContext()).getDocument( id);
        
            request.setAttribute("document", doc);
        }
        
        
        return "view";
    }




    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

    
    
    
    
    /**
     * Get rename form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     * @throws PortletException
     * @throws CMSException 
     */
    @ModelAttribute("form")
    public RenameForm getForm(PortletRequest request, PortletResponse response) throws PortletException, CMSException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Current document
        String contentId = WindowFactory.getWindow(request).getProperty("osivia.rename.id");
        CMSController ctrl = new CMSController(portalControllerContext);
        Document document;

        document = cmsService.getCMSSession(ctrl.getCMSContext()).getDocument( new UniversalID(contentId));


        // Rename form
        RenameForm form = this.applicationContext.getBean(RenameForm.class);


        // Document DTO
        form.setTitle(document.getTitle());

        return form;

    }

    
    /**
     * Save action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param form          form model attribute
     * @param result        binding result
     * @param sessionStatus session status
     * @throws PortletException
     * @throws PortalException 
     * @throws IOException
     */
    @ActionMapping("save")
    public void save(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") RenameForm form, BindingResult result,
                     SessionStatus sessionStatus) throws PortletException, PortalException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (!result.hasErrors()) {
            sessionStatus.setComplete();

            // New document title
            String newTitle = StringUtils.trim(form.getTitle());
            UniversalID contentId = new UniversalID(WindowFactory.getWindow(request).getProperty("osivia.rename.id"));
            
            
            CMSController ctrl = new CMSController(portalControllerContext); 

            AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(ctrl.getCMSContext(), contentId.getRepositoryName());
            if( repository instanceof AdvancedRepository) {

                ((AdvancedRepository) repository).renameDocument(contentId.getInternalID(), newTitle);
            }

            // Nuxeo document link
            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, ctrl.getCMSContext(), contentId);

            response.sendRedirect(url);
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
