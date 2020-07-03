/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
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
package org.osivia.portal.cms.portlets.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;


/**
 * Navigation display item java-bean.
 */
public class NavigationDisplayItem {

    /** Item identifier. */
    private final String id;
    /** Display title. */
    private final String title;
    /** Absolute URL. */
    private final String url;
    /** Selected item indicator. */
    private final boolean selected;
    /** Current item indicator. */
    private final boolean current;

    /** CMS navigation item. */
    private final NavigationItem navItem;

    /** Children. */
    private final List<NavigationDisplayItem> children;

    /** Last selected indicator. */
    private boolean lastSelected;


    /**
     * Constructor.
     *
     * @param document Nuxeo document
     * @param link link
     * @param selected selected item indicator
     * @param current current item indicator
     * @param fetchedChildren fetched children indicator
     * @param navItem CMS navigation item
     */
    public NavigationDisplayItem(Document document, String url,boolean selected, boolean current, NavigationItem navItem) {
        super();
        this.id = document.getId().getInternalID();
        this.title = document.getTitle();
        this.url = url;
        this.selected = selected;
        this.current = current;

        this.navItem = navItem;

        this.children = new ArrayList<NavigationDisplayItem>();
    }


 

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NavigationDisplayItem [title=");
        builder.append(this.title);
        builder.append(", url=");
        builder.append(this.url);
        builder.append("]");
        return builder.toString();
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }


    /**
     * Getter for selected.
     *
     * @return the selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Getter for current.
     *
     * @return the current
     */
    public boolean isCurrent() {
        return this.current;
    }



    /**
     * Getter for navItem.
     *
     * @return the navItem
     */
    public NavigationItem getNavItem() {
        return this.navItem;
    }



    /**
     * Getter for children.
     *
     * @return the children
     */
    public List<NavigationDisplayItem> getChildren() {
        return this.children;
    }

    /**
     * Getter for lastSelected.
     * 
     * @return the lastSelected
     */
    public boolean isLastSelected() {
        return this.lastSelected;
    }

    /**
     * Setter for lastSelected.
     * 
     * @param lastSelected the lastSelected to set
     */
    public void setLastSelected(boolean lastSelected) {
        this.lastSelected = lastSelected;
    }

}
