package org.osivia.portal.sample.controller;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Sample controller.
 * 
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping
public class SampleController {

    /**
     * Constructor.
     */
    public SampleController() {
        super();
    }


    /**
     * Render mapping.
     *
     * @param request portlet request
     * @param response portlet response
     * @return render view path
     */
    @RenderMapping
    public String view(PortletRequest request, PortletResponse response) {
        return "view";
    }

}
