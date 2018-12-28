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
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;

public class TemplatePortalObjectContainer implements org.jboss.portal.core.model.portal.PortalObjectContainer {

	Map<PortalObjectId, PortalObject> nodes;
	ContainerContext containerContext = new ContainerContext();
	

	private static String PORTAL_A_NAME = "portalA";
	private static String PAGE_A_NAME = "pageA";	
	private static String DEFAULT_PAGE_NAME = PAGE_A_NAME;	

	private static String WINDOW_A_NAME = "winA";
	private static String WINDOW_B_NAME = "winB";
	private static String WINDOW_C_NAME = "winC";
	
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
		ObjectNodeMock contextNode = new ObjectNodeMock(new PortalObjectId("", contextPath), "", containerContext);
		ContextImplMock context = new ContextImplMock();
		context.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, PORTAL_A_NAME);
		context.setObjectNode(contextNode);
		nodes.put(context.getId(),context);

		PortalObjectPath portalAPath = new PortalObjectPath("/" + PORTAL_A_NAME, PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock portalANode = new ObjectNodeMock(new PortalObjectId("", portalAPath), PORTAL_A_NAME, containerContext);
		PortalImplMock portalA = new PortalImplMock();
		portalA.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, DEFAULT_PAGE_NAME);
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



		// Context Children
		Map contextChildren = new HashMap();
		contextChildren.put(PORTAL_A_NAME, portalANode);
		contextNode.setChildren(contextChildren);
		
		Map portalChildren = new HashMap();		
		
		
		// Page A
		

		String pageAName = PAGE_A_NAME;
		Map<String, String> pageProperties = new HashMap<>();
		
		pageProperties.put(ThemeConstants.PORTAL_PROP_LAYOUT,"generic-2cols");
		pageProperties.put(ThemeConstants.PORTAL_PROP_THEME,"generic");	
				
		portalChildren.put(pageAName, createPage(contextNode, portalANode, pageAName, pageProperties));

		
		
		// Page B
		
		String pageBName = PAGE_A_NAME + "-ajax";
		pageProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED,"true");
				
		portalChildren.put(pageBName, createPage(contextNode, portalANode, pageBName, pageProperties));

	
		// Children
		portalANode.setChildren(portalChildren);

	}

	
	
	private ObjectNodeMock createPage(ObjectNodeMock contextNode, ObjectNodeMock portalANode, String pageName, Map<String, String> pageProperties) {
		
		
		PortalObjectPath pageAPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + pageName,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock pageANode = new ObjectNodeMock(new PortalObjectId("", pageAPath), pageName, containerContext);
		PageImplMock pageA = new PageImplMock();
		pageA.setObjectNode(pageANode);
		
		for(String key : pageProperties.keySet()) {
			pageA.setDeclaredProperty(key, pageProperties.get(key));
		}
		
		
		pageANode.setObject(pageA);
		nodes.put(pageA.getId(), pageA);

		PortalObjectPath winAPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + pageName + "/" + WINDOW_A_NAME,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock winANode = new ObjectNodeMock(new PortalObjectId("", winAPath), WINDOW_A_NAME, containerContext);
		WindowImplMock winA = new WindowImplMock();
		winA.setContext(containerContext);
		winA.setURI("SampleInstance");
		winA.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION,"col-1");
		winA.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER,"0");		
		winA.setObjectNode(winANode);
		winANode.setObject(winA);
		nodes.put(winA.getId(), winA);
		
		
		
		
		PortalObjectPath winBPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + pageName + "/" + WINDOW_B_NAME,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock winBNode = new ObjectNodeMock(new PortalObjectId("", winBPath), WINDOW_B_NAME, containerContext);
		WindowImplMock winB = new WindowImplMock();
		winB.setContext(containerContext);
		winB.setURI("SampleRemote");
		winB.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION,"col-2");
		winB.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER,"0");				
		winB.setObjectNode(winBNode);
		winBNode.setObject(winB);
		nodes.put(winB.getId(), winB);
		
		
		PortalObjectPath winCPath = new PortalObjectPath("/" + PORTAL_A_NAME + "/" + pageName + "/" + WINDOW_C_NAME,
				PortalObjectPath.CANONICAL_FORMAT);
		ObjectNodeMock winCNode = new ObjectNodeMock(new PortalObjectId("", winCPath), WINDOW_C_NAME, containerContext);
		WindowImplMock winC = new WindowImplMock();
		winC.setContext(containerContext);
		winC.setURI("SampleInstance");
		winC.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION,"col-2");
		winC.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER,"1");			
		winC.setObjectNode(winCNode);
		winCNode.setObject(winC);
		nodes.put(winC.getId(), winC);
		

		// children

		pageANode.setParent(portalANode);
		Map pageChildren = new HashMap();
		pageChildren.put(WINDOW_A_NAME, winANode);
		pageChildren.put(WINDOW_B_NAME, winBNode);
		pageChildren.put(WINDOW_C_NAME, winCNode);		
		pageANode.setChildren(pageChildren);

		winANode.setParent(pageANode);
		winBNode.setParent(pageANode);		
		winCNode.setParent(pageANode);
		
		
		return pageANode;
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
	      public TemplatePortalObjectContainer getContainer()
	      {
	         return TemplatePortalObjectContainer.this;
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
