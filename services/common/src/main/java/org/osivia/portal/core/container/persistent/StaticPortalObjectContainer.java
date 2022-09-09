package org.osivia.portal.core.container.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerContext;


import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.content.spi.handler.ContentHandler;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.jems.hibernate.ContextObject;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSContentEvent;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.CMSRepositoryEvent;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;
import org.osivia.portal.core.content.IPublicationManager;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.api.cms.model.Space;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;


public class StaticPortalObjectContainer implements org.jboss.portal.core.model.portal.PortalObjectContainer {


    Map<String, Map<PortalObjectId, PortalObject>> nodes;
    ContainerContext containerContext = new ContainerContext();




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
    
    private Map<String, CMSListener> listeners;
    
    private ICMSServiceLocator cmServiceLocator;
    
    public ICMSServiceLocator getCmsServiceLocator() {
        if( cmServiceLocator == null)
            cmServiceLocator = Locator.getService( ICMSServiceLocator.MBEAN_NAME,ICMSServiceLocator.class);
        return cmServiceLocator;
    }

    
    

    @PostConstruct
    private void build() {
       nodes = new ConcurrentHashMap();
        
       listeners = new ConcurrentHashMap();
   }
    

   private CMSListener getCMSListener(String namespace) {

       CMSListener listener = listeners.get(namespace);
       if (listener == null) {
           synchronized (namespace) {
               listener = listeners.get(namespace);
               if (listener == null) {
                   listener = new CMSListener(namespace);

                   PortalControllerContext portalCtx;
                   try {
                       portalCtx = new PortalControllerContext(getCmsServiceLocator().getCMSService().getPortletContext());
                       
                   } catch (CMSException e) {
                       throw new RuntimeException(e);
                   }

                   CMSContext cmsContext = new CMSContext(portalCtx);
                   cmsContext.setSuperUserMode(true);
                   getCMSService().addListener(cmsContext, namespace, listener);


                   listeners.put(namespace, listener);
               }
           }
       }

       return listener;
   }
  

