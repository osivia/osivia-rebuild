/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.jboss.portal.theme.impl.render.dynamic.response;

import org.jboss.portal.theme.impl.render.dynamic.DynaResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class UpdatePageStateResponse extends DynaResponse {

    /** . */
    private Map fragments;

    /** . */
    private Map<String, List<String>> regions;


    /** . */
    private String viewState;
    private String layout;
    private String restoreUrl;
    private String fullStateUrl;
    private String sessionCheck;
    private Set<PageResource> resources;
    
    
    
    public String getSessionCheck() {
        return sessionCheck;
    }



    
    public void setSessionCheck(String sessionCheck) {
        this.sessionCheck = sessionCheck;
    }



    public String getFullStateUrl() {
        return fullStateUrl;
    }


    
    public void setFullStateUrl(String fullStateUrl) {
        this.fullStateUrl = fullStateUrl;
    }


    public String getRestoreUrl() {
        return restoreUrl;
    }

    
    public void setRestoreUrl(String restoreUrl) {
        this.restoreUrl = restoreUrl;
    }




    public UpdatePageStateResponse(String viewState) {
        this.viewState = viewState;
        this.fragments = new HashMap();
        this.regions = new HashMap();
    }

    public String getViewState() {
        return viewState;
    }

    public void addFragment(String id, String fragment) {
        fragments.put(id, fragment);
    }

    public void addRegion(String name, List<String> windows) {
        regions.put(name, windows);
    }

    public Map getFragments() {
        return Collections.unmodifiableMap(fragments);
    }

    public Map<String, List<String>> getRegions() {
        return regions;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return layout;
    }


    public Set<PageResource> getResources() {
        return resources;
    }


    public void setResources(Set<PageResource> resources) {
        this.resources = resources;
    }


}
