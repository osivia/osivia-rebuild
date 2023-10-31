package org.osivia.portal.sample.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.refresh.IRefreshService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.WindowFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

/**
 * Sample controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping(value = "VIEW")
public class SampleController implements PortletContextAware {
    
    protected static final Log logger = LogFactory.getLog(SampleController.class);


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
    
    @Autowired   
    IRefreshService refreshService;
    
    
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
/*
        UniversalID docId = new UniversalID("myspace","ID_DOC_1");
        UniversalID pageId = new UniversalID("templates","ID_PAGE_A");        
        

        
        String foo = cmsService.getCMSSession(new CMSContext(portalCtx)).getDocument( docId).getTitle();
        request.setAttribute("foo", foo);
        */
        
        Map<String, String> properties = new HashMap<String, String>();
        String startWindowUrl = portalUrlFactory.getStartPortletUrl(portalCtx, "SampleInstance", properties, PortalUrlType.DEFAULT);
        request.setAttribute("startWindowCommand", startWindowUrl);
        
        /*
        request.setAttribute("docURL", portalUrlFactory.getViewContentUrl(portalCtx, new CMSContext(portalCtx),docId));
        
        request.setAttribute("pageURL",portalUrlFactory.getViewContentUrl(portalCtx, new CMSContext(portalCtx),pageId));
        */
        
        String openInPopupUrl = portalUrlFactory.getStartPortletUrl(portalCtx, "SampleInstance", properties, PortalUrlType.MODAL);
        request.setAttribute("openInPopupUrl", openInPopupUrl);
        
        request.setAttribute("user", request.getRemoteUser());
        
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

    @RenderMapping(params = "tab=5")
    public String view5() {
        return "react";
    }
    
    @RenderMapping(params = "tab=6")
    public String resource() {
        return "resource";
    }
    
    
    /**
     * copy file to response
     *
     */
    @ResourceMapping("export")
    public void export(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

        
        if(true)
            throw new PortletException("error test");
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
        logger.info("SamplePortlet Add");
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

    @ActionMapping("refresh")
    public void refresh(ActionRequest request, ActionResponse response) throws PortalException {

        PortalControllerContext portalCtx = new PortalControllerContext(portletContext, request, response);
        refreshService.applyRefreshStrategy(portalCtx, IRefreshService.REFRESH_STRATEGY_SAFRAN);    }
    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
