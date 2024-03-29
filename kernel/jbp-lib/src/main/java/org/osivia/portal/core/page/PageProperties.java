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
package org.osivia.portal.core.page;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.core.cms.CMSConfigurationItem;



/**
 * Utility class associated to current page
 * 
 *  - get Window property at theme level
 *  - check if refreshing
 *
 * @author jeanseb
 *
 */
public class PageProperties {

    /** Region layouts. */
    private Set<CMSConfigurationItem> regionLayouts;
    
    /** Binary subject */
    private Subject binarySubject;


	
    public Subject getBinarySubject() {
        return binarySubject;
    }


    
    public void setBinarySubject(Subject binarySubject) {
        this.binarySubject = binarySubject;
    }


    public PageProperties() {
		super();
	}


	public PageProperties(Map<String, Map<String, String>> properties, Map<String,String> pageMap, Map<String,Object> requestCache) {
		super();
		this.windowProperties = properties;
		this.pageMap = pageMap;
		this.requestCache = requestCache;
	}

	protected static final Log logger = LogFactory.getLog(PageProperties.class);

	public static ThreadLocal<PageProperties> localProperties = new ThreadLocal<PageProperties>();

	public static PageProperties getProperties()	{
		if( localProperties.get() == null)	{
			localProperties.set(new PageProperties());
		}
	return localProperties.get();
	}

	// v2.0.6 : ajout rafraichissement utilisateur sur la requete
	private boolean refreshingPage = false;
	private Long checkingSpaceTS = null;
	private Long checkingSpaceBeginning = null;



	public boolean isRefreshingPage() {
		// TODO : a finir d'implémenter
//			return false;

		if( this.parent != null)
			return this.parent.isRefreshingPage();

		return this.refreshingPage;
	}
	
	   public boolean isCheckingSpaceContents() {
	       return getCheckingSpaceTs() != null;
	    }


	public void setRefreshingPage(boolean pageRefresh) {
		if( this.parent != null)
			this.parent.refreshingPage = pageRefresh;
		else
			this.refreshingPage = pageRefresh;
	}


    public void setCheckingSpaceTS(Long spaceTs) {
        if( this.parent != null)    {
            this.parent.checkingSpaceTS = spaceTs;
            this.parent.checkingSpaceBeginning = System.currentTimeMillis();
        }
        else    {
            this.checkingSpaceTS = spaceTs;
            this.checkingSpaceBeginning = System.currentTimeMillis();
        }
    }

    public Long getCheckingSpaceTs( )    {
        if( this.parent != null)
            return this.parent.getCheckingSpaceTs();

        return this.checkingSpaceTS;
    }

    public Long getCheckingSpaceBeginning( )    {
        if( this.parent != null)
            return this.parent.checkingSpaceBeginning;

        return this.checkingSpaceBeginning;
    }


    public  Map<String, String> getPagePropertiesMap()	{

		Map<String, String> pageProperties;
		if( this.parent == null)
			pageProperties =  this.pageMap;
		else
			pageProperties =  this.parent.pageMap;


		return pageProperties;

	}
    
    
    public  Map<String, Object> getPageRequestCache()  {

        Map<String, Object> requestCache;
        if( this.parent == null)
            requestCache =  this.requestCache;
        else
            requestCache =  this.parent.requestCache;


        return requestCache;

    }

	private Map<String, String> pageMap = null;
	
	private Map<String, Object> requestCache = null;

	private PageProperties parent;

	public static void createThreadContext( PageProperties parent){

		PageProperties newBean = new PageProperties( );
		newBean.parent = parent;
		localProperties.set(newBean);
	}

	// JSS v 1.0.10 : Map must be thread-safe

	private Map<String, Map<String, String>> windowProperties = null;
	//private Map<String, Map<String, String>> windowProperties = new HashMap<String, Map<String,String>>();

	public String getWindowProperty( String windowId, String propertyName){
		if( this.parent != null)
			return this.parent.getWindowProperty(windowId, propertyName);
		if( this.windowProperties.get(windowId) != null)	{
			return  this.windowProperties.get(windowId).get(propertyName);
		}	else
			return null;
	}

	public void setWindowProperty( String windowId, String propertyName, String propertyValue){


		if( this.parent != null)	{

			this.parent.setWindowProperty(windowId, propertyName, propertyValue);

			//if( logger.isDebugEnabled())
			//	logger.debug("windowId: " +windowId+ " name:" + propertyName + " value:" +propertyValue);

			return;
		}

		if( this.windowProperties.get(windowId) == null)	{
			// JSS v 1.0.10 : Map must be thread-safe
			//windowProperties.put(windowId, new HashMap<String, String>());
			this.windowProperties.put(windowId, new Hashtable<String, String>());

		}

		if( propertyValue != null)
			this.windowProperties.get(windowId).put( propertyName, propertyValue);
	}


	// Pour les rendersets
	private String currentWindowId;

	public String getCurrentWindowId() {
		return this.currentWindowId;
	}

	public void setCurrentWindowId(String currentWindowId) {
		this.currentWindowId = currentWindowId;
	}


    public static void init() {
		localProperties.set(new PageProperties(new Hashtable<String, Map<String,String>>(), new Hashtable<String, String>(), new Hashtable<String, Object>()));

	}
    
    public static void remove() {
		localProperties.remove();

	}


    /**
     * Getter for regionLayouts.
     *
     * @return the regionLayouts
     */
    public Set<CMSConfigurationItem> getRegionLayouts() {
        return this.regionLayouts;
    }

    /**
     * Setter for regionLayouts.
     *
     * @param regionLayouts the regionLayouts to set
     */
    public void setRegionLayouts(Set<CMSConfigurationItem> regionLayouts) {
        this.regionLayouts = regionLayouts;
    }

}
