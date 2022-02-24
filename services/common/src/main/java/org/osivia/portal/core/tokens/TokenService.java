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
package org.osivia.portal.core.tokens;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.ha.ClusterMap;
import org.osivia.portal.api.ha.IHAService;

import org.osivia.portal.api.tokens.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author loic
 *
 */
@Service(ITokenService.MBEAN_NAME)
public class TokenService implements ITokenService {

    /** Logger. */
    private static final Log logger = LogFactory.getLog(TokenService.class);
    
    @Autowired
    private IHAService haService;

     
    private String sync = new String("SYNC");
    private int modulo = 1;



    /**
     *  Timeout for token validity (10 minutes)
     */
    private long TOKEN_TIMEOUT = 600000L;

    /* (non-Javadoc)
     * @see org.osivia.portal.api.tokens.ITokenService#generateToken(java.util.Map)
     */
    
    public String generateToken(Map<String, String> attributes) {

        if( logger.isDebugEnabled())
            logger.debug("generateToken");
        
        String tokenKey;
        
        synchronized (sync)   {
            // Avoid doublon in same ms
            modulo = ( modulo + 1 ) % 100;
            tokenKey = new String(Base64.encodeBase64(("key_" + System.currentTimeMillis() + "_" + modulo).getBytes()));            
        }

        
        // Hashmap is ready for serialization
        HashMap<String, String> serMap = new HashMap<>(attributes);


        try {
            haService.shareMap(tokenKey, serMap);
            return tokenKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }





    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.tokens.ITokenService#validateToken(java.lang.String, boolean)
     */
    public Map<String, String> validateToken(String tokenKey, boolean renew) {

        if (logger.isDebugEnabled())
            logger.debug("validateToken" + tokenKey);

        Map<String, String> attributes = null;

        try {
            ClusterMap wrappedObject = haService.getSharedMap(tokenKey);

            if (wrappedObject != null) {

                long ts = System.currentTimeMillis();
                if (ts - wrappedObject.getReceptionTs() < TOKEN_TIMEOUT) {
                    attributes = wrappedObject.getObject();
                    if (!renew) {
                        haService.unshareMap(tokenKey);
                    }
                }

            }
            return attributes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.tokens.ITokenService#validateToken(java.lang.String)
     */
    public Map<String, String> validateToken(String tokenKey) {
        return validateToken(tokenKey, false);
    }


}
