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
package org.osivia.portal.core.urls;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WindowPropertiesEncoder {
    
    /*
    public static void main(String args[])  {
        String callckackurl="/portal/auth/sites/ID_SITE_A/_dyn_c3BhY2VfcG9ydGFsQV9fY3R4X19sb2NhbGVfZnI%3D.Y29udGVudDp0ZW1wbGF0ZXM6REVGQVVMVF9URU1QTEFURVNfUFVCTElTSA%3D%3D.b3NpdmlhLmNvbnRlbnRJZCUzRHRlbXBsYXRlcyUzQURFRkFVTFRfVEVNUExBVEVTX1BVQkxJU0glMjZvc2l2aWEuY29udGVudC5wcmV2aWV3JTNEZmFsc2UlMjZvc2l2aWEucGFnZVR5cGUlM0R0ZW1wbGF0ZSUyNm9zaXZpYS5pbml0aWFsV2luZG93UmVnaW9uJTNEY29sLTElMjZvc2l2aWEuaW5pdGlhbFdpbmRvd1Byb3BzJTNEb3NpdmlhLmNvbnRlbnQucHJldmlldyUyNTNEdHJ1ZSUyNTI2b3NpdmlhLmFqYXhMaW5rJTI1M0QxJTI1MjZvc2l2aWEuc3BhY2UuaWQlMjUzRHNpdGVzJTI1M0FJRF9TSVRFX0ElMjUyNm9zaXZpYS5oaWRlVGl0bGUlMjUzRDElMjUyNm9zaXZpYS5oaWRlRGVjb3JhdG9ycyUyNTNEMSUyNTI2dGhlbWUuZHluYS5wYXJ0aWFsX3JlZnJlc2hfZW5hYmxlZCUyNTNEdHJ1ZSUyNTI2b3NpdmlhLmNvbnRlbnQubG9jYWxlJTI1M0RmciUyNTI2b3NpdmlhLnRpdGxlJTI1M0RQcm9wcmklMjVDMyUyNUE5dCUyNUMzJTI1QTlzJTJCZGUlMkJsJTI1Mjdlc3BhY2UlMjZvc2l2aWEubmF2aWdhdGlvbklkJTNEdGVtcGxhdGVzJTNBREVGQVVMVF9URU1QTEFURVNfUFVCTElTSCUyNm9zaXZpYS5jb250ZW50LmxvY2FsZSUzRGZyJTI2b3NpdmlhLmluaXRpYWxXaW5kb3dJbnN0YW5jZSUzREVkaXRpb25Nb2RpZnlTcGFjZUluc3RhbmNlJTI2b3NpdmlhLnNwYWNlSWQlM0R0ZW1wbGF0ZXMlM0Fwb3J0YWxB.X19OX18%3D.ZnIlM0RUZW1wbGF0ZStzcGFjZS5wb3J0YWxB/content?javax.portlet.action=addProfile&action=1";
        
        Map<String, String> params = new HashMap<>();
        params.put("url", callckackurl);
        
        String encoded = encodeProperties(params);
        Map<String, String> result = decodeProperties(encoded);
        
        String res = result.get("url");
        if( !res.equals(callckackurl)) 
            System.out.println("ko");
       
        
    }*/

	public static String encodeProperties(Map<String, String> props) {

		try {
			String url = "";

			for (String name : props.keySet()) {
				if (props.get(name) != null) {
					if (url.length() > 0)
						url += "&&&";
					url += encodeValue(name) + "===" + encodeValue(props.get(name));
				}
			}

			return URLEncoder.encode(url, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> decodeProperties(String urlParams) {
		try {

			Map<String, String> params = new HashMap<String, String>();

			if (urlParams == null || urlParams.length() == 0)
				return params;

			String decodedParam = URLDecoder.decode(urlParams, "UTF-8");

			String[] tabParams = decodedParam.split("&&&");

			for (int i = 0; i < tabParams.length; i++) {
				String[] valParams = tabParams[i].split("===");
				
				if (valParams.length != 1 && valParams.length != 2)
					throw new IllegalArgumentException("Bad parameter format");
			
				String value = "";
				
				if( valParams.length == 2)
					value = valParams[1];
				

				params.put(valParams[0], value);
			}

			return params;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static String encodeValue(String origValue) {

		if (origValue.contains("==="))
			throw new RuntimeException("Bad parameter format");
		
		if (origValue.contains("&&&"))
			throw new RuntimeException("Bad parameter format");

		return origValue;

	}

}
