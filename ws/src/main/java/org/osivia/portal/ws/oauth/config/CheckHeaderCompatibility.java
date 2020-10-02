package org.osivia.portal.ws.oauth.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * the Accept : application/json must be specified in order to reply correct JSON format
 * 
 * It's due to oAuth2 librairies  ...
 * for exemple, the DefaultOAuth2ExceptionRenderer.class won't reply an applicative code
 */
public class CheckHeaderCompatibility extends HttpServletRequestWrapper {

    /** The logger. */
    protected static Log logger = LogFactory.getLog(CheckHeaderCompatibility.class);
    
    private String defaultContentType = null;
    

    public CheckHeaderCompatibility(HttpServletRequest request, String defaultContentType) {
        super(request);
        this.defaultContentType = defaultContentType;
    }
    
    /**
     * The default behavior of this method is to return getHeaders(String name)
     * on the wrapped request object.
     */
    public Enumeration getHeaders(String name) {
        boolean mustTransform = false;        
        if ("Accept".equals(name)) {

            Enumeration e = super.getHeaders(name);


            if( e != null && e.hasMoreElements()) {
                String value = (String) e.nextElement();
                if( "*/*".equals(value) && !e.hasMoreElements())    {
                    mustTransform = true;
                }
             }  else    {
                 mustTransform = true;
             }
         }
        
        
        if( mustTransform) {
            if( logger.isDebugEnabled())    {
                if( defaultContentType != null)
                    logger.debug("add "+defaultContentType+" to header");
            }
            
            List<String> values = new ArrayList<String>();
            if(defaultContentType != null) {
                values.add(defaultContentType);
                return Collections.enumeration(values);
            }
        }          

        return super.getHeaders(name);
    } 

    /**
     * The default behavior of this method is to return getHeader(String name)
     * on the wrapped request object.
     */
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if ("Accept".equals(name)) {
            if (value == null || ("*/*".equals(value))) {
                if( defaultContentType != null)
                    value = defaultContentType;
            }
        }
        return value;
    }


    /**
     * The default behavior of this method is to return getHeaderNames()
     * on the wrapped request object.
     */

    public Enumeration getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        if (!names.contains("Accept"))
            names.add("Accept");
        return Collections.enumeration(names);
    }


}