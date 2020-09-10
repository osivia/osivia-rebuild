package org.osivia.portal.cms.portlets.edition.controller;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.producers.sample.inmemory.IRepositoryUpdate;
import fr.toutatice.portail.cms.producers.sample.inmemory.TemplatesLocator;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class EditionController implements PortletContextAware, ApplicationContextAware {

    /** Portlet context. */
    private PortletContext portletContext;


    /** CMS service. */
    @Autowired
    private CMSService cmsService;

    /** Application context. */
    private ApplicationContext applicationContext;

    @Autowired
    private IPortalUrlFactory portalUrlFactory;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(EditionController.class);

    /**
     * Constructor.
     */
    public EditionController() {
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
        return "view";
    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "addPage")
    public void addPage(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

            String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (navigationId != null) {

                UniversalID id = new UniversalID(navigationId);

                CMSContext cmsContext = getCMSContext(portalControllerContext, id);


                IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

                String newID = "" + System.currentTimeMillis();

                ((IRepositoryUpdate) repository).addEmptyPage(newID, "" + System.currentTimeMillis(), id.getInternalID());


                String url = portalUrlFactory.getViewContentUrl(portalControllerContext, new UniversalID(id.getRepositoryName(), newID));
                response.sendRedirect(url);


            }
        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }

    }

    /**
     * Add page sample
     */
    @ActionMapping(name = "addPortlet")
    public void addPortlet(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);


            addPortletToRegion(request, portalControllerContext, "SampleInstance", "col-2");
        } catch (PortalException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Add page sample
     */
    @ActionMapping(name = "switchMode")
    public void switchMode(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {

            // TODO : create edition service
            HttpServletRequest mainRequest = (HttpServletRequest) request.getAttribute(Constants.PORTLET_ATTR_HTTP_REQUEST);
            String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            UniversalID id = new UniversalID(navigationId);
            
            String editionModeKey = "editionMode."+id.getRepositoryName();
            String editionMode = (String) mainRequest.getSession().getAttribute(editionModeKey);
            if ("preview".equals(editionMode)) {
                mainRequest.getSession().removeAttribute(editionModeKey);
            } else
                mainRequest.getSession().setAttribute(editionModeKey, "preview");

            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
  
            String url = portalUrlFactory.getViewContentUrl(portalControllerContext, id);
            response.sendRedirect(url);

        } catch (PortalException | IOException e) {
            throw new PortletException(e);
        }
    }


    /**
     * Add portlet sample
     */
    @ActionMapping(name = "addPortletNav")
    public void addPortletNav(ActionRequest request, ActionResponse response) throws PortletException, CMSException {

        try {
            // Portal Controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

            addPortletToRegion(request, portalControllerContext, "SampleRemote", "nav");
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }


    protected void addPortletToRegion(ActionRequest request, PortalControllerContext portalControllerContext, String portletName, String region)
            throws CMSException {
        String navigationId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
        if (navigationId != null) {

            UniversalID id = new UniversalID(navigationId);

            CMSContext cmsContext = getCMSContext(portalControllerContext, id);


            IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());

            String windowID = "" + System.currentTimeMillis();

            ((IRepositoryUpdate) repository).addWindow(windowID, windowID, portletName, region, id.getInternalID());

        }
    }

    

    /**
     * publish sample
     */
    @ActionMapping(name = "publish")
    public void publish(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.contentId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = getCMSContext(portalControllerContext, id);

                IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof IRepositoryUpdate) {
                    ((IRepositoryUpdate) repository).publish(id.getInternalID());
                }

                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }

    
    /**
     * add folder
     */
    @ActionMapping(name = "addFolder")
    public void addFolder(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = getCMSContext(portalControllerContext, id);

                IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof IRepositoryUpdate) {
                    String newID = "" + System.currentTimeMillis();

                    ((IRepositoryUpdate) repository).addFolder(newID, newID, id.getInternalID());
                }

                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }
    
    /**
     * add document
     */
    @ActionMapping(name = "addDocument")
    public void addDocument(ActionRequest request, ActionResponse response) throws PortletException, CMSException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        try {
            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (contentId != null) {

                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = getCMSContext(portalControllerContext, id);

                IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                if( repository instanceof IRepositoryUpdate) {
                    String newID = "" + System.currentTimeMillis();

                    ((IRepositoryUpdate) repository).addDocument(newID, newID, id.getInternalID());
                }

                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

    }
    
    @ModelAttribute("status")
    public EditionStatus getStatus(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        // application form
        EditionStatus status = this.applicationContext.getBean(EditionStatus.class);

        try {

            String contentId = WindowFactory.getWindow(request).getPageProperty("osivia.navigationId");
            if (contentId != null) {
                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = getCMSContext(portalControllerContext, id);
                status.setPreview(cmsContext.isPreview());

                IRepositoryUpdate repository = TemplatesLocator.getTemplateRepository(cmsContext, id.getRepositoryName());
                status.setSupportPreview(repository.supportPreview());
                
                status.setPageEdition(repository.supportPageEdition());
                
                
                Document currentDoc = cmsService.getDocument(cmsContext, id);
                status.setSubtypes(currentDoc.getSubTypes());
                
                if( cmsContext.isPreview()) {
                    cmsContext.setPreview(false);
                    try {
                        cmsService.getDocument(cmsContext, id);
                        status.setHavingPublication(true);
                    } catch (CMSException e) {
                        // Not found
                    }
                }
                
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return status;
    }


    protected CMSContext getCMSContext(PortalControllerContext portalControllerContext, UniversalID id) {
        CMSContext cmsContext = new CMSContext(portalControllerContext, id);
        return cmsContext;
    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
