package org.osivia.portal.cms.portlets.edition.tree.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
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
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Tree controller
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class TreeController extends GenericPortlet implements PortletContextAware, ApplicationContextAware {

    private static final String EOL = "#EOL#";


    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;


    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /** Application context. */
    private ApplicationContext applicationContext;

    @Autowired
    private IBundleFactory bundleFactory;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(TreeController.class);


    /**
     * Constructor.
     */
    public TreeController() {
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
    public String view(RenderRequest request, RenderResponse response) throws PortalException, PortletException, IOException {
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        String docId = window.getProperty("osivia.move.id");
        if (StringUtils.isNotEmpty(docId)) {
            return "move";
        } else {
            return "view";
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;

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


    /**
     * Get move form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return form
     * @throws PortletException
     * @throws CMSException
     */
    @ModelAttribute("form")
    public MoveForm getForm(PortletRequest request, PortletResponse response) throws PortletException, CMSException {

        MoveForm form = this.applicationContext.getBean(MoveForm.class);

        return form;

    }


    /**
     * Browse resource mapping.
     *
     * @param request resource request
     * @param response resource response
     */
    @ResourceMapping("browse")
    public void browse(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        String docId = window.getProperty("osivia.move.id");

        Boolean move;
        if (StringUtils.isNotEmpty(docId)) {
            move = true;
        } else {
            move = false;
        }

        // Browse
        String data;
        try {
            data = browse(portalControllerContext, move);
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


    private String browse(PortalControllerContext portalControllerContext, boolean move) throws CMSException, JSONException {
        // JSON array
        JSONArray jsonArray;
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
        
        if (move) {
            jsonArray = new JSONArray();
            for (NavigationItem child : getSpace(portalControllerContext).getChildren()) {
                JSONObject childObject = this.generateMoveObject(portalControllerContext, bundle, child);
                jsonArray.put(childObject);
            }
            
        } else {
            jsonArray = new JSONArray();
            

            JSONObject jsonObject = this.generateBrowseJSONObject(portalControllerContext, bundle, getSpace(portalControllerContext), true);
            if (jsonObject != null) {
                jsonArray.put(jsonObject);
            }
        }

        return jsonArray.toString();

    }


    private JSONObject generateBrowseJSONObject(PortalControllerContext portalControllerContext, Bundle bundle, NavigationItem navItem, boolean root) throws JSONException, CMSException {
        JSONObject object = new JSONObject();


        if (root) {
            object.put("expanded", "true");
        }
        object.put("title", navItem.getTitle());
        object.put("href", portalUrlFactory.getViewContentUrl(portalControllerContext, navItem.getDocumentId()));
        object.put("path", navItem.getDocumentId().toString());

        // Children
        if (!navItem.getChildren().isEmpty()) {
            JSONArray childrenArray = new JSONArray();
            for (NavigationItem child : navItem.getChildren()) {
                JSONObject childObject = this.generateBrowseJSONObject(portalControllerContext, bundle, child, false);
                childrenArray.put(childObject);
            }

            object.put("children", childrenArray);
        }

        return object;
    }

    private JSONObject generateMoveObject(PortalControllerContext portalControllerContext, Bundle bundle, NavigationItem navItem) throws JSONException, CMSException {
        JSONObject object = new JSONObject();


        object.put("title", navItem.getTitle());
        object.put("path", navItem.getDocumentId().toString());

        // Children
        
        JSONArray childrenArray = new JSONArray();
        
        if (!navItem.getChildren().isEmpty()) {
            
            for (NavigationItem child : navItem.getChildren()) {
                JSONObject childObject = this.generateMoveObject(portalControllerContext, bundle, child);
                childrenArray.put(childObject);
            }



        }

        JSONObject lastLine = new JSONObject();
        lastLine.put("title", bundle.getString("MODIFY_MOVE_END_OF_LIST"));
        lastLine.put("path", navItem.getDocumentId().toString() + EOL);
        childrenArray.put(lastLine);


        object.put("children", childrenArray);
        
        return object;
    }


    @ActionMapping("move")
    public void move(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") MoveForm form, BindingResult result, SessionStatus sessionStatus) throws PortletException, PortalException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        if (StringUtils.isEmpty(form.getTarget())) {
            ObjectError error = new FieldError("target", "target", bundle.getString("MODIFY_MOVE_MANDATORY_TARGET"));
            result.addError(error);
        }

        if (StringUtils.isEmpty(form.getTarget())) {
            ObjectError error = new FieldError("target", "target", bundle.getString("MODIFY_MOVE_MANDATORY_TARGET"));
            result.addError(error);
        }


        if (!result.hasErrors()) {
            sessionStatus.setComplete();


            // New document title
            UniversalID srcId = new UniversalID(WindowFactory.getWindow(request).getProperty("osivia.move.id"));

            boolean eol = false;
            String target = form.getTarget();
            if (target.endsWith(EOL)) {
                target = target.substring(0, target.length() - EOL.length());
                eol = true;
            }

            UniversalID targetId = new UniversalID(target);

            CMSController ctrl = new CMSController(portalControllerContext);

            AdvancedRepository repository = TestRepositoryLocator.getTemplateRepository(ctrl.getCMSContext(), srcId.getRepositoryName());
            if (repository instanceof AdvancedRepository) {

                ((AdvancedRepository) repository).moveDocument(srcId.getInternalID(), targetId.getInternalID(), eol);
            }

            // Nuxeo document link
            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, ctrl.getCMSContext(), srcId);

            response.sendRedirect(url);

        }
    }


}
