package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.osivia.portal.api.cms.model.Profile;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Space modifications controller
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW", params = "view=modifyProfile")
@SessionAttributes("profileForm")
public class ModifyProfileController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** Application context. */
    private ApplicationContext applicationContext;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(ModifyProfileController.class);

    /**
     * Constructor.
     */
    public ModifyProfileController() {
        super();
    }

    @Autowired
    private ModifyProfileValidator validator;

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
        return "modifyProfile";
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
     * Save portlet action
     * 
     * @throws IOException
     */
    @ActionMapping(name = "save")
    public void modifyPortlet(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("profileForm") ModifyProfileForm form, BindingResult result) throws PortletException, CMSException, IOException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

        if (!result.hasErrors()) {

            String callBackUrl = window.getProperty("callbackUrl");

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String profile = ow.writeValueAsString(form.getProfile());

            profile = URLEncoder.encode(profile, "UTF-8");

            callBackUrl += "&profile=" + profile;

            form.setCallBackUrl(callBackUrl);
        }

        response.setRenderParameter("view", "modifyProfile");
    }


    /**
     * Get form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @ModelAttribute("profileForm")
    public ModifyProfileForm getForm(PortletRequest request, PortletResponse response) throws PortletException, CMSException, JsonMappingException, JsonProcessingException {
        // Portal Controller context
        PortalControllerContext portalCtx = new PortalControllerContext(this.portletContext, request, response);


        ModifyProfileForm form = this.applicationContext.getBean(ModifyProfileForm.class);

        PortalWindow window = WindowFactory.getWindow(portalCtx.getRequest());

        String profile = window.getProperty("profile");
        if( profile != null)
            form.setProfile(new ObjectMapper().readValue(profile, Profile.class));
        else 
            form.setProfile(new Profile());


        return form;
    }

    @InitBinder("profileForm")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.addValidators(this.validator);
    }
}
