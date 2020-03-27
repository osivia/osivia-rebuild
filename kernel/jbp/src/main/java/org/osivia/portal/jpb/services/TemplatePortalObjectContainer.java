package org.osivia.portal.jpb.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.content.spi.handler.ContentHandler;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.services.Locator;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.tracker.ITracker;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

public class TemplatePortalObjectContainer implements org.jboss.portal.core.model.portal.PortalObjectContainer {

    Map<String, Map<PortalObjectId, PortalObject>> nodes;
    ContainerContext containerContext = new ContainerContext();


    private static String PORTAL_A_NAME = "portalA";


    private ContentProviderRegistry contentProviderRegistry;

    public ContentProviderRegistry getContentProviderRegistry() {
        return contentProviderRegistry;
    }

    public void setContentProviderRegistry(ContentProviderRegistry contentProviderRegistry) {
        this.contentProviderRegistry = contentProviderRegistry;
    }

    private ITracker tracker;

    public ITracker getTracker() {
        return this.tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }

    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    private CMSService cmsService;

    @PostConstruct
    private void build() {

        nodes = new ConcurrentHashMap();


    }


    @Override
    public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {


        Map<PortalObjectId, PortalObject> contextNodes = getContextNodes();


        PortalObject res = contextNodes.get(id);

        if (res == null) {
            // Create portal

            String portalName = id.getPath().getName(0);

            PortalObjectPath portalPath = new PortalObjectPath("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);
            PortalObjectId portalID = new PortalObjectId("", portalPath);

            PortalObjectImplBase curPortalObject = (PortalObjectImplBase) nodes.get(id);
            if (curPortalObject == null) {

                createPortal(contextNodes, portalID);


            }

            res = contextNodes.get(id);
        }

        return res;

    }

    /**
     * Gets the context nodes.
     *
     * @param sessionId the session id
     * @return the context nodes
     */
    protected Map<PortalObjectId, PortalObject> getContextNodes() {
        String sessionId = getTracker().getHttpRequest().getSession().getId();
        Map currentContextNodes = nodes.get(sessionId);
        if (currentContextNodes == null) {

            PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
            ObjectNodeImplBase contextNode = new ObjectNodeImplBase(new PortalObjectId("", contextPath), "", containerContext);

            ContextImplBase context = new ContextImplBase();
            context.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, PORTAL_A_NAME);
            context.setObjectNode(contextNode);
            contextNode.setObject(context);
            currentContextNodes = new ConcurrentHashMap();
            nodes.put(sessionId, currentContextNodes);
            currentContextNodes.put(context.getId(), context);

            PortalObjectPath defaultPortalPath = new PortalObjectPath("/" + PORTAL_A_NAME, PortalObjectPath.CANONICAL_FORMAT);
            createPortal(currentContextNodes, new PortalObjectId("", defaultPortalPath));
        }
        return currentContextNodes;
    }


