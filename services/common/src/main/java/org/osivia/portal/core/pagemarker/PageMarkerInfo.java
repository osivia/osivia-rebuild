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
 */
package org.osivia.portal.core.pagemarker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;


/**
 * Permet de stocker l'état d'une page et facilite notamment la gestion des
 * backs
 *
 * @author jeanseb
 *
 */
public class PageMarkerInfo implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;


    /** Breadcrumb. */
    private Breadcrumb breadcrumb;
    


    /** Dynamic pages. */
    private List<DynamicPageBean> dynamicPages;
    /** Dynamic windows. */
    private List<DynamicWindowBean> dynamicWindows;
    /** First tab index. */
    private Integer firstTab;
    /** Last timestamp. */
    private Long lastTimeStamp;
    /** Page identifier. */
    private PortalObjectId pageId;
    /** Page navigational state. */
    private PageNavigationalState pageNavigationalState;
    
    /** Window infos. */
    private Map<PortalObjectId, WindowStateMarkerInfo> windowInfos;

    /** Page marker. */
    private final String viewState;


    /**
     * Constructor.
     *
     * @param pageMarker page marker
     */
    public PageMarkerInfo(String viewState) {
        super();
        this.viewState = viewState;

    }




    /**
     * Getter for dynamicPages.
     *
     * @return the dynamicPages
     */
    public List<DynamicPageBean> getDynamicPages() {
        return this.dynamicPages;
    }

    /**
     * Setter for dynamicPages.
     *
     * @param dynamicPages the dynamicPages to set
     */
    public void setDynamicPages(List<DynamicPageBean> dynamicPages) {
        this.dynamicPages = dynamicPages;
    }

    /**
     * Getter for dynamicWindows.
     *
     * @return the dynamicWindows
     */
    public List<DynamicWindowBean> getDynamicWindows() {
        return this.dynamicWindows;
    }

    /**
     * Setter for dynamicWindows.
     *
     * @param dynamicWindows the dynamicWindows to set
     */
    public void setDynamicWindows(List<DynamicWindowBean> dynamicWindows) {
        this.dynamicWindows = dynamicWindows;
    }

    /**
     * Getter for firstTab.
     *
     * @return the firstTab
     */
    public Integer getFirstTab() {
        return this.firstTab;
    }

    /**
     * Setter for firstTab.
     *
     * @param firstTab the firstTab to set
     */
    public void setFirstTab(Integer firstTab) {
        this.firstTab = firstTab;
    }

    /**
     * Getter for lastTimeStamp.
     *
     * @return the lastTimeStamp
     */
    public Long getLastTimeStamp() {
        return this.lastTimeStamp;
    }

    /**
     * Setter for lastTimeStamp.
     *
     * @param lastTimeStamp the lastTimeStamp to set
     */
    public void setLastTimeStamp(Long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

 


    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public PortalObjectId getPageId() {
        return this.pageId;
    }

    /**
     * Setter for pageId.
     *
     * @param pageId the pageId to set
     */
    public void setPageId(PortalObjectId pageId) {
        this.pageId = pageId;
    }

    /**
     * Getter for pageNavigationalState.
     *
     * @return the pageNavigationalState
     */
    public PageNavigationalState getPageNavigationalState() {
        return this.pageNavigationalState;
    }

    /**
     * Setter for pageNavigationalState.
     *
     * @param pageNavigationalState the pageNavigationalState to set
     */
    public void setPageNavigationalState(PageNavigationalState pageNavigationalState) {
        this.pageNavigationalState = pageNavigationalState;
    }





   
  
    /**
     * Getter for windowInfos.
     *
     * @return the windowInfos
     */
    public Map<PortalObjectId, WindowStateMarkerInfo> getWindowInfos() {
        return this.windowInfos;
    }

    /**
     * Setter for windowInfos.
     *
     * @param windowInfos the windowInfos to set
     */
    public void setWindowInfos(Map<PortalObjectId, WindowStateMarkerInfo> windowInfos) {
        this.windowInfos = windowInfos;
    }

    /**
     * Getter for pageMarker.
     *
     * @return the pageMarker
     */
    public String getViewState() {
        return this.viewState;
    }

    
    /**
     * Gets the breadcrumb.
     *
     * @return the breadcrumb
     */
    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }




    
    /**
     * Sets the breadcrumb.
     *
     * @param breadcrumb the new breadcrumb
     */
    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }
    
}
