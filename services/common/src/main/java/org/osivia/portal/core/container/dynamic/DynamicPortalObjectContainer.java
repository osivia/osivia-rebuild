package org.osivia.portal.core.container.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.container.persistent.CMSPage;
import org.osivia.portal.core.container.persistent.ContextImplBase;
import org.osivia.portal.core.container.persistent.PageImplBase;
import org.osivia.portal.core.container.persistent.PortalImplBase;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.RequestContextUtil;


/**
 * Dynamic pages container.
 * 
 * Dynamic page are stored into DynamicPageBean and created on demand
 * 
 */
public class DynamicPortalObjectContainer implements org.jboss.portal.core.model.portal.PortalObjectContainer {

    protected StaticPortalObjectContainer portalObjectContainer;

    public StaticPortalObjectContainer getPortalObjectContainer() {
        return portalObjectContainer;
    }

    public void setPortalObjectContainer(StaticPortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    private ITracker tracker;

    public ITracker getTracker() {
        return this.tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }

    private static ThreadLocal<DynamicCache> dynamicLocalCache = new ThreadLocal<DynamicCache>();

    public static DynamicCache getDynamicCache() {

        return dynamicLocalCache.get();
    }

    public static void clearCache() {
        getDatas().clear();
    }

    public static void removeCache() {
        dynamicLocalCache.remove();
    }

    public static void addToCache(PortalObjectId id, PortalObject value) {
        getDatas().put(id, value);
    }
    
    public static PortalObject getFromCache(PortalObjectId id) {
        return getDatas().get(id);
    }

    private static Map<PortalObjectId, PortalObject> getDatas() {

        DynamicCache dynamicCache = dynamicLocalCache.get();

        if (dynamicCache == null) {
            dynamicCache = new DynamicCache();
            dynamicLocalCache.set(dynamicCache);
        }

        return dynamicCache.getDatas();

    }



    private void addCMSDynaPage(PortalObject portal, String name, PortalObjectId cmsTemplateID) {
        
        //System.out.println( "addCMSDynaPage " + cmsTemplateID.toString() );
        Map cmsProperties = new ConcurrentHashMap<String, String>();

        DynamicPageBean cmsPageBean1 = new DynamicPageBean(portal, name, null, null, cmsTemplateID, cmsProperties);

        addDynamicPage(cmsPageBean1);
    }

    private Object getNavigationalItem(String attribute) {

        return this.getTracker().getHttpRequest().getAttribute(attribute);
    }

    private void setNavigationalItem(String attribute, Object value) {

        this.getTracker().getHttpRequest().setAttribute(attribute, value);
    }

    public void addDynamicWindow(DynamicWindowBean newWindow) {

        List<DynamicWindowBean> windows = this.getDynamicWindows();
        List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean window : windows) {
            if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT).equals(newWindow.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT))) {
                newWindows.add(window);
            }
        }

        newWindows.add(newWindow);


        setDynamicWindows(newWindows);
        

        List<PortalObjectId> dirtyWindowIds = (List<PortalObjectId>) this.getTracker().getHttpRequest().getAttribute("osivia.dynamic.dirtyWindows");
        if( dirtyWindowIds == null) {
            dirtyWindowIds = new ArrayList<>();
            this.getTracker().getHttpRequest().setAttribute("osivia.dynamic.dirtyWindows", dirtyWindowIds);
        }
        dirtyWindowIds.add(newWindow.getWindowId());

        // On vide le cache
        getDatas().clear();
    }

    public void setDynamicWindows(List<DynamicWindowBean> newWindows) {
        setNavigationalItem("osivia.dynamic_windows", newWindows);
    }

    public void addDynamicPage(DynamicPageBean newPage) {

        List<DynamicPageBean> pages = this.getDynamicPages();
        List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

        int maxOrder = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;

        // Reconstruction du tableau

        for (DynamicPageBean page : pages) {
            if (!page.getPageBusinessId().toString(PortalObjectPath.SAFEST_FORMAT)
                    .equals(newPage.getPageBusinessId().toString(PortalObjectPath.SAFEST_FORMAT))) {
                newPages.add(page);
                if (page.getOrder() > maxOrder) {
                    maxOrder = page.getOrder();
                }
            }
        }

        // Insertion nouvelle page

        if (newPage.getOrder() == -1) {
            newPage.setOrder(maxOrder + 1);
        }
        newPages.add(newPage);

        setDynamicPages(newPages);
        
        this.getTracker().getHttpRequest().setAttribute("osivia.dynamic.newPage", Boolean.TRUE);
        
        // On vide le cache
        getDatas().clear();        
    }

    public void setDynamicPages(List<DynamicPageBean> newPages) {
        setNavigationalItem("osivia.dynamic_pages", newPages);
    }

    public List<DynamicWindowBean> getPageWindows(PortalObjectId pageId) {
        List<DynamicWindowBean> windows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean windowBean : this.getDynamicWindows()) {
            if (windowBean.getPageId().equals(pageId)) {
                windows.add(windowBean);
            }
        }

        return windows;
    }

    public void removeDynamicWindow(String dynamicWindowId) {

        List<DynamicWindowBean> windows = this.getDynamicWindows();
        List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean window : windows) {
            if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId)) {
                newWindows.add(window);
            }
        }
        setDynamicWindows(newWindows);

        // On vide le cache
        getDatas().clear();

    }

    public void removeDynamicPage(String dynamicWindowId) {

        List<DynamicPageBean> pages = this.getDynamicPages();
        List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

        for (DynamicPageBean page : pages) {
            if (!page.getPageId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId)) {
                newPages.add(page);
            }
        }
        setDynamicPages(newPages);

        // Remove child windows

        List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean windowBean : this.getDynamicWindows()) {
            if (!windowBean.getPageId().equals(dynamicWindowId)) {
                newWindows.add(windowBean);
            }
        }
        setDynamicWindows(newWindows);

        // On vide le cache
        getDatas().clear();

    }

    public List<DynamicWindowBean> getDynamicWindows() {

        List<DynamicWindowBean> windows = null;

        if (this.getTracker().getHttpRequest() != null) {
            windows = (List<DynamicWindowBean>) getNavigationalItem("osivia.dynamic_windows");
        }

        if (windows == null) {
            windows = new ArrayList<DynamicWindowBean>();
        }

        return windows;
    }

    public ServerInvocation getInvocation() {

        ServerInvocation invocation = RequestContextUtil.getServerInvocation();

        return invocation;
    }

    public List<DynamicPageBean> getDynamicPages() {

        List<DynamicPageBean> pages = null;

        if (this.getTracker().getHttpSession() != null) {
            pages = (List<DynamicPageBean>) getNavigationalItem("osivia.dynamic_pages");
        }

        if (pages == null) {
            pages = new ArrayList<DynamicPageBean>();
        }

        return pages;
    }

    public HttpSession getSession() {
        HttpSession session = this.getTracker().getHttpRequest().getSession();
        return session;
    }
    
    
    @Override
    public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {

        // get Cache Data
        PortalObject cache = getDatas().get(id);

        if (cache != null) {
            return cache;
        }


        
        PortalObject dynamicObject = getExistingDynamicObject(id);
        
        if( dynamicObject != null)
            return dynamicObject;

        // fetch object
        dynamicObject = fetchDynamicObject(id);

        return dynamicObject;
    }

    /**
     * Gets the existing dynamic object.
     *
     * @param id the id
     * @return the existing dynamic object
     */
    protected PortalObject getExistingDynamicObject(PortalObjectId id) {
        
        Object cmd = this.getTracker().getCurrentState();

        for (DynamicPageBean dynamicPageBean : this.getDynamicPages()) {

            if (dynamicPageBean.getPageId().equals(id)) {
                PortalObjectId templateId = dynamicPageBean.getTemplateId();
                PageImplBase template = (PageImplBase) portalObjectContainer.getObject(templateId);
                DynamicPage dynamicPage = DynamicTemplatePage.createPage(portalObjectContainer, dynamicPageBean.getParentId(), dynamicPageBean.getName(),
                        dynamicPageBean.getDisplayNames(), template, null, this, dynamicPageBean, templateId);

                return dynamicPage;
            }

            // Accès à une window d'une page template
            // Un template ne peut pas contenir de sous-page,
            // il s'agit donc forcément d'une window
            if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

                PortalObjectId templateId = dynamicPageBean.getTemplateId();
                PageImplBase template = (PageImplBase) portalObjectContainer.getObject(templateId);
                DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(portalObjectContainer, dynamicPageBean.getParentId(),
                        dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), template, null, this, dynamicPageBean, templateId);
                String windowName = id.getPath().getLastComponentName();

                return dynamicPage.getChild(windowName);

            }
        }
        
        return null;
    }

   
    
    
    /**
     * Fetch dynamic object.
     *
     * @param id the id
     * @return the portal object
     */
    protected PortalObject fetchDynamicObject(PortalObjectId id) {
        
        PortalObject staticObject = portalObjectContainer.getObject(id);
        

        if (staticObject instanceof PortalImplBase) {
            // If context already in cache, force update of children
        	// (ex: case of loose of session where a default portal is loaded and later the current portal is restored from url)
        	// #PortalObjectCommandFactory
            PortalObjectId contextId = new PortalObjectId(id.getNamespace(), id.getPath().getParent());
            PortalObject context = DynamicPortalObjectContainer.getFromCache(contextId);
            if( context instanceof DynamicContext)	{
            	((DynamicContext) context).children = null;
            }
             return new DynamicPortal(portalObjectContainer, (PortalImplBase) staticObject, this);
        }

        if (staticObject instanceof ContextImplBase) {
            return new DynamicContext(portalObjectContainer, (ContextImplBase) staticObject, this);
        }
        
        return null;
    }



    @Override
    public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType) throws IllegalArgumentException {
        return (T) getObject(id);
    }

    @Override
    public Context getContext() {
        return getContext("");
    }

    @Override
    public Context getContext(String namespace) {
        PortalObjectId id = new PortalObjectId(namespace, PortalObjectPath.ROOT_PATH);
        return (Context) getObject(id);
    }

    @Override
    public Context createContext(String namespace) throws DuplicatePortalObjectException {
        return null;
    }

    @Override
    public AuthorizationDomain getAuthorizationDomain() {
        return portalObjectContainer.getAuthorizationDomain();
    }

}
