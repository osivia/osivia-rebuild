package org.osivia.portal.cms.portlets.menu.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.edition.controller.EditionController;
import org.osivia.portal.cms.portlets.menu.model.NavigationDisplayItem;
import org.osivia.portal.cms.portlets.menu.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class MenuController implements PortletContextAware {

    /** Portlet context. */
    private PortletContext portletContext;



    /** CMS service. */
    @Autowired
    private CMSService cmsService;
    
    /** CMS service. */
    @Autowired
    private IMenuService menuService;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(MenuController.class);
    
    /**
     * Constructor.
     */
    public MenuController() {
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
        
        logger.info("view");
        
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        
        String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
        if( navigationId != null)   {

            
            UniversalID id = new UniversalID(navigationId);
            CMSContext cmsContext = nuxeoController.getCMSContext();           
            request.setAttribute("navigation", cmsService.getNavigationItem(cmsContext, id));
        }

        request.setAttribute("navigationPath", nuxeoController.getNavigationPath());
        
        


        return "view";
    }




    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

    

    @ModelAttribute("displayItem")
    public NavigationDisplayItem getDisplayItem(PortletRequest request, PortletResponse response) throws PortletException, IOException {
        
        logger.info("getDisplayItem");
        
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.menuService.getDisplayItem(portalControllerContext);
    }
    
}
