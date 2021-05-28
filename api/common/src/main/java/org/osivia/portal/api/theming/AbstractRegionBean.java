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
package org.osivia.portal.api.theming;

/**
 * Region java bean abstract super-class.
 *
 * @author Cédric Krommenhoek
 */
public abstract class AbstractRegionBean {

    /** Region name. */
    private final String name;

    /** Region context path, may be null. */
    private String contextPath;


    /**
     * Constructor.
     *
     * @param name region name
     */
    public AbstractRegionBean(String name) {
        super();
        this.name = name;
    }


    /**
     * Check if current region is customizable.
     * @return true if current region is customizable
     */
    public abstract boolean isCustomizable();


    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for contextPath.
     * 
     * @return the contextPath
     */
    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * Setter for contextPath.
     * 
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

}
