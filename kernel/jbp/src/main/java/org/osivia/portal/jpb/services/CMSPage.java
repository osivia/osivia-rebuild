package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.Format;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class CMSPage extends PageImplBase {

	/** Local properties. */
	protected Map<String, String> properties = null;

	private PortalObjectPath pagePath;

	private String pageName;
	ContainerContext containerContext;
	TemplatePortalObjectContainer container;
	private ObjectNodeImplBase parentNode;

	
	private String cmsID;
	
	
	public String getCmsID() {
		return cmsID;
	}

	public void setCmsID(String cmsID) {
		this.cmsID = cmsID;
	}

	public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext,
			PortalObjectId pageId) {
		new CMSPage(container, containerContext, pageId);
	}

	private CMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId) {
		super();

		PortalObjectId parentId = new PortalObjectId("", pageId.getPath().getParent());
		PortalObjectImplBase parent = (PortalObjectImplBase) container.getObject(parentId);
		

		this.containerContext = containerContext;
		this.container = container;
		
		this.parentNode = parent.getObjectNode();
		this.pagePath = new PortalObjectPath(pageId.getPath().toString(), PortalObjectPath.CANONICAL_FORMAT);
		this.pageName = pagePath.getLastComponentName();


		ObjectNodeImplBase pageNode = new ObjectNodeImplBase(pageId, pageName, containerContext);
		this.setObjectNode(pageNode);

		pageNode.setObject(this);
		container.nodes.put(this.getId(), this);

		// parent relations
		pageNode.setParent(parentNode);
		parentNode.getChildren().put(pageName, pageNode);

		// children relations
		pageNode.setChildren(computeWindows());

		// TODO init form CMS
		cmsID = "id_"+pageName;
		
		Map<String, String> pageProperties = new HashMap<String, String>();

		for (String key : pageProperties.keySet()) {
			setDeclaredProperty(key, pageProperties.get(key));
		}
	}

	private Map<String, PortalObject> computeWindows() {

		Map windows = new ConcurrentHashMap<>();

		/* Inherited Windows */

		PortalObject parent = parentNode.getObject();

		if (parent instanceof Page) {
			Collection<PortalObject> parentWindows = parent.getChildren(PortalObject.WINDOW_MASK);
			for (PortalObject parentWindow : parentWindows) {
				WindowImplBase parentWindowMock = (WindowImplBase) parentWindow;
				ObjectNodeImplBase dupWinNode = duplicateWindow(parentWindowMock, false);
				if (dupWinNode != null)
					windows.put(parentWindow.getName(), dupWinNode);

			}
		}

		/* Template Windows */

		for (PortalObjectId templateID : getTemplatesID()) {

			PortalObject template = container.getObject(templateID);
			Collection<PortalObject> tmplWindows = template.getChildren(PortalObject.WINDOW_MASK);

			for (PortalObject tmplWindow : tmplWindows) {

				if (tmplWindow instanceof WindowImplBase) {
					ObjectNodeImplBase dupWinNode = duplicateWindow(tmplWindow, true);
					if (dupWinNode != null)

						windows.put(tmplWindow.getName(), dupWinNode);
				}

			}
		}

		/* CMS Windows */

		String windowName = "win-" + pageName;

		PortalObjectPath winPath = new PortalObjectPath(
				pagePath.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + windowName,
				PortalObjectPath.CANONICAL_FORMAT);

		ObjectNodeImplBase winNode = new ObjectNodeImplBase(new PortalObjectId("", winPath), windowName,
				containerContext);
		WindowImplBase win = new WindowImplBase();
		win.setContext(containerContext);
		win.setURI("SampleInstance");
		win.setObjectNode(winNode);
		win.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION, pageName);
		win.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, "0");
		winNode.setObject(win);
		container.nodes.put(win.getId(), win);
		windows.put(windowName, winNode);

		return windows;

	}

	private ObjectNodeImplBase duplicateWindow(PortalObject tmplWindow, boolean injected) {
		WindowImplBase tmplWindowMock = (WindowImplBase) tmplWindow;

		PortalObjectPath tmplWinPath = new PortalObjectPath(
				pagePath.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + tmplWindow.getName(),
				PortalObjectPath.CANONICAL_FORMAT);

		ObjectNodeImplBase dupWinNode = new ObjectNodeImplBase(new PortalObjectId("", tmplWinPath),
				tmplWindow.getName(), containerContext);
		WindowImplBase dupWin = new WindowImplBase();
		dupWin.setContext(containerContext);
		dupWin.setURI(tmplWindowMock.getURI());
		dupWin.setObjectNode(dupWinNode);
		for (String key : tmplWindowMock.getDeclaredPropertyMap().keySet()) {
			dupWin.setDeclaredProperty(key, tmplWindowMock.getDeclaredPropertyMap().get(key));
		}

		dupWin.setDeclaredProperty("osivia.window.injected", "1");
		dupWinNode.setObject(dupWin);
		container.nodes.put(dupWin.getId(), dupWin);
		return dupWinNode;
	}

	@Override
	public Map<String, String> getDeclaredProperties() {
		return properties;
	}

	private List<PortalObjectId> getTemplatesID() {
		List<PortalObjectId> templateIds = new ArrayList<>();
		PortalObjectPath mainTemplatePath = new PortalObjectPath("/portalA/pageA", PortalObjectPath.CANONICAL_FORMAT);
		templateIds.add(new PortalObjectId("", mainTemplatePath));
		return templateIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getProperties() {
		if (this.properties == null) {
			this.properties = new ConcurrentHashMap<>();
			// Initialize from template
			for (PortalObjectId templateID : getTemplatesID()) {
				PortalObject template = container.getObject(templateID);
				properties.putAll(template.getProperties());
			}

			// TODO : initialize from current CMS properties

		}
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDeclaredProperty(String name) {
		String value = null;
		value = this.getProperties().get(name);
		return value;

	}

}
