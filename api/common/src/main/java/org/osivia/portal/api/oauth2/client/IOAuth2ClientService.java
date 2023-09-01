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
package org.osivia.portal.api.oauth2.client;

import org.springframework.web.client.RestTemplate;

/**
 * OAuth2 client  service
 *
 * @author Jean-Sébastien Steux
 */
public interface IOAuth2ClientService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=OAuth2ClientService";
    
    /**
     * Get  portal generic RestTemplate
     * @return
     */
    RestTemplate getPortalClientCredentialRestTemplate();


  
}
