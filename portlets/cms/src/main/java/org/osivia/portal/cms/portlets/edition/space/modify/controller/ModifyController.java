package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
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
    private IBundleFactory bundleFactory;

    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /** Application context. */
    private ApplicationContext applicationContext;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(ModifyController.class);


    private static final String GROUP_PREFIX = "group:";
    
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
        
        String tab = request.getParameter("tab");
        if( StringUtils.isNotEmpty(tab))    {
            request.setAttribute("tab", request.getParameter("tab"));
            reloadForm(request, response, form);
        }

        request.setAttribute( "urls", getUrls(request, response, form));

        if (portalControllerContext.getHttpServletRequest().getAttribute("ajax") != null)   {
            if(BooleanUtils.isNotTrue((Boolean)portalControllerContext.getHttpServletRequest().getAttribute("osivia.portal.refreshPage")))
                return "ajax";
        }

        return "view";
    }
    
    
    
    private void checkPageProfiles(PortalControllerContext portalControllerContext, AdvancedRepository userRepository, NavigationItem navItem, String role, List<NavigationItem> errorPages) throws JSONException, CMSException {
        List<String> acls =  userRepository.getACL(navItem.getDocumentId().getInternalID());
        if( acls.contains( GROUP_PREFIX + role)) {
            errorPages.add(navItem);
        }
        
        // Children
        if (!navItem.getChildren().isEmpty()) {
            for (NavigationItem child : navItem.getChildren()) {
                this.checkPageProfiles(portalControllerContext, userRepository, child, role, errorPages);

            }
        }
    }
    
    @ResourceMapping("checkProfile")
    public void checkProfile(ResourceRequest request, ResourceResponse response, @ModelAttribute("form") ModifyForm form,
            @RequestParam(value = "index", required = true) String index) throws PortletException {
        
        
        int profileIndex = Integer.parseInt(index);
        StringBuffer sbResponse = new StringBuffer();

        try {

                PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);
                Bundle bundle = this.bundleFactory.getBundle(portalCtx.getRequest().getLocale());
                NavigationItem space = (NavigationItem) getSpace(portalCtx);
                NativeRepository userRepository = getRepository(portalCtx);
                

                String ldapRole = form.getProfiles().get(profileIndex).getRole();
                
                               
                List<NavigationItem> errorPages = new ArrayList<>();
                checkPageProfiles(portalCtx, (AdvancedRepository) userRepository, space, ldapRole,errorPages);
                
                if(errorPages.size() > 0)   {
                    sbResponse.append("<p>"+bundle.getString("MODIFY_SPACE_CONFIRM_DELETE_USED_PROFILE") + "</p>");
                    sbResponse.append("<div class=\"m-3\">");
                    for(int i=0; i< errorPages.size(); i++ ) {
                        sbResponse.append("<div class=\"row\">");
                        sbResponse.append("<span class=\"col-6 text-truncate\">"+errorPages.get(i).getTitle()+"</span>");
                        sbResponse.append("<span class=\"col-6\">["+errorPages.get(i).getDocumentId().getInternalID()+"]</span>");
                        sbResponse.append("</div>");
                    }
                    sbResponse.append("</div>");
                }   else    {
                    sbResponse.append("OK");
                }
 


            try {
                response.getPortletOutputStream().write(sbResponse.toString().getBytes());
            } finally {
                response.getPortletOutputStream().close();
            }
        } catch (Exception e) {
            if (!(e instanceof PortletException)) {
                throw new PortletException(e);
            } else {
                throw ((PortletException) e);
            }

        }

    }

    @ActionMapping(name = "addProfile")
    public void addProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String profile = request.getParameter("profile");

        form.getProfiles().add( new ObjectMapper().readValue(URLDecoder.decode(profile, "UTF-8"), Profile.class));
        
        saveProfiles(portalControllerContext,form);
        response.setRenderParameter("tab", "profiles");
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }

    
    @ActionMapping(name = "addStyle")
    public void addStyle(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String profile = request.getParameter("style");

        form.getStyles().add( new ObjectMapper().readValue(URLDecoder.decode(profile, "UTF-8"), String.class));
        
        saveStyles(portalControllerContext,form);
        response.setRenderParameter("tab", "styles");
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }
    

    @ActionMapping(name = "modifyProfile")
    public void modifyProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String profile = request.getParameter("profile");
        int index = Integer.parseInt(request.getParameter("index"));
        form.getProfiles().remove(index);

        form.getProfiles().add(index, new ObjectMapper().readValue(URLDecoder.decode(profile, "UTF-8"), Profile.class));
        
        saveProfiles(portalControllerContext,form);
        response.setRenderParameter("tab", "profiles");
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }
    
    
    @ActionMapping(name = "modifyStyle")
    public void modifyStyle(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        String style = request.getParameter("style");
        int index = Integer.parseInt(request.getParameter("index"));
        form.getStyles().remove(index);

        form.getStyles().add(index, new ObjectMapper().readValue(URLDecoder.decode(style, "UTF-8"), String.class));
        
        
        saveStyles(portalControllerContext,form);
        response.setRenderParameter("tab", "styles");
 
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
    }

    
    @ActionMapping(name = "deleteProfile")
    public void deleteProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int index = Integer.parseInt(request.getParameter("index"));
        form.getProfiles().remove(index);
        
        saveProfiles(portalControllerContext,form);
        response.setRenderParameter("tab", "profiles");

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
       }
    
    @ActionMapping(name = "deleteStyle")
    public void deleteStyle(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int index = Integer.parseInt(request.getParameter("index"));
        form.getStyles().remove(index);
        
        saveStyles(portalControllerContext,form);
        response.setRenderParameter("tab", "styles");

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
       }
    

    @ActionMapping(name = "moveProfile")
    public void moveProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int srcIndex = Integer.parseInt(request.getParameter("srcIndex"));
        int destIndex = Integer.parseInt(request.getParameter("destIndex"));

        
        Profile profile = form.getProfiles().get(srcIndex);
        form.getProfiles().remove(srcIndex);
        
        form.getProfiles().add(destIndex, profile);

        saveProfiles(portalControllerContext,form);
        response.setRenderParameter("tab", "profiles");

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
       }
    
    @ActionMapping(name = "moveStyle")
    public void moveStyle(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int srcIndex = Integer.parseInt(request.getParameter("srcIndex"));
        int destIndex = Integer.parseInt(request.getParameter("destIndex"));
        
        String style  = form.getStyles().get(srcIndex);
        form.getStyles().remove(srcIndex);
        

        form.getStyles().add(destIndex, style);

        saveStyles(portalControllerContext,form);
        response.setRenderParameter("tab", "styles");

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);
       }    
   
    @ActionMapping(name = "modifyPortal", params = "formAction=sortProfile")
    public void sortProfile(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form, @RequestParam String sortSrc,  @RequestParam String sortTarget) throws PortletException, CMSException, IOException {
        
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int src = Integer.parseInt(sortSrc);
        
        Profile srcprofile = form.getProfiles().get(src);
        form.getProfiles().remove(src);
        
        
        int target = Integer.parseInt(sortTarget);
        
        form.getProfiles().add(target,srcprofile);
        
        saveProfiles(portalControllerContext,form);
        response.setRenderParameter("tab", "profiles");
        

        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);        
   }
    
    
    @ActionMapping(name = "modifyPortal", params = "formAction=sortStyle")
    public void sortStyle(ActionRequest request, ActionResponse response, @ModelAttribute("form") ModifyForm form, @RequestParam String sortSrc,  @RequestParam String sortTarget) throws PortletException, CMSException, IOException {
        
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        int src = Integer.parseInt(sortSrc);
        
        String srcStyle = form.getStyles().get(src);
        form.getStyles().remove(src);
        
        
        int target = Integer.parseInt(sortTarget);
        
        
        form.getStyles().add(target,srcStyle);
        saveStyles(portalControllerContext,form);
        
        response.setRenderParameter("tab", "styles");
        
        portalControllerContext.getHttpServletRequest().setAttribute("ajax", Boolean.TRUE);        
   }
    
    private void saveProfiles( PortalControllerContext portalCtx, ModifyForm form) throws CMSException, PortletException {
        CMSController ctrl = new CMSController(portalCtx); 
        
        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");
        UniversalID id = new UniversalID(spaceId);

        AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(ctrl.getCMSContext(), id.getRepositoryName());

        if( repository instanceof AdvancedRepository) {
            ((AdvancedRepository) repository).setProfiles(id.getInternalID(), form.getProfiles());
        }
        
        try {
            reloadForm(portalCtx.getRequest(), portalCtx.getResponse(), form);
        } catch (Exception e) {
           throw new PortletException( e);
           
        }
        
    }
    
    private void saveStyles( PortalControllerContext portalCtx, ModifyForm form) throws CMSException, PortletException {
        CMSController ctrl = new CMSController(portalCtx); 
        
        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");
        UniversalID id = new UniversalID(spaceId);

        AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(ctrl.getCMSContext(), id.getRepositoryName());

        if( repository instanceof AdvancedRepository) {
            ((AdvancedRepository) repository).setStyles(id.getInternalID(), form.getStyles());
        }
        
        try {
            reloadForm(portalCtx.getRequest(), portalCtx.getResponse(), form);
        } catch (Exception e) {
           throw new PortletException( e);
           
        }
        
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

    private NavigationItem getSpace(PortalControllerContext portalCtx) throws CMSException {

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");


        UniversalID id = new UniversalID(spaceId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();

        NavigationItem space = cmsService.getCMSSession(cmsContext).getNavigationItem(id);

        return space;
    }

    
    private NativeRepository  getRepository(PortalControllerContext portalCtx) throws CMSException {

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());
        String spaceId = window.getProperty("osivia.space.id");


        UniversalID id = new UniversalID(spaceId);
        CMSController ctrl = new CMSController(portalCtx);
        CMSContext cmsContext = ctrl.getCMSContext();
        
        NativeRepository userRepository = cmsService.getUserRepository(cmsContext, id.getRepositoryName());

        return userRepository;
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

        ModifyForm form = this.applicationContext.getBean(ModifyForm.class);

        form.setProfiles(new ArrayList<>(space.getProfiles()));
        form.setStyles(new ArrayList<>(space.getStyles()));


        return form;
    }
    
    
    public void reloadForm(PortletRequest request, PortletResponse response,  ModifyForm form) throws PortletException, IOException, PortalException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        Space space = (Space) getDocument(portalCtx);

        
        form.setProfiles(new ArrayList<>(space.getProfiles()));
        form.setStyles(new ArrayList<>(space.getStyles()));

    }


    public ModifyUrls getUrls(PortletRequest request, PortletResponse response, @ModelAttribute("form") ModifyForm form) throws PortletException, IOException, PortalException {
        
        
        ModifyUrls modifyUrls = new ModifyUrls();
        
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);

        List<String> urls = new ArrayList<>();

        int index = 0;
        
        /* Get Profiles urls */
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
        
        /* Get Styles Urls */
        urls = new ArrayList<>();        
        index = 0;
        for (String style : form.getStyles()) {

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String serStyle = ow.writeValueAsString(style);


            Map<String, String> properties = new HashMap<>();
            properties.put("style", serStyle);


            if (response instanceof RenderResponse) {
                PortletURL saveStyleUrl = ((RenderResponse) response).createActionURL();
                saveStyleUrl.setParameter(ActionRequest.ACTION_NAME, "modifyStyle");
                saveStyleUrl.setParameter("index", Integer.toString(index++));
                String callbackUrl = saveStyleUrl.toString();
                properties.put("callbackUrl", callbackUrl);
            }


            Map<String, String> params = new HashMap<>();
            params.put("view", "modifyStyle");

            String url = portalUrlFactory.getStartPortletUrl(portalCtx, "EditionModifySpaceInstance", properties, params, PortalUrlType.MODAL);

            urls.add(url);
        }
        
        modifyUrls.setModifyStylesUrls(urls);
        
        
        
        // add profile
        if (response instanceof RenderResponse) {
            
            Map<String, String> properties = new HashMap<>();
            
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String serStyle = ow.writeValueAsString(new String());
            properties.put("style", serStyle);


            PortletURL saveStyleUrl = ((RenderResponse) response).createActionURL();
            saveStyleUrl.setParameter(ActionRequest.ACTION_NAME, "addStyle");

            String callbackUrl = saveStyleUrl.toString();
            properties.put("callbackUrl", callbackUrl);
            Map<String, String> params = new HashMap<>();
            params.put("view", "modifyStyle");

            String url = portalUrlFactory.getStartPortletUrl(portalCtx, "EditionModifySpaceInstance", properties, params, PortalUrlType.MODAL);
            modifyUrls.setAddStyleUrl(url);
          }        
        
        
        
        
        return modifyUrls;
    }


}
