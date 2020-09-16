package org.osivia.portal.cms.portlets.browser.controller;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class BrowserController implements PortletContextAware {

    /** Portlet context. */
    private PortletContext portletContext;



    /** CMS service. */
    @Autowired
    private CMSService cmsService;


    /**
     * Constructor.
     */
    public BrowserController() {
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
    public String view(RenderRequest request, RenderResponse response) throws CMSException {
        PortalControllerContext portalCtx = new PortalControllerContext(portletContext, request, response);
        CMSController ctrl = new CMSController(portalCtx);
        
        String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
        if( contentId != null)  {
            UniversalID id = new UniversalID(contentId);
            CMSContext cmsContext = ctrl.getCMSContext();

            TestRepository repository = TestRepositoryLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
            request.setAttribute("children", repository.getChildren(id.getInternalID()));
        }
        
        
        return "view";
    }




    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
