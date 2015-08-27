package org.osivia.portal.sample.controller;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.lang3.StringUtils;
import org.osivia.portal.sample.model.SampleForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping(value = "VIEW", params = "tab=4")
@SessionAttributes(types = SampleForm.class)
public class SampleFormController {


    public SampleFormController() {
        super();
    }


    @RenderMapping(params = "step=1")
    public String view1() {
        return "form-1";
    }


    @RenderMapping(params = "step=2")
    public String view2() {
        return "form-2";
    }


    @ExceptionHandler(value = Exception.class)
    public String exception() {
        return "error";
    }


    @ActionMapping(name = "submit1")
    public void submit1(ActionRequest request, ActionResponse response, @ModelAttribute(value = "sampleForm") SampleForm form) throws PortletException {
        if (StringUtils.isNotBlank(form.getFirstName())) {
            response.setRenderParameter("tab", "4");
            response.setRenderParameter("step", "2");
        } else {
            throw new PortletException("First name is blank.");
        }
    }


    @ActionMapping(name = "submit2")
    public void submit2(ActionRequest request, ActionResponse response, SessionStatus status, @ModelAttribute(value = "sampleForm") SampleForm form)
            throws PortletException {
        if (StringUtils.isNotBlank(form.getFirstName())) {
            status.setComplete();
        } else {
            throw new PortletException("Last name is blank.");
        }
    }


    @ModelAttribute(value = "sampleForm")
    public SampleForm getForm() {
        return new SampleForm();
    }

}
