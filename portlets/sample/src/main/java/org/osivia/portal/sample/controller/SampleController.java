package org.osivia.portal.sample.controller;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

/**
 * Sample controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping(value = "VIEW")
public class SampleController implements PortletContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private IDynamicService dynamicWindowService;

    /** CMS service. */
    @Autowired
    private CMSService cmsService;

    /** Url factory service. */
    @Autowired
    IPortalUrlFactory portalUrlFactory;
    
    
    /**
     * Constructor.
     */
    public SampleController() {
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
    public String view1(RenderRequest request, RenderResponse response, @RequestParam(name = "count", defaultValue = "1") String count) throws Exception {
        request.setAttribute("count", count);
        PortalControllerContext portalCtx = new PortalControllerContext(portletContext, request, response);

        String foo = cmsService.getDocument(new CMSContext(portalCtx), new UniversalID("myspace","ID_DOC_1")).getTitle();
        request.setAttribute("foo", foo);
        
        Map<String, String> properties = new HashMap<String, String>();
        String startWindowUrl = portalUrlFactory.getStartPortletUrl(portalCtx, "SampleInstance", properties);
        request.setAttribute("startWindowCommand", startWindowUrl);
        
        return "view-1";
    }


    @RenderMapping(params = "tab=2")
    public String view2(RenderRequest request, RenderResponse response) {
        response.setTitle("Sample (tab2)");
        return "view-2";
    }


    @RenderMapping(params = "tab=3")
    public String view3() {
        return "view-3";
    }


    /**
     * Action mapping
     *
     * @param request action request
     * @param response action response
     * @param count count request parameter
     */
    @ActionMapping(name = "add")
    public void add(ActionRequest request, ActionResponse response, @RequestParam(name = "count") String count) {
        response.setRenderParameter("count", count);
    }



    @ActionMapping("throwException")
    public void throwException(ActionRequest request, ActionResponse response) {
        if (true)
            throw new NullPointerException();
    }


    @ActionMapping("startWindow")
    public void startWindow(ActionRequest request, ActionResponse response) throws PortalException {

        PortalControllerContext portalCtx = new PortalControllerContext(portletContext, request, response);
        Map<String, String> properties = new HashMap<String, String>();
        dynamicWindowService.startDynamicWindow(portalCtx, "col-2", "SampleInstance", properties);
    }

    @ActionMapping("goToPage")
    public void gotoPage(ActionRequest request, ActionResponse response) throws PortalException {
        // TODO : put in API
        request.setAttribute("osivia.pagePath", "/portalA/simple-ajax");
    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
