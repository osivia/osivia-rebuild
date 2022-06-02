package org.osivia.portal.cms.portlets.content.delete.controller;

import java.io.IOException;
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
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.producers.test.TestRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;

/**
 * Delete content controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")

public class DeleteController {

    /** Portlet context. */
    private PortletContext portletContext;


    @Autowired
    private IPortalUrlFactory portalUrlFactory;
    
    @Autowired
    private CMSService cmsService;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(DeleteController.class);

    /**
     * Constructor.
     */
    public DeleteController() {
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
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortalException {
        return "view";
    }


    /**
     * Delete portlet
     */
    @ActionMapping(name = "deleteContent")
    public void delete(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
            CMSController ctrl = new CMSController(portalControllerContext);

            // Current document
            String sContentId = WindowFactory.getWindow(request).getProperty("osivia.delete.id");
            UniversalID id = new UniversalID(sContentId);
            
            NavigationItem nav = cmsService.getCMSSession(ctrl.getCMSContext()).getNavigationItem( id);
            if( nav.getDocumentId().equals(id))
                nav = nav.getParent();


            deleteContent(request, portalControllerContext, ctrl, id);

            String url = this.portalUrlFactory.getViewContentUrl(portalControllerContext, ctrl.getCMSContext(), nav.getDocumentId());
            response.sendRedirect(url);
            
        } catch (PortalException e) {
            throw new PortletException(e);
        } catch (IOException e) {
            throw new PortletException(e);
        }
    }

    protected void deleteContent(ActionRequest request, PortalControllerContext portalControllerContext, CMSController ctrl, UniversalID id ) throws CMSException {


        TestRepository repository = TestRepositoryLocator.getTemplateRepository(ctrl.getCMSContext(), id.getRepositoryName());
        if (repository instanceof TestRepository) {
             ((TestRepository) repository).deleteDocument(id.getInternalID());
        }

    }


}
