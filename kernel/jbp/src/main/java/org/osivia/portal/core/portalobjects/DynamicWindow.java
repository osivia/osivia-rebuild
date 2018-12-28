/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.portalobjects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.jpb.services.WindowImplMock;



@SuppressWarnings("unchecked")
public abstract class DynamicWindow extends WindowImplMock {


	protected String name;
	protected Page page;

	protected Map<String, String> properties ;
	protected DynamicPortalObjectContainer dynamicContainer;
	
	protected PortalObjectId id = null;
	
	
	protected String uniqueID = null;
	
	private static Log logger = LogFactory.getLog(DynamicWindow.class);
	
	private DynamicWindowBean dynamicWindowBean = null;
	
	public DynamicWindowBean getDynamicWindowBean() {
		return dynamicWindowBean;
	}

	public void setDynamicWindowBean(DynamicWindowBean dynamicWindowBean) {
		this.dynamicWindowBean = dynamicWindowBean;
	}

	/**
	 * Non unique au niveau dynamique
	 * 
	 */
	public String getDynamicUniqueID() {
		if( uniqueID == null)	{
			uniqueID = "";
			
			if( page != null && page instanceof DynamicTemplatePage)	{
				String pageUID = ((DynamicTemplatePage) page).getPageBean().getUniqueID();
				if( pageUID != null)
					uniqueID += pageUID;
			}
			
			if( dynamicWindowBean != null)	{
				String windowUID = dynamicWindowBean.getUniqueID();
				if( windowUID != null)
					uniqueID += windowUID;
			}	else	{
				if( name != null)
					uniqueID += "_n" + name;
			}
		}

		return uniqueID;
	}
	

	protected boolean sessionWindow  = false;

	
	public boolean isSessionWindow() {
		return sessionWindow;
	}

	public void setSessionWindow(boolean sessionWindow) {
		this.sessionWindow = sessionWindow;
	}
	
	
	@Override
	public PortalObject getParent() {
		 return getPage();
	}
	
	public DynamicWindow( )	{
		super();
	}
	

	 public boolean equals(Object obj)
	   {
	     if ( obj instanceof PortalObjectImpl)
	      {
	         PortalObjectImpl that = (PortalObjectImpl)obj;
	         return getId().equals(that.getId());
	      }
	      return false;
	   }
	 

	public void setLocalProperty(String name, String value) {
		 getProperties().put(name, value);
			
	}
	
	@Override
	public PortalObjectId getId() {
		return id;

	}
	
	public String getName()	{
		return name;
	}

	
	public DynamicWindow( DynamicPage page, String name, Object context, DynamicPortalObjectContainer dynamicContainer, DynamicWindowBean dynaBean)	{
		super();
		
		this.dynamicContainer = dynamicContainer;
		this.page = page;
		this.name = name;
		id = new PortalObjectId("", new PortalObjectPath( page.getId().getPath().toString().concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));
		properties = new HashMap<String, String>();
		
		setContext(context);
		
		setSessionWindow(true);
		
		dynamicWindowBean = dynaBean;
		
		// Optimisation  : ajout cache
		
		DynamicPortalObjectContainer.addToCache(id, this);
	}
	

	
	

}
