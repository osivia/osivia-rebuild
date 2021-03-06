package fr.toutatice.portail.cms.nuxeo.api;

import java.util.Locale;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;

import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.windows.WindowFactory;

public class NxControllerMock {


    /** The navigation path. */
    String basePath;

    /** The navigation path. */
    String navigationPath;

    /** The item navigation path. */
    String itemNavigationPath;

    /** The cms service. */
    private CMSService cmsService;
    
    /** The locale service. */
    private ILocaleService localeService;


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
     * Gets the locale service.
     *
     * @return the locale service
     */
    public ILocaleService getLocaleService() {
        if (localeService == null) {
            localeService =  Locator.getService(ILocaleService.class);
        }
        return localeService;

       
    }

    /**
     * Gets the CMS context.
     *
     * @return the CMS context
     */
    public CMSContext getCMSContext() {
        if (cmsContext == null) {
            cmsContext = new CMSContext(portalCtx);
            String sPreview = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.content.preview");
            cmsContext.setPreview(BooleanUtils.toBoolean(sPreview));
            String sLocale = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.content.locale");
            cmsContext.setLocale(new Locale(sLocale));

          }
        return cmsContext;
    }

    /**
     * Gets the navigation path.
     *
     * @return the navigation path
     */
    public String getNavigationPath() throws RuntimeException {
        if (navigationPath == null) {
            if (navigationId != null) {
                try {
                    UniversalID docId = navigationId;
                    org.nuxeo.ecm.automation.client.model.NxDocumentMock doc = (org.nuxeo.ecm.automation.client.model.NxDocumentMock) getCMSService()
                            .getCMSSession(getCMSContext()).getDocument( docId).getNativeItem();

                    navigationPath = ((org.nuxeo.ecm.automation.client.model.NxDocumentMock) doc).getPath();
                } catch (CMSException e) {
                    throw new RuntimeException(e);
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
        if (basePath == null) {
            if (spaceId != null) {
                try {
                    UniversalID docId = spaceId;
                    org.nuxeo.ecm.automation.client.model.NxDocumentMock doc = (org.nuxeo.ecm.automation.client.model.NxDocumentMock) getCMSService()
                            .getCMSSession(getCMSContext()).getDocument(docId).getNativeItem();
                    basePath = doc.getPath();
                } catch (CMSException e) {
                    throw new RuntimeException(e);
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
        if (itemNavigationPath == null) {
            if (contentId != null) {
                try {
                    UniversalID docId = contentId;
                    org.nuxeo.ecm.automation.client.model.NxDocumentMock doc = (org.nuxeo.ecm.automation.client.model.NxDocumentMock) getCMSService()
                            .getCMSSession(cmsContext).getDocument( docId).getNativeItem();
                    itemNavigationPath = ((org.nuxeo.ecm.automation.client.model.NxDocumentMock) doc).getPath();
                } catch (CMSException e) {
                    throw new RuntimeException(e);
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
    public NxControllerMock(PortletRequest request, PortletResponse response, PortletContext portletCtx) throws RuntimeException {

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
    public NxControllerMock(PortalControllerContext portalControllerContext) {
        this(portalControllerContext.getRequest(), portalControllerContext.getResponse(), portalControllerContext.getPortletCtx());
    }

}
