package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.content.spi.handler.ContentHandler;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.RequestContextUtil;
import org.osivia.portal.jpb.services.ContextImplMock;
import org.osivia.portal.jpb.services.PageImplMock;
import org.osivia.portal.jpb.services.PortalImplMock;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer;
import org.osivia.portal.jpb.services.WindowImplMock;

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
			// http://localhost:8080/portal/portal/portalA/dyna ->
			// http://localhost:8080/portal/portal/portalA/pageA

			PortalObjectId portalID = new PortalObjectId("",
					new PortalObjectPath("/portalA", PortalObjectPath.CANONICAL_FORMAT));
			PortalObjectId templateID = new PortalObjectId("",
					new PortalObjectPath("/portalA/pageA", PortalObjectPath.CANONICAL_FORMAT));

			PortalObject portal = portalObjectContainer.getObject(portalID);

			DynamicPageBean pageBean = new DynamicPageBean(portal, "dyna", null, null, templateID,
					new HashMap<String, String>());

			addDynamicPage(pageBean);

			this.getTracker().getHttpSession().setAttribute("osivia.demo_portal", "1");

			PortalObjectId dynaPageID = new PortalObjectId("",
					new PortalObjectPath("/portalA/dyna", PortalObjectPath.CANONICAL_FORMAT));

			PortalObject dynaPage = getObject(dynaPageID);

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
			properties.put(ThemeConstants.PORTAL_PROP_REGION, "col-2");

			DynamicWindowBean windowBean = new DynamicWindowBean(dynaPageID, "dyna", "SampleInstance", properties,
					null);

			addDynamicWindow(windowBean);
		}
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
			if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT)
					.equals(newWindow.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT))) {
				newWindows.add(window);
			}
		}

		newWindows.add(newWindow);

		setNavigationalItem("osivia.dynamic_windows", newWindows);

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
				PageImplMock template = (PageImplMock) portalObjectContainer.getObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(portalObjectContainer,
						dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(),
						template, null, this, dynamicPageBean, templateId);

				return dynamicPage;
			}

			// Accès à une window d'une page template
			// Un template ne peut pas contenir de sous-page,
			// il s'agit donc forcément d'une window
			if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PageImplMock template = (PageImplMock) portalObjectContainer.getObject(templateId);
				DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(portalObjectContainer,
						dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(),
						template, null, this, dynamicPageBean, templateId);
				String windowName = id.getPath().getLastComponentName();

				return dynamicPage.getChild(windowName);

			}
		}

		PortalObject object = portalObjectContainer.getObject(id);

		// statics page compatibility
		if (object instanceof PageImplMock) {
			PageImplMock originalPage = ((PageImplMock) object);

			PortalObjectPath parentPath = originalPage.getId().getPath().getParent();
			PortalObjectId parentId = new PortalObjectId("", parentPath);

			Portal portal = (Portal) this.getObject(parentId);
			DynamicPageBean pageBean = new DynamicPageBean(portal, originalPage.getName(),
					"bus" + System.currentTimeMillis(), null, originalPage.getId(), new HashMap<String, String>());

			addDynamicPage(pageBean);
			return getObject(id);
		}

		if (object instanceof PortalImplMock) {
			return new DynamicPortal(portalObjectContainer, (PortalImplMock) object, this);
		}

		if (object instanceof ContextImplMock) {
			return new DynamicContext(portalObjectContainer, (ContextImplMock) object, this);

		}

		return object;
	}

	@Override
	public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType)
			throws IllegalArgumentException {
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