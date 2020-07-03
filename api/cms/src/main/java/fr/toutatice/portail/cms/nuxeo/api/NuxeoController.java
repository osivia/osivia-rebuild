package fr.toutatice.portail.cms.nuxeo.api;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.HierarchicalDocument;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.windows.WindowFactory;

public class NuxeoController {


    
    /** The navigation path. */
    String basePath;
    
    /** The navigation path. */
    String navigationPath;

    /** The item navigation path. */
    String itemNavigationPath;

    /** The cms service. */
    private CMSService cmsService;


    /** The cms context. */
    private CMSContext cmsContext;


    /** The portal ctx. */
    private PortalControllerContext portalCtx;
    
    /** The request. */
    PortletRequest request;


    
    public PortletRequest getRequest() {
        return request;
    }



    /**
     * Gets the CMS service.
     *
     * @return the CMS service
     */
    public CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }
        return cmsService;
    }



    /**
     * Gets the CMS context.
     *
     * @return the CMS context
     */
    public CMSContext getCMSContext() {
        if (cmsContext == null) {
            cmsContext = new CMSContext(portalCtx);
        }
        return cmsContext;
    }

    /**
     * Gets the navigation path.
     *
     * @return the navigation path
     */
    public String getNavigationPath() throws RuntimeException {
        if( navigationPath == null) {
            if( navigationId != null)   {
                 try {
                     UniversalID docId = navigationId;
                    Document doc = getCMSService().getDocument(getCMSContext(), docId);
                    if( doc instanceof HierarchicalDocument)
                        navigationPath = ((HierarchicalDocument)doc).getPath();
                } catch (CMSException e) {
                    throw new RuntimeException( e);
                }
            }
        }
        return this.navigationPath;
    }


    

    /**
     * Gets the navigation path.
     *
     * @return the navigation path
     */
    public String getBasePath() throws RuntimeException {
        if( basePath == null) {
            if( spaceId != null)   {
                 try {
                     UniversalID docId = spaceId;
                     Document doc = getCMSService().getDocument(getCMSContext(), docId);
                     if( doc instanceof HierarchicalDocument)
                         basePath = ((HierarchicalDocument)doc).getPath();
                } catch (CMSException e) {
                    throw new RuntimeException( e);
                }
            }
        }
        return this.basePath;
    }
    
    
    
    
    /**
     * Gets the navigation path.
     *
     * @return the navigation path
     */
    public String getItemNavigationPath() throws RuntimeException {
        if( itemNavigationPath == null) {
            if( contentId != null)   {
                 try {
                     UniversalID docId =  contentId;
                    Document doc = getCMSService().getDocument(getCMSContext(), docId);
                    if( doc instanceof HierarchicalDocument)
                        itemNavigationPath = ((HierarchicalDocument)doc).getPath();
                } catch (CMSException e) {
                    throw new RuntimeException( e);
                }
            }
        }
        return this.itemNavigationPath;
    }

    
    public final UniversalID navigationId;
    public final UniversalID contentId;    
    public final UniversalID spaceId;

    
    public UniversalID getSpaceId() {
        return spaceId;
    }



    /**
     * Instantiates a new nuxeo controller.
     *
     * @param request the request
     * @param response the response
     * @param portletCtx the portlet ctx
     * @throws RuntimeException the runtime exception
     */
    public NuxeoController(PortletRequest request, PortletResponse response, PortletContext portletCtx) throws RuntimeException {

        this.request = request;
        this.contentId = new UniversalID(WindowFactory.getWindow(request).getPageProperty("osivia.contentId"));         
        this.navigationId = new UniversalID(WindowFactory.getWindow(request).getPageProperty("osivia.navigationId"));
        this.spaceId = new UniversalID(WindowFactory.getWindow(request).getPageProperty("osivia.spaceId"));        
        this.portalCtx = new PortalControllerContext(portletCtx, request, response);

    }
    
    /**
     * Constructor.
     *
     * @param portalControllerContext portal controller context
     */
    public NuxeoController(PortalControllerContext portalControllerContext) {
        this(portalControllerContext.getRequest(), portalControllerContext.getResponse(), portalControllerContext.getPortletCtx());
    }
    
}
