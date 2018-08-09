package org.osivia.portal.sample.controller;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osivia.portal.api.cms.service.CMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Sample controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping(value = "VIEW")
public class SampleController {

    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;


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
     */
    @RenderMapping
    public String view1(RenderRequest request, RenderResponse response, @RequestParam(name = "count", defaultValue = "1") String count) {
        request.setAttribute("count", count);
        
        String foo = cmsService.foo();
        request.setAttribute("foo", foo);

        return "view-1";
    }


    @RenderMapping(params = "tab=2")
    public String view2() {
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


    @ActionMapping("foo")
    public void foo(ActionRequest request, ActionResponse response) {
        // CMSService cmsService = this.applicationContext.getBean(CMSService.class);

        String foo = cmsService.foo();
        request.setAttribute("foo", foo);
    }

}
