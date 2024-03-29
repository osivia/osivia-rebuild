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
package org.osivia.portal.api.page;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * The Class PageParametersEncoder.
 * 
 * Encode portal page properties into url parameters
 * 
 * @author Jean-Sébastien Steux
 */
public class PageParametersEncoder {
	
	/**
	 * Encode properties.
	 *
	 * @param props the props
	 * @return the string
	 */
	public static String encodeProperties( Map <String, List<String>> props)	{
		
		try	{
		String url = "";
		
		for( String name : props.keySet())	{
			
			List<String> values = props.get(name);
			
			if( values != null && values.size() > 0) {

					if (url.length() > 0)
						url += "&";

					url += encodeValue(name);

					// Encode values
					String encodedValues = "";
					for (String value : values) {
						if (encodedValues.length() > 0)
							encodedValues += ",";
						encodedValues += encodeValue(value);
					}

					url += "=" + encodedValues;
			}
		}
		
		return URLEncoder.encode(url , "UTF-8");
		
		} catch( Exception e)	{
			throw new RuntimeException( e);
		}
	}
	
	/**
	 * Decode properties.
	 *
	 * @param urlParams the url params
	 * @return the map
	 */
	public static Map<String,List<String>> decodeProperties( String urlParams)	{
		try	{
			
		Map<String, List<String>> params = new HashMap<String, List<String>>();		
		
		if( urlParams == null || urlParams.length() == 0)
				return params;
			
		
		String decodedParam = URLDecoder.decode(urlParams , "UTF-8");
		
		
		String[] tabParams = decodedParam.split("&");	
		
		for(int i=0; i< tabParams.length; i++){
			String[] valParams = tabParams[i].split("=");
			
			if( valParams.length == 1)
			    params.put(valParams[0], new ArrayList<>());
			else if( valParams.length == 2)  {
			    String[] values = valParams[1].split(",");
			    List<String> decodedValues = new ArrayList<String>();
			    for( int j=0; j< values.length; j++)
			        decodedValues.add( decodeValue( values[ j]));
			    params.put(valParams[0], decodedValues);
			}    else
			    throw new IllegalArgumentException("Bad parameter format");			    
		}
		
		return params;
		
		} catch( Exception e)	{
			throw new RuntimeException( e);
		}

	}

	/** The esc equals. */
	private static String ESC_EQUALS = "##EQUALS##";
	
	/** The esc amp. */
	private static String ESC_AMP = "##AMP##";	
	
	/** The esc comma. */
	private static String ESC_COMMA = "##COMMA##";
	
	/**
	 * Encode value.
	 *
	 * @param origValue the orig value
	 * @return the string
	 */
	private static String encodeValue( String origValue)	{
		
		String res = origValue.replaceAll("=", ESC_EQUALS);
		res = res.replaceAll("&", ESC_AMP);
		res = res.replaceAll(",",  ESC_COMMA);		
		
		return res;
				
		
	}
	
	/**
	 * Decode value.
	 *
	 * @param origValue the orig value
	 * @return the string
	 */
	private static String decodeValue( String origValue)	{
		
		String res = origValue.replaceAll( ESC_EQUALS, "=");
		res = res.replaceAll(ESC_AMP, "&");
		res = res.replaceAll(ESC_COMMA, ",");		
		
		return res;
				
		
	}


}
