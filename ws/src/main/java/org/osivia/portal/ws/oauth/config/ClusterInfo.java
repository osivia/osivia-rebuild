package org.osivia.portal.ws.oauth.config;


import javax.servlet.http.HttpServletRequest;

public class ClusterInfo {
    
    
    public ClusterInfo( HttpServletRequest req) {
        super();
        this.req = req;
    }

    public static ThreadLocal<ClusterInfo> instance = new ThreadLocal<ClusterInfo>();
    
    /** The request. */
    private final HttpServletRequest req;

    public HttpServletRequest getRequest()    {
        return req;
    }

  

}