    private void createPortal(Map<PortalObjectId, PortalObject> contextNodes, PortalObjectId portalID) {

        String portalName = portalID.getPath().getName(0);

        ObjectNodeImplBase portalNode = new ObjectNodeImplBase(portalID, portalName, containerContext);

        PortalImplBase portal = new PortalImplBase();
        portal.setObjectNode(portalNode);
        // states
        Set<WindowState> states = new HashSet<>();
        states.add(WindowState.NORMAL);
        states.add(WindowState.MAXIMIZED);
        portal.setWindowStates(states);
        // modes
        Set<Mode> modes = new HashSet<>();
        modes.add(Mode.ADMIN);
        modes.add(Mode.VIEW);
        portal.setModes(modes);

        portalNode.setObject(portal);
        contextNodes.put(portal.getId(), portal);

        PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
        ObjectNodeImplBase contextNode = ((ContextImplBase) contextNodes.get(new PortalObjectId("", contextPath))).getObjectNode();
        contextNode.getChildren().put(portalName, portalNode);


        portalNode.setParent(contextNode);

        /*
         * sample layout
         * 
         * String layoutCode = "<body id=\"body\"><table class=\"layout\" width=\"100%\">\n" +
         * "  <tbody><tr>\n" +
         * "    <td valign=\"top\">\n" +
         * "      <!-- insert the content of the 'center' region of the page, and assign the css selector id 'regionB' -->\n" +
         * "      <div id=\"col-1\"></div>\n" +
         * "      <div id=\"col-2\"></div>\n" +
         * "  </tr>\n" +
         * "</tbody></table></body>";
         * 
         * 
         * portal.setDeclaredProperty("osivia.layout.generic-2cols.code", layoutCode);
         */


//        if ("portalA".equals(portalName)) {
//            portal.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, "pageA");
//            portal.setDeclaredProperty("osivia.publication.nameType", "name");
//
//            // static pages
//            String path = "/portalA/pageA";
//            Map<String, String> pageProperties = new HashMap<>();
//            pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
//            pageProperties.put(ThemeConstants.PORTAL_PROP_THEME, "generic");
//            SampleStaticPageFactory.createCMSPage(this, containerContext, new PortalObjectId("", new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT)),
//                    pageProperties);
//
//            path = "/portalA/pageA-ajax";
//            pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");
//            SampleStaticPageFactory.createCMSPage(this, containerContext, new PortalObjectId("", new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT)),
//                    pageProperties);
//
//            path = "/portalA/simple-ajax";
//            pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, "true");
//            SampleStaticSimplePageFactory.createCMSPage(this, containerContext,
//                    new PortalObjectId("", new PortalObjectPath(path, PortalObjectPath.CANONICAL_FORMAT)), pageProperties);
//        } else {
            try {
                PortalControllerContext portalCtx = new PortalControllerContext(tracker.getHttpRequest());
                CMSContext cmsContext = new CMSContext(portalCtx);
                Document document = getCMSService().getDocument(cmsContext, portalName);
                if (document instanceof Space) {
                    Space space = (Space) document;
                    portal.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, "default");
                    portal.setDeclaredProperty("osivia.publication.nameType", "name");

                    DefaultCMSPageFactory.createCMSPage(this, containerContext, portal, getCMSService(), cmsContext, space);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//        }


        // else {
        // // CMS Sample pages
        // Map<String, String> pageProperties = new HashMap<>();
        //
        // String path = "/" + portalName ;
        // path = path + "/col-1";
        // SampleSitePageFactory.createCMSPage(this, containerContext, new PortalObjectId("", new PortalObjectPath (path, PortalObjectPath.CANONICAL_FORMAT )),
        // pageProperties);
        // path = path + "/col-2";
        // SampleSitePageFactory.createCMSPage(this, containerContext, new PortalObjectId("", new PortalObjectPath (path, PortalObjectPath.CANONICAL_FORMAT )),
        // pageProperties);
        // }

    }

    @Override
    public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Context getContext() {
        PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
        PortalObjectId id = new PortalObjectId("", contextPath);
        return (Context) getContextNodes().get(id);
    }

    @Override
    public Context getContext(String namespace) {
        return getContext();
    }

    @Override
    public Context createContext(String namespace) throws DuplicatePortalObjectException {
        return null;
    }

    @Override
    public AuthorizationDomain getAuthorizationDomain() {
        // TODO Auto-generated method stub
        return null;
    }

    public class ContainerContext {

        /**
         */
        public TemplatePortalObjectContainer getContainer() {
            return TemplatePortalObjectContainer.this;
        }

        public ContentType getDefaultContentType() {
            return ContentType.PORTLET;
        }

        /**
         */
        public void destroyChild(ObjectNode node) {
        }

        /**
         * @throws DuplicatePortalObjectException
         */
        public void createChild(ObjectNode node) throws DuplicatePortalObjectException {
        }

        /**
         */
        public void updated(ObjectNode node) {
        }

        /**
         */
        public ContentHandler getContentHandler(ContentType contentType) {
            ContentProvider contentProvider = contentProviderRegistry.getContentProvider(contentType);

            //
            if (contentProvider != null) {
                return contentProvider.getHandler();
            } else {
                return null;
            }
        }
    }

}
