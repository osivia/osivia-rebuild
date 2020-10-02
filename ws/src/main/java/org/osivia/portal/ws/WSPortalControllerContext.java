package org.osivia.portal.ws;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osivia.portal.api.context.PortalControllerContext;


public class WSPortalControllerContext extends PortalControllerContext {

    HttpServletRequest request;
    HttpServletResponse response;    
    Principal principal;
    
    public WSPortalControllerContext(HttpServletRequest request, HttpServletResponse response) {
        super(null);
        this.request = request;
        this.response = response;
    }
    
    public WSPortalControllerContext(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        this(request, response);
        this.principal = principal;
    }

     
    /**
     * Getter for principal.
     * @return the principal
     */
    public Principal getPrincipal() {
        return principal;
    }
    
    /**
     * Setter for principal.
     * @param principal the principal to set
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

}