    @Override
    public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {


        try {
        Map<PortalObjectId, PortalObject> contextNodes = getContextNodes();


        PortalObject res = contextNodes.get(id);
        
        /* Check if a content has been modified */
        
        if( res != null)    {
            PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
            PortalObjectId contextId = new PortalObjectId(id.getNamespace(), contextPath);
            ContextImplBase context =  (ContextImplBase) getContextNodes().get(contextId);
            if (context != null)    {
                
                CMSListener listener = getCMSListener(context.getId().getNamespace());
                
                if( (context.getObjectNode().getVersion() < listener.getVersion()))    {
                    // TODO remove all child
                    Set<PortalObjectId> nodes = new HashSet<>(getContextNodes().keySet());
                    for( PortalObjectId poid: nodes)
                        getContextNodes().remove(poid);
                    createDefaultContext(getContextNodes());
                    res = null;
                    
                   
                }
            }
         }
        

         if (res == null) {

             String portalName = id.getPath().getName(0);

             PortalObjectPath portalPath = new PortalObjectPath("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);
             PortalObjectId portalID = new PortalObjectId(id.getNamespace(), portalPath);


             PortalObjectImplBase curPortalObject = (PortalObjectImplBase) contextNodes.get(portalID);


             if (curPortalObject == null) {
                 // Create portal
                 createPortal(contextNodes, portalID);
             }

             if (id.getPath().getLength() > 2) {
                 
                 // Create page

                 PortalControllerContext portalCtx = new PortalControllerContext(tracker.getHttpRequest());
                 CMSContext cmsContext = new CMSContext(portalCtx);

                 buildCMSContext(portalName, cmsContext);


                 // Loop on parent pages until page is found
                 
                 List<NavigationItem> pageList = new ArrayList<>();
                 String pageId = id.getPath().getLastComponentName();

                 NavigationItem pageNavigation = getCMSService().getCMSSession(cmsContext).getNavigationItem(new UniversalID(portalID.getNamespace(), pageId));
                 PortalObjectPath pagePath = id.getPath();

                 while ((!pageNavigation.isRoot())) {

                     if (contextNodes.get(new PortalObjectId(id.getNamespace(), pagePath)) != null) {
                         break;
                     }

                     pageList.add(0, pageNavigation);

                     pageNavigation = pageNavigation.getParent();
                     pagePath = pagePath.getParent();

                 }

                 // Create parents on descendant order
                 
                 for (NavigationItem curPage : pageList) {
                    PortalObjectImplBase parent = (PortalObjectImplBase) contextNodes.get(new PortalObjectId(id.getNamespace(), pagePath));
                     DefaultCMSPageFactory.createCMSPage(this, containerContext, parent, getCMSService(), cmsContext, curPage);
                     pagePath = new PortalObjectPath(pagePath.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + curPage.getDocumentId().getInternalID(), PortalObjectPath.CANONICAL_FORMAT);
                 }

             }


             res = contextNodes.get(id);


         }
        return res;
        
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        


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

            currentContextNodes = new ConcurrentHashMap();
            nodes.put(sessionId, currentContextNodes);
            
            createDefaultContext(currentContextNodes);
        }
        return currentContextNodes;
    }
    
    

    private void createDefaultContext(Map currentContextNodes) {
        UniversalID defaultId = getDefaultPortal();
        
        ContextImplBase context = createContext( defaultId.getRepositoryName(), defaultId.getInternalID());
        
        PortalObjectPath defaultPortalPath = new PortalObjectPath("/" + defaultId.getInternalID(), PortalObjectPath.CANONICAL_FORMAT);
        createPortal(currentContextNodes, new PortalObjectId(defaultId.getRepositoryName(), defaultPortalPath));
    }

    protected UniversalID getDefaultPortal() {
        PortalControllerContext portalCtx = new PortalControllerContext(tracker.getHttpRequest());
        CMSContext cmsContext = new CMSContext(portalCtx);
        
        UniversalID defaultId;
        try {
            defaultId = getCMSService().getDefaultPortal(cmsContext);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return defaultId;
    }

    
    
    
    protected ContextImplBase createContext(String nameSpace, String defaultPortalName) {
        
        String sessionId = getTracker().getHttpRequest().getSession().getId();
        Map<PortalObjectId, PortalObject> currentContextNodes = nodes.get(sessionId);
        
        PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
        long version = getCMSListener(nameSpace).getVersion();
        ObjectNodeImplBase contextNode = new ObjectNodeImplBase(new PortalObjectId(nameSpace, contextPath), "", containerContext, version);
        
          

        ContextImplBase context = new ContextImplBase();
        context.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, defaultPortalName);
        context.setObjectNode(contextNode);
        contextNode.setObject(context);

        currentContextNodes.put(context.getId(), context);
        
        
        UniversalID defaultId = getDefaultPortal();
        
        // this is the default context
        if( defaultId.getRepositoryName().equals(nameSpace))  {
            PortalObjectId rootId = new PortalObjectId("", PortalObjectPath.ROOT_PATH);
            currentContextNodes.put(rootId, context);
        }
        
        return context;
    }
    

    
    
    
    
    protected void checkContextNode(String nameSpace, String defaultPortalName) {

        String sessionId = getTracker().getHttpRequest().getSession().getId();
        Map<PortalObjectId, PortalObject> currentContextNodes = nodes.get(sessionId);

        PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
        PortalObjectId contextId = new PortalObjectId(nameSpace, contextPath);
        
        
        if (currentContextNodes.get(contextId) == null) {
            createContext( nameSpace, defaultPortalName);
            
            
        }
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
        // rendering
        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_RENDERSET, "OsiviaDefaultRenderer");
        
        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, "generic-2cols");
        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, "generic");        

        portalNode.setObject(portal);
        contextNodes.put(portal.getId(), portal);

        PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
        // Check if context exists
        checkContextNode(portalID.getNamespace(), portalName);
        ObjectNodeImplBase contextNode = ((ContextImplBase) contextNodes.get(new PortalObjectId(portalID.getNamespace(), contextPath))).getObjectNode();
        
        // Add portal
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



            try {
                PortalControllerContext portalCtx = new PortalControllerContext(tracker.getHttpRequest());

                
                String portalCMSName = portalName;
                CMSContext cmsContext = new CMSContext(portalCtx);
                portalCMSName = buildCMSContext(portalCMSName, cmsContext);
                    
                Document document = getCMSService().getCMSSession(cmsContext).getDocument(new UniversalID(portalID.getNamespace(), portalCMSName));
                
                
                if (document instanceof Space) {
                    Space space = (Space) document;
                    
                    String defaultObjectName = (String) space.getProperties().get("portal.defaultObjectName");
                    if( defaultObjectName == null)
                        defaultObjectName = DefaultCMSPageFactory.getRootPageName();
                    portal.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, defaultObjectName);
  
                    String defaultObjectId = (String) space.getProperties().get("portal.defaultPageId");
                    if( defaultObjectId != null)    {
                        portal.setDeclaredProperty("portal.defaultPageId", defaultObjectId);
                    }
                    String defaultUnprofiled = (String) space.getProperties().get("portal.unprofiledPageId");
                    if( defaultUnprofiled != null)    {
                        portal.setDeclaredProperty("portal.unprofiledPageId", defaultUnprofiled);
                    }
                    
                    String defaultThemeId = (String) space.getProperties().get(ThemeConstants.PORTAL_PROP_THEME);
                    if( defaultThemeId != null)    {
                        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, defaultThemeId);
                    }

                    
                    portal.setDeclaredProperty("portal.cms.id", document.getId().toString());
                    
                    portal.setDeclaredProperty("osivia.publication.nameType", "name");
                    
                    
                    String templateRepository = (String) space.getProperties().get("templates.namespace");
                    if( templateRepository != null)    {
                        portal.setDeclaredProperty("templates.namespace", templateRepository);
                    }                   
                   
                    NavigationItem navRoot = cmsService.getCMSSession(cmsContext).getNavigationItem(space.getId());
                      
                    DefaultCMSPageFactory.createCMSPage(this, containerContext, portal, getCMSService(), cmsContext,  navRoot);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


    }

    private String buildCMSContext(String portalCMSName, CMSContext cmsContext) {
        int iCtx = portalCMSName.indexOf(IPublicationManager.PAGEID_CTX);
        if( iCtx != -1) {

            String portalCMSCtx = portalCMSName.substring(iCtx + IPublicationManager.PAGEID_CTX.length());
            portalCMSName = portalCMSName.substring(0, iCtx);                    
            String items[] = portalCMSCtx.split(IPublicationManager.PAGEID_ITEM_SEPARATOR);
            
            for(int i= 0; i<items.length; i+=1)    {
                String values[] = items[i].split(IPublicationManager.PAGEID_VALUE_SEPARATOR);
                 if( values[0].equals(IPublicationManager.PAGEID_PREVIEW))    {
                    cmsContext.setPreview(BooleanUtils.toBoolean(values[1]));
                }
                 if( values[0].equals(IPublicationManager.PAGEID_LOCALE))    {
                     cmsContext.setLocale(new Locale(values[1]));
                 }
            }
        }
        return portalCMSName;
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
        public StaticPortalObjectContainer getContainer() {
            return StaticPortalObjectContainer.this;
        }

        public ContentType getDefaultContentType() {
            return ContentType.PORTLET;
        }

        /**
         */
        public void destroyChild(ContextObject node) {
            
            
            
        }

        /**
         * @throws DuplicatePortalObjectException
         */
        public void createChild(ContextObject node) throws DuplicatePortalObjectException {
        }

        /**
         */
        public void updated(ContextObject node) {
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
