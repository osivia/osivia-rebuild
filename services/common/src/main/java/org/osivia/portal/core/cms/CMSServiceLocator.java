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
package org.osivia.portal.core.cms;

import java.util.Hashtable;
import java.util.Map;

/**
 * registry for CMS Service
 *
 */
public class CMSServiceLocator implements ICMSServiceLocator {

    ICMSService service;

    Map<String, ICMSService> cmsServices = new Hashtable<>();

    public void register(ICMSService service) {
        this.service = service;
        cmsServices.put("nx", service);
    }


    public void register(String repositoryName, ICMSService service) {
        this.service = service;
        cmsServices.put(repositoryName, service);
    }

    public ICMSService getCMSService() {
        return cmsServices.get("nx");
    }


    public ICMSService getCMSService(String repositoryName) {
        if (!"nx".equals(repositoryName))
            return cmsServices.get(repositoryName);
        else
            return getCMSService();
    }
}
