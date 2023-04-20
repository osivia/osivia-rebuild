package org.osivia.portal.cms.portlets.edition.repository.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.StreamableRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.error.Debug;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;


/**
 * Repository edition controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class RepositoryController extends GenericPortlet implements PortletContextAware, ApplicationContextAware   {

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
    INotificationsService notificationService;
    
    /** The logger. */
    protected static Log logger = LogFactory.getLog(RepositoryController.class);

    /**
     * Constructor.
     */
    public RepositoryController() {
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
     * Upload file action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "submit", params = "upload-file")
    public void uploadFile(ActionRequest request, ActionResponse response, @ModelAttribute("form") RepositoryForm form,  @RequestParam("repositoryName") String repositoryName,  SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        // Temporary file

        MultipartFile upload = (MultipartFile) form.getFileUpload().get(repositoryName);
        try {
           
            // Portal controller context
            CMSController ctrl = new CMSController(portalCtx);

            CMSContext cmsContext = ctrl.getCMSContext();
            cmsContext.setSuperUserMode(true);
            
            
            StreamableRepository repository = (StreamableRepository) cmsService.getUserRepository( cmsContext, repositoryName);
            repository.readFrom(upload.getInputStream());
            
            Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());  
            String message = bundle.getString("MODIFY_REPOSITORY_IMPORT_SUCCESS");        
            notificationService.addSimpleNotification(portalCtx, message, NotificationsType.SUCCESS);


        } catch (Exception e) {
            
            logger.error(Debug.stackTraceToString(  e ));
            
            
            Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());  
            String message = bundle.getString("MODIFY_REPOSITORY_IMPORT_ERROR",e.getMessage());        
            notificationService.addSimpleNotification(portalCtx, message, NotificationsType.ERROR);
             
        }

    }

     
    
     
    /**
     * Load page resource mapping.
     *
     * @param request   resource request
     * @param response  resource response
     * @param pageIndex page index request parameter
     */
    @ResourceMapping("export")
    public void export(ResourceRequest request, ResourceResponse response, @RequestParam("repositoryName") String repositoryName) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        // Content type
        response.setContentType("text/html");
        response.setContentType("application/json;charset=UTF-8");
        
        response.addProperty("Content-Disposition", "attachment; filename=\""+repositoryName+"-"+new SimpleDateFormat("yy-MM-dd-hh-mm").format(new Date())+".json"+"\"");
        

        // Portal controller context
        CMSController ctrl = new CMSController(portalCtx);

        CMSContext cmsContext = ctrl.getCMSContext();
        cmsContext.setSuperUserMode(true);
        
        try {
            StreamableRepository repository = (StreamableRepository) cmsService.getUserRepository( cmsContext, repositoryName);
            repository.saveTo(response.getPortletOutputStream());
        } catch (CMSException e) {
            throw new PortletException(e);
        }
    }


    private List<RepositoryBean> getRepositories(PortalControllerContext portalControllerContext) throws PortalException {
        
        // Portal controller context
        CMSController ctrl = new CMSController(portalControllerContext);

        CMSContext cmsContext = ctrl.getCMSContext();
        cmsContext.setSuperUserMode(true);
        
        List<RepositoryBean> formRepositories = new ArrayList<>();
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        List<NativeRepository> repositories = cmsService.getUserRepositories(cmsContext);
        for(NativeRepository repository: repositories)  {
            if( repository instanceof BaseUserRepository)   {
                RepositoryBean repositoryBean = new RepositoryBean();

                String repositoryName = ((BaseUserRepository) repository).getRepositoryName();
           		boolean repositoryManager = cmsService.getCMSSession(new CMSContext(portalControllerContext)).isManager( repositoryName);
           		
           		if( repositoryManager)	{
	                repositoryBean.setName( repositoryName);
	                repositoryBean.setStreamable(repository instanceof StreamableRepository);
	                
	                if( repository instanceof StreamableRepository)    {
	                    repositoryBean.setVersion(((StreamableRepository) repository).getVersion());
	                }
	                else
	                    repositoryBean.setVersion(null);
	                
	                Map<String, String> properties = new HashMap<>();
	                String title = bundle.getString("MODIFY_REPOSITORY_MERGE_LABEL");
	                properties.put("osivia.repository.name", repositoryName);

	                String manageRepositoriesUrl = portalUrlFactory.getStartPortletInNewPage(portalControllerContext, "EditionRepositoryMergePortletInstance", title, "EditionRepositoryMergePortletInstance", properties,
	                        new HashMap<>());
	                
	                repositoryBean.setMergeUrl(manageRepositoriesUrl);
	                formRepositories.add(repositoryBean);
           		}
            }
        }
         
        return formRepositories;
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
     * Repository form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public RepositoryForm getForm(PortletRequest request, PortletResponse response) throws PortletException {

        try {

            // Portal Controller context
            PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
            
            RepositoryForm form = this.applicationContext.getBean(RepositoryForm.class);
            

            return form;

        } catch ( Exception e) {
            throw new PortletException(e);
        }
    }
    
    /**
     * Repository index model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("repositoryIndex")
    public RepositoryIndex getIndex(PortletRequest request, PortletResponse response) throws PortletException {

        try {

            // Portal Controller context
            PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
            
            RepositoryIndex index = this.applicationContext.getBean(RepositoryIndex.class);
            
            index.setRepositories(getRepositories(portalCtx));

            return index;

        } catch ( Exception e) {
            throw new PortletException(e);
        }
    }


}
