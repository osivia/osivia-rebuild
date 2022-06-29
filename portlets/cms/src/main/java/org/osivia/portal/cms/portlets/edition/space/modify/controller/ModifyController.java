package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
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
import javax.portlet.PortletURL;
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
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Space modifications controller
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
     * @throws IOException 
     * @throws PortletException 
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, @ModelAttribute("form") ModifyForm form) throws PortalException, PortletException, IOException {
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        request.setAttribute( "urls", getUrls(request, response, form));
        
        if (portalControllerContext.getHttpServletRequest().getAttribute("ajax") != null)
            return "ajax";

        return "view";
    }

    @ActionMapping(name = "addProfile")
    public void addProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String profile = request.getParameter("profile");

        form.getProfiles().add( new ObjectMapper().readValue(URLDecoder.decode(profile, "UTF-8"), Profile.class));
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }


    @ActionMapping(name = "modifyProfile")
    public void modifyProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String profile = request.getParameter("profile");
        int index = Integer.parseInt(request.getParameter("index"));
        form.getProfiles().remove(index);

        form.getProfiles().add(index, new ObjectMapper().readValue(URLDecoder.decode(profile, "UTF-8"), Profile.class));
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }

    
    @ActionMapping(name = "deleteProfile")
    public void deleteProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int index = Integer.parseInt(request.getParameter("index"));
        form.getProfiles().remove(index);

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
       }

    
    
    
    private Document getDocument(PortalControllerContext portalCtx) throws CMSException {

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");


        UniversalID id = new UniversalID(spaceId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        Document document = cmsService.getCMSSession(cmsContext).getDocument(id);

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
     * Get form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     * @throws IOException
     * @throws PortalException
     */
    @ModelAttribute("form")
    public ModifyForm getForm(PortletRequest request, PortletResponse response) throws PortletException, IOException, PortalException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        Space space = (Space) getDocument(portalCtx);

        space.getProfiles().add(new Profile("X", "XX", "XXX", "XXXX"));
        space.getProfiles().add(new Profile("Y", "YY", "YYY", "YYYY"));
        ModifyForm form = this.applicationContext.getBean(ModifyForm.class);

        form.setProfiles(new ArrayList<>(space.getProfiles()));


        return form;
    }



    public ModifyUrls getUrls(PortletRequest request, PortletResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, IOException, PortalException {
        
        
        ModifyUrls modifyUrls = new ModifyUrls();
        
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        List<String> urls = new ArrayList<>();

        int index = 0;
        for (Profile profile : form.getProfiles()) {

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String serProfile = ow.writeValueAsString(profile);


            Map<String, String> properties = new HashMap<>();
            properties.put("profile", serProfile);


            if (response instanceof RenderResponse) {
                PortletURL saveProfileUrl = ((RenderResponse) response).createActionURL();
                saveProfileUrl.setParameter(ActionRequest.ACTION_NAME, "modifyProfile");
                saveProfileUrl.setParameter("index", Integer.toString(index++));
                String callbackUrl = saveProfileUrl.toString();
                properties.put("callbackUrl", callbackUrl);
            }


            Map<String, String> params = new HashMap<>();
            params.put("view", "modifyProfile");

            String url = portalUrlFactory.getStartPortletUrl(portalCtx, "EditionModifySpaceInstance", properties, params, PortalUrlType.MODAL);

            urls.add(url);
        }
        
        modifyUrls.setModifyProfileUrls(urls);
        
        
        // add profile
        if (response instanceof RenderResponse) {
            
            Map<String, String> properties = new HashMap<>();
            
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String serProfile = ow.writeValueAsString(new Profile());
            properties.put("profile", serProfile);


            PortletURL saveProfileUrl = ((RenderResponse) response).createActionURL();
            saveProfileUrl.setParameter(ActionRequest.ACTION_NAME, "addProfile");

            String callbackUrl = saveProfileUrl.toString();
            properties.put("callbackUrl", callbackUrl);
            Map<String, String> params = new HashMap<>();
            params.put("view", "modifyProfile");

            String url = portalUrlFactory.getStartPortletUrl(portalCtx, "EditionModifySpaceInstance", properties, params, PortalUrlType.MODAL);
            modifyUrls.setAddProfileUrl(url);
            
            
            
            
            
        }        
        
        
        
        
        return modifyUrls;
    }


}
