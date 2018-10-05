package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.jboss.portal.core.model.portal.PortalObject;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.theme.ThemeConstants;

public class PortalObjectContainerMock implements org.jboss.portal.core.model.portal.PortalObjectContainer {

	Map<PortalObjectId, PortalObject> nodes;
	ContainerContext containerContext = new ContainerContext();
	

	private static String PORTAL_A_NAME = "portalA";
	private static String PAGE_A_NAME = "pageA";
	private static String WINDOW_A_NAME = "winA";

	private ContentProviderRegistry contentProviderRegistry;

	public ContentProviderRegistry getContentProviderRegistry() {
		return contentProviderRegistry;
	}

	public void setContentProviderRegistry(ContentProviderRegistry contentProviderRegistry) {
		this.contentProviderRegistry = contentProviderRegistry;
	}

	@PostConstruct
	private void build() {

		nodes = new HashMap<>();
		
		PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock contextNode = new ObjectNodeMock(new PortalObjectId("", contextPath), "");
		ContextImplMock context = new ContextImplMock();
		context.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, PORTAL_A_NAME);
		context.setObjectNode(contextNode);
		nodes.put(context.getId(),context);

		PortalObjectPath portalAPath = new PortalObjectPath("/" + PORTAL_A_NAME, PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock portalANode = new ObjectNodeMock(new PortalObjectId("", portalAPath), PORTAL_A_NAME);
		PortalImplMock portalA = new PortalImplMock();
		portalA.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, PAGE_A_NAME);
		portalA.setObjectNode(portalANode);
		// states
		Set<WindowState> states = new HashSet<>();
		states.add(WindowState.NORMAL);
		states.add(WindowState.MAXIMIZED);
		portalA.setWindowStates(states);
		// modes
		Set<Mode> modes = new HashSet<>();
		modes.add(Mode.ADMIN);
		modes.add(Mode.VIEW);
		portalA.setModes(modes);
	
		
		portalANode.setObject(portalA);
		nodes.put(portalA.getId(), portalA);

		PortalObjectPath pageAPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + PAGE_A_NAME,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock pageANode = new ObjectNodeMock(new PortalObjectId("", pageAPath), PAGE_A_NAME);
		PageImplMock pageA = new PageImplMock();
		pageA.setObjectNode(pageANode);
		pageA.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT,"generic");
		pageANode.setObject(pageA);
		nodes.put(pageA.getId(), pageA);

		PortalObjectPath winAPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + PAGE_A_NAME + "/" + WINDOW_A_NAME,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock winANode = new ObjectNodeMock(new PortalObjectId("", winAPath), WINDOW_A_NAME);
		WindowImplMock winA = new WindowImplMock();
		winA.setContext(containerContext);
		winA.setURI("sample-instance");
		winA.setObjectNode(winANode);
		winANode.setObject(winA);
		nodes.put(winA.getId(), winA);

		// children

		
		Map contextChildren = new HashMap();
		contextChildren.put(PORTAL_A_NAME, portalANode);
		contextNode.setChildren(contextChildren);
		
		Map portalChildren = new HashMap();
		portalChildren.put(PAGE_A_NAME, pageANode);
		portalANode.setChildren(portalChildren);

		pageANode.setParent(portalANode);
		Map pageChildren = new HashMap();
		pageChildren.put(WINDOW_A_NAME, winANode);
		pageANode.setChildren(pageChildren);

		winANode.setParent(pageANode);
		
		
		

	}

	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		return nodes.get(id);
	}

	@Override
	public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		PortalObjectPath contextPath = new PortalObjectPath("/", PortalObjectPath.CANONICAL_FORMAT);
		PortalObjectId id = new PortalObjectId("", contextPath);
		return (Context) nodes.get(id);
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

	public class ContainerContext
	   {
	      /**
	       */
	      public PortalObjectContainerMock getContainer()
	      {
	         return PortalObjectContainerMock.this;
	      }

	      public ContentType getDefaultContentType()
	      {
	         return ContentType.PORTLET;
	      }

	      /**
	       */
	      public void destroyChild(ObjectNode node)
	      {
	      }

	      /**
	       * @throws DuplicatePortalObjectException 
	       */
	      public void createChild(ObjectNode node) throws DuplicatePortalObjectException
	      {
	      }

	      /**
	       */
	      public void updated(ObjectNode node)
	      {
	      }

	      /**
	       */
	      public ContentHandler getContentHandler(ContentType contentType)
	      {
	         ContentProvider contentProvider = contentProviderRegistry.getContentProvider(contentType);

	         //
	         if (contentProvider != null)
	         {
	            return contentProvider.getHandler();
	         }
	         else
	         {
	            return null;
	         }
	      }
	   }

}
