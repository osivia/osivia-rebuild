package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.RequestContextUtil;
import org.osivia.portal.jpb.services.CMSPage;
import org.osivia.portal.jpb.services.ContextImplBase;
import org.osivia.portal.jpb.services.PageImplBase;
import org.osivia.portal.jpb.services.PortalImplBase;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer;

public class DynamicPortalObjectContainer implements org.jboss.portal.core.model.portal.PortalObjectContainer {

    protected TemplatePortalObjectContainer portalObjectContainer;

    public TemplatePortalObjectContainer getPortalObjectContainer() {
        return portalObjectContainer;
    }

    public void setPortalObjectContainer(TemplatePortalObjectContainer portalObjectContainer) {
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

    private static Map<PortalObjectId, PortalObject> getDatas() {

        DynamicCache dynamicCache = dynamicLocalCache.get();

        if (dynamicCache == null) {
            dynamicCache = new DynamicCache();
            dynamicLocalCache.set(dynamicCache);
        }

        return dynamicCache.getDatas();

    }

    public void addDynaPortal() {

        if (this.getTracker().getHttpSession().getAttribute("osivia.demo_portal") == null) {

            // Add dynamic page to portalA

            // http://localhost:8080/portal/portal/portalA/pageA
            // http://localhost:8080/portal/portal/portalA/pageA-ajax
            // http://localhost:8080/portal/portal/portalA/dyna            
            // http://localhost:8080/portal/portal/site1/id-col1
            // http://localhost:8080/portal/portal/site1/id-col2

            PortalObjectId portalID = new PortalObjectId("", new PortalObjectPath("/portalA", PortalObjectPath.CANONICAL_FORMAT));
            PortalObjectId templateID = new PortalObjectId("", new PortalObjectPath("/portalA/pageA", PortalObjectPath.CANONICAL_FORMAT));

            PortalObject portal = portalObjectContainer.getObject(portalID);

            DynamicPageBean pageBean = new DynamicPageBean(portal, "dyna", null, null, templateID, new HashMap<String, String>());

            addDynamicPage(pageBean);

            this.getTracker().getHttpSession().setAttribute("osivia.demo_portal", "1");

            PortalObjectId dynaPageID = new PortalObjectId("", new PortalObjectPath("/portalA/dyna", PortalObjectPath.CANONICAL_FORMAT));

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, "col-2");

            DynamicWindowBean windowBean = new DynamicWindowBean(dynaPageID, "dyna", "SampleInstance", properties, null);

            addDynamicWindow(windowBean);

            this.getTracker().getHttpSession().setAttribute("osivia.demo_portal", "1");
        }
    }

    private void addCMSDynaPage(PortalObject portal, String name, String cmsTemplatePath) {
        Map cmsProperties = new ConcurrentHashMap<String, String>();

        PortalObjectId cmsTemplateID = new PortalObjectId("", new PortalObjectPath(cmsTemplatePath, PortalObjectPath.CANONICAL_FORMAT));

        DynamicPageBean cmsPageBean1 = new DynamicPageBean(portal, name, null, null, cmsTemplateID, cmsProperties);

        addDynamicPage(cmsPageBean1);
    }

    private Object getNavigationalItem(String attribute) {
        return this.getTracker().getHttpSession().getAttribute(attribute);
    }

    private void setNavigationalItem(String attribute, Object value) {
        this.getTracker().getHttpSession().setAttribute(attribute, value);
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

        // TODO : move to jbp conversation scopes
        setNavigationalItem("osivia.dynamic_windows", newWindows);
        
        // TODO MOVE TO JBP NavigationalStateContext
        List<PortalObjectId> dirtyWindowIds = (List<PortalObjectId>) this.getTracker().getHttpRequest().getAttribute("osivia.dynamic.dirtyWindows");
        if( dirtyWindowIds == null) {
            dirtyWindowIds = new ArrayList<>();
            this.getTracker().getHttpRequest().setAttribute("osivia.dynamic.dirtyWindows", dirtyWindowIds);
        }
        dirtyWindowIds.add(newWindow.getWindowId());

        // On vide le cache
        getDatas().clear();
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
        setNavigationalItem("osivia.dynamic_windows", newWindows);

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
        setNavigationalItem("osivia.dynamic_pages", newPages);

        // Remove child windows

        List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean windowBean : this.getDynamicWindows()) {
            if (!windowBean.getPageId().equals(dynamicWindowId)) {
                newWindows.add(windowBean);
            }
        }
        setNavigationalItem("osivia.dynamic_windows", newWindows);

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

    @Override
    public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {

        // get Cache Data
        PortalObject cache = getDatas().get(id);

        if (cache != null) {
            return cache;
        }

        // just for test
        // TODO : remove
        addDynaPortal();

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

        PortalObject object = portalObjectContainer.getObject(id);

        if (object instanceof PortalImplBase) {

            /* create CMS PAGES */

            // each page is stored by its id end references the real cms path

            // portal/XXXXXX/id-col2 ---> / XXXXXXXX/col1/col2


            PortalImplBase portal = (PortalImplBase) object;


            Collection children = portal.getChildren();
            for (Object o : children) {
                Page page = (Page) o;
                addCMSPage(portal, page);
            }

            return new DynamicPortal(portalObjectContainer, (PortalImplBase) object, this);
        }

        if (object instanceof ContextImplBase) {
            return new DynamicContext(portalObjectContainer, (ContextImplBase) object, this);

        }

        return object;
    }

    private void addCMSPage(Portal portal, Page page) {
        if (page instanceof CMSPage) {
            String pageDynamicName;
            
            if( "name".equals(portal.getDeclaredProperty("osivia.publication.nameType")))
                pageDynamicName =  ((CMSPage) page).getName();
            else
                pageDynamicName = ((CMSPage) page).getCmsID();

            addCMSDynaPage(portal,pageDynamicName, page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));

            Collection children = page.getChildren();
            for (Object o : children) {
                if (o instanceof Page) {
                    Page subPage = (Page) o;
                    addCMSPage(portal, subPage);
                }
            }
        }
    }

    @Override
    public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType) throws IllegalArgumentException {
        return portalObjectContainer.getObject(id, expectedType);
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
