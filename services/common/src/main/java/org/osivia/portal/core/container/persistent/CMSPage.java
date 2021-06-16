package org.osivia.portal.core.container.persistent;

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
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer.ContainerContext;


public  class  CMSPage extends PageImplBase {

	/** Local properties. */
	protected Map<String, String> declaredProperties = new ConcurrentHashMap<String, String>();
	protected Map<String, String> properties = null;
	private PortalObjectPath pagePath;
    private String pageName;
	


    ContainerContext containerContext;
	StaticPortalObjectContainer container;
	private ObjectNodeImplBase parentNode;
	
	private CMSPageFactory factory;
	private List<String> inheritedRegions;
	
	private String cmsID;
	
	List<PortalObjectId> templatesId = null;

	
	public String getCmsID() {
		return cmsID;
	}

	public void setCmsID(String cmsID) {
		this.cmsID = cmsID;
	}

    public PortalObjectPath getPagePath() {
        return pagePath;
    }


    
    public String getPageName() {
        return pageName;
    }

	public CMSPage(StaticPortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId, Map<String, String> pageProperties, List<String> inheritedRegions, CMSPageFactory factory)  throws CMSException{
		super();


		this.factory = factory;
		
		PortalObjectId parentId = new PortalObjectId(pageId.getNamespace(), pageId.getPath().getParent());
		PortalObjectImplBase parent = (PortalObjectImplBase) container.getObject(parentId);
		

		this.containerContext = containerContext;
		this.container = container;
		
		this.parentNode = parent.getObjectNode();
		this.pagePath = new PortalObjectPath(pageId.getPath().toString(), PortalObjectPath.CANONICAL_FORMAT);
		this.pageName = pagePath.getLastComponentName();

		this.inheritedRegions = inheritedRegions;

		ObjectNodeImplBase pageNode = new ObjectNodeImplBase(pageId, pageName, containerContext);
		this.setObjectNode(pageNode);

		pageNode.setObject(this);
		container.getContextNodes().put(this.getId(), this);

		// parent relations
		pageNode.setParent(parentNode);
		parentNode.getChildren().put(pageName, pageNode);

		// children relations
		pageNode.setChildren(computeWindows());

		// TODO init form CMS
		cmsID = "id_"+pageName;

		for (String key : pageProperties.keySet()) {
			setDeclaredProperty(key, pageProperties.get(key));
		}
		

		setDeclaredProperty("osivia.creationTs", ""+ System.currentTimeMillis());
		

	}


	private Map<String, PortalObject> computeWindows() throws CMSException {

		Map windows = new ConcurrentHashMap<>();

		/* Inherited Windows */

		PortalObject parent = parentNode.getObject();

		if (parent instanceof Page) {
			Collection<PortalObject> parentWindows = parent.getChildren(PortalObject.WINDOW_MASK);
			for (PortalObject parentWindow : parentWindows) {
				WindowImplBase parentWindowMock = (WindowImplBase) parentWindow;
				String region = parentWindow.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
				if( inheritedRegions.contains(region))  {
    				ObjectNodeImplBase dupWinNode = duplicateWindow(parentWindowMock, false);
    				if (dupWinNode != null)
    					windows.put(parentWindow.getName(), dupWinNode);
				}
			}
		}

		/* Template Windows */

		for (PortalObjectId templateID : getTemplatesId()) {

            PortalObject template = container.getObject(templateID);

            if (template instanceof Page) {
                Collection<PortalObject> tmplWindows = template.getChildren(PortalObject.WINDOW_MASK);

                for (PortalObject tmplWindow : tmplWindows) {

                    if (tmplWindow instanceof WindowImplBase) {
                        ObjectNodeImplBase dupWinNode = duplicateWindow(tmplWindow, true);
                        if (dupWinNode != null)

                            windows.put(tmplWindow.getName(), dupWinNode);
                    }

                }
            }
		}

		/* CMS Windows */

		factory.createCMSWindows(this, windows);

		return windows;

	}


	
  
    protected void addWindow(Map windows, ModuleRef module, int order) {

        PortalObjectPath winPath = new PortalObjectPath(
                pagePath.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + module.getWindowName(),
                PortalObjectPath.CANONICAL_FORMAT);

        ObjectNodeImplBase winNode = new ObjectNodeImplBase(new PortalObjectId("", winPath), module.getWindowName(),
                containerContext);
        WindowImplBase win = new WindowImplBase();
        win.setContext(containerContext);
        win.setURI(module.getModuleId());
        win.setObjectNode(winNode);
        win.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION, module.getRegion());
        win.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(order));
        
        for( String propName : module.getProperties().keySet()) {
            win.setDeclaredProperty(propName, module.getProperties().get(propName));
        }
        
        winNode.setObject(win);
        container.getContextNodes().put(win.getId(), win);
        
        winNode.setParent(getObjectNode());
        
        
        windows.put(module.getWindowName(), winNode);
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
		container.getContextNodes().put(dupWin.getId(), dupWin);
		return dupWinNode;
	}

	@Override
	public Map<String, String> getDeclaredProperties() {
		return declaredProperties;
	}


	protected List<PortalObjectId> getTemplatesId() throws CMSException    {
	    if( templatesId == null) {
    	    templatesId = factory.getDeclaredTemplatesID( this);
    	    
    	    if( templatesId.isEmpty()) {
    	        PortalObject parent = parentNode.getObject();
    	        if( parent instanceof CMSPage) 
    	            templatesId = ((CMSPage) parent).getTemplatesId();
    	       
    	    }
    	    
//    	    if( templatesId == null || templatesId.isEmpty())  {
//    	        throw new RuntimeException("No template found for " + pagePath);
//    	    }
	    }
	    return templatesId;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getProperties() {
		if (this.properties == null) {
			this.properties = new ConcurrentHashMap<>();

			
			// Add parent
			properties.putAll(parentNode.getObject().getProperties());

            // Add from template
            try {
                for (PortalObjectId templateID : getTemplatesId()) {
                    PortalObject template = container.getObject(templateID);
                    properties.putAll(template.getDeclaredProperties());
                }
            } catch (CMSException | IllegalArgumentException e) {
                throw new RuntimeException( e);
            }  
			
	         // Add current level
            properties.putAll(this.getDeclaredProperties());
            


		}
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDeclaredProperty(String name) {
		String value = null;
		value = this.getDeclaredProperties().get(name);
		return value;

	}
	
	   /**
     * {@inheritDoc}
     */
    @Override
    public void setDeclaredProperty(String name, String value) {
        this.getDeclaredProperties().put(name, value);

    }

}
