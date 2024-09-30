package org.osivia.portal.cms.portlets.edition.repository.merge.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONArray;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONObject;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.MergeException;
import org.osivia.portal.api.cms.service.MergeParameters;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.StreamableCheckResult;
import org.osivia.portal.api.cms.service.StreamableCheckResults;
import org.osivia.portal.api.cms.service.StreamableRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.error.Debug;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.edition.repository.admin.controller.CheckedItems;
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

    private static final String SELECT_ELEMENTS_STEP = "select-elements";
    private static final String DOWNLOAD_STEP = "download";
    private static final String CHOOSE_FILE_STEP = "choose-file";
    private static final String VALIDATION_FILE_STEP = "validation-file";

    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;

    /** Application context. */
    private ApplicationContext applicationContext;


    

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
    public String view(RenderRequest request, RenderResponse response, @ModelAttribute("form") RepositoryForm form) throws PortalException {
        
        String step =  request.getParameter("step");

        if (StringUtils.isEmpty(step))
            step =  CHOOSE_FILE_STEP;
        
        // Lost of session
        if( form.getFileToMerge() == null) {
            step =  CHOOSE_FILE_STEP;
        }

        
        
        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());  
        
        if( CHOOSE_FILE_STEP.equals(step)) {
           
            String part2 = getHelpMessagePart2(bundle);
            request.setAttribute("helpMessage", bundle.getString("MODIFY_REPOSITORY_MERGE_CHOOSE_FILE_HELP", part2));
        }
        

        if( DOWNLOAD_STEP.equals(step)) {
            
            String part2 = getHelpMessagePart2(bundle);
            request.setAttribute("helpMessage", bundle.getString("MODIFY_REPOSITORY_MERGE_DOWNLOAD_HELP", part2));
           
        }
        
        
     
          
        return step;
    }

    private String getHelpMessagePart2(Bundle bundle) {
        String helpUrl = System.getProperty("administration.forge.url");
        String part2= bundle.getString("MODIFY_REPOSITORY_MERGE_GIT_HELP");
        if( StringUtils.isNotEmpty(helpUrl))    {
            part2= "<a href=\""+helpUrl+"\" target=\"_blank\">"+part2+"</a>";
        }
        return part2;
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
    public void uploadFile(ActionRequest request, ActionResponse response, @ModelAttribute("form") RepositoryForm form,   SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        // Temporary file
        MultipartFile upload = (MultipartFile) form.getFileUpload();
        
        File f = File.createTempFile("merge", ".json");
        
        FileInputStream fis = null;
        
        try {
           
            upload.transferTo(f);
            form.setFileToMerge(f);
            
           StreamableCheckResults results = null;
            
           CheckedItems checkedItems = new CheckedItems();
           
           // Portal controller context
           CMSController ctrl = new CMSController(portalCtx);

           CMSContext cmsContext = ctrl.getCMSContext();
           cmsContext.setSuperUserMode(true);
                    
           StreamableRepository repository = (StreamableRepository) cmsService.getUserRepository( cmsContext, WindowFactory.getWindow(request).getProperty("osivia.repository.name"));
           fis = new FileInputStream(f);
 
           // Call the check service
           results = repository.checkInputFile(fis);
            
           boolean ok = true;

           for ( StreamableCheckResult item : results.getItems()) {
                if( item.isOk() == false)   {
                    ok = false;
                }
           }
                    
           checkedItems.setOk(ok);
           checkedItems.setResults(results);
           form.setCheckedItems(checkedItems);
            
           response.setRenderParameter("step", VALIDATION_FILE_STEP);

        } catch (Exception e) {
            
            logger.error(Debug.stackTraceToString(  e ));
            
            
            Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());  
            String message = bundle.getString("MODIFY_REPOSITORY_IMPORT_ERROR",e.getMessage());        
            notificationService.addSimpleNotification(portalCtx, message, NotificationsType.ERROR);
             
        } 
        
        finally   {
            if( fis != null)    {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new PortletException(e);
                }
            }
        }
        

    }
    
    
    
    @ActionMapping(name = "confirm-validation")
    public void confirmValidation(ActionRequest request, ActionResponse response, @ModelAttribute("form") RepositoryForm form,   SessionStatus session)
            throws PortletException, IOException {    
    
        response.setRenderParameter("step", SELECT_ELEMENTS_STEP);
    }
    
    @ActionMapping(name = "cancel-validation")
    public void cancelValidation(ActionRequest request, ActionResponse response, @ModelAttribute("form") RepositoryForm form,   SessionStatus session)
            throws PortletException, IOException {    
    
        response.setRenderParameter("step", CHOOSE_FILE_STEP);
    }
    
    
    
    @ActionMapping(name = "select")
    public void select(ActionRequest request, ActionResponse response, @ModelAttribute("form") RepositoryForm form,   SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        
        // Lost of session
        if( form.getFileToMerge() == null) {
            response.setRenderParameter("step", CHOOSE_FILE_STEP);
            return;
        }
        

        
        File f = File.createTempFile("merge", ".json");
        FileOutputStream fileToMergeOutput = new FileOutputStream(f);
        FileInputStream fileToMergeInput = new FileInputStream(form.getFileToMerge());
        
        try {
           
            // Portal controller context
            CMSController ctrl = new CMSController(portalCtx);

            CMSContext cmsContext = ctrl.getCMSContext();
            cmsContext.setSuperUserMode(true);
            
            
            String repositoryName = WindowFactory.getWindow(request).getProperty("osivia.repository.name");
            
            MergeParameters mergeParams = new MergeParameters();
            mergeParams.setPagesId(new ArrayList<>(form.getMergedPages()));
            mergeParams.setMergeProfiles(new ArrayList<>(form.getMergedProfiles()));
            
            
            StreamableRepository repository = (StreamableRepository) cmsService.getUserRepository( cmsContext, repositoryName);
            repository.merge(fileToMergeInput,  mergeParams, fileToMergeOutput);
            
            form.setFileDownload(f);
            
            response.setRenderParameter("step", DOWNLOAD_STEP);
            
            

        } catch (MergeException e) {
            
            Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());  
            String message = bundle.getString("MODIFY_REPOSITORY_MERGE_ERROR",e.getMessage());        
            notificationService.addSimpleNotification(portalCtx, message, NotificationsType.ERROR);
            
            response.setRenderParameter("step", SELECT_ELEMENTS_STEP);
             
        } catch(Exception e)    {
            throw new PortletException(e);
        }
        
        
        finally   {
            fileToMergeInput.close();
            fileToMergeOutput.close();
        }

    }

    
    /**
     * get Current space
     * 
     * @param portalCtx
     * @return
     * @throws CMSException
     */
    private NavigationItem getSpace(PortalControllerContext portalCtx) throws CMSException {
        String repositoryName = WindowFactory.getWindow(portalCtx.getRequest()).getProperty("osivia.repository.name");


        UniversalID id = new UniversalID(repositoryName, "DEFAULT");
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        NavigationItem document = cmsService.getCMSSession(cmsContext).getNavigationItem(id);

        return document;
    }    
     
    
    /**
     * get Current space
     * 
     * @param portalCtx
     * @return
     * @throws CMSException
     */
    private Space getSpaceDocument(PortalControllerContext portalCtx) throws CMSException {

        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();

        Space space = (Space) cmsService.getCMSSession(cmsContext).getDocument(getSpace(portalCtx).getDocumentId());

        return space;
    } 
    
     
    /**
     * copy file to response
     *
     */
    @ResourceMapping("export")
    public void export(ResourceRequest request, ResourceResponse response, @ModelAttribute("form") RepositoryForm form) throws PortletException, IOException {

        try {
            response.setContentType("text/html");
            response.setContentType("application/json;charset=UTF-8");

            String repositoryName = WindowFactory.getWindow(request).getProperty("osivia.repository.name");

            response.addProperty("Content-Disposition",
                    "attachment; filename=\"" + repositoryName + "-merge-" + new SimpleDateFormat("yy-MM-dd-hh-mm").format(new Date()) + ".json" + "\"");

            File f = form.getFileDownload();
            FileInputStream in = new FileInputStream(f);

            IOUtils.copy(in, response.getPortletOutputStream());
        } catch (Exception e) {
            PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
            portalCtx.getHttpServletRequest().setAttribute("osivia.no_redirection", "0");
            throw new RuntimeException(e);
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

    
    private void browse(PortalControllerContext portalControllerContext, NavigationItem navItem, Map<String, String> pages) throws CMSException {
        
        pages.put(navItem.getDocumentId().getInternalID(), navItem.getTitle());
        
        // Children
        if (!navItem.getChildren().isEmpty()) {
              for (NavigationItem child : navItem.getChildren()) {
                
                this.browse(portalControllerContext, child, pages);
             }

        }
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

            RepositoryForm form = this.applicationContext.getBean(RepositoryForm.class);
            
            return form;
       } catch ( Exception e) {
            throw new PortletException(e);
        }
    }

    private List<String> getSpaceProfiles(PortalControllerContext portalCtx) throws CMSException {
        Space space = getSpaceDocument(portalCtx);
        List<String> profiles = new ArrayList<String>();
        for(Profile profile: space.getProfiles())   {
            profiles.add(profile.getName());
        }
        return profiles;
    }
    
    
    @ModelAttribute("profiles")
    public List<String> getProfiles(PortletRequest request, PortletResponse response) throws PortletException {

        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        
        try {
           
            return getSpaceProfiles(portalCtx);

        } catch ( Exception e) {
            throw new PortletException(e);
        }
    }

    
    @ModelAttribute("pages")
    public Map<String, String> getPages(PortletRequest request, PortletResponse response) throws PortletException {

        // Portal controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
        
        try {

            NavigationItem space = getSpace(portalCtx);
            
            Map<String, String> pages = new HashMap<>();
            
            browse(portalCtx,space, pages);
            
            // Sort by title
            List<Entry<String, String>> list = new ArrayList<>(pages.entrySet());
            list.sort(Entry.comparingByValue( new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.toUpperCase().compareTo(o2.toUpperCase());
                }
            }));

            Map<String, String> result = new LinkedHashMap<String, String>();
            for (Entry<String, String> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            
            
            return result;

        } catch ( Exception e) {
            throw new PortletException(e);
        }
    }

}
