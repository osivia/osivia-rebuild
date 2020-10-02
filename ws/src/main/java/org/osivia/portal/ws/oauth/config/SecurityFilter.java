package org.osivia.portal.ws.oauth.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.ws.portlet.WSUtilPortlet;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;





/**
 * Implements web security
 * - cors
 * - session fixation
 * 
 * @author Jean-Sébastien
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityFilter implements Filter {

    /** The logger. */
    protected static Log logger = LogFactory.getLog(SecurityFilter.class);
    
    private static Pattern accessTokenPattern = Pattern.compile("Bearer ([^_]*)_([^_]*)");

    /**
     * Extract session id from Bearer
     *
     * @param bearer the bearer
     * @return the string
     */
    private static String extractOAuth2Id(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        if (authorization != null) {
           Matcher m = accessTokenPattern.matcher(authorization);
            if (m.matches()) {
                int nb = m.groupCount();
                if (nb == 2)
                    return m.group(2);
            }
        }
        return null;
    }

    public FilterConfig filterConfig;


    public SecurityFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, content-type");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            

            
            // Default content type (web-services)
            String defaultContentType = "application/json";
            
             
            
            // /oauth/authorize must be accessed just one time par login
            // if not, the user must reauthenticate for security reason
            if( request.getRequestURI().endsWith("/oauth/authorize"))    {
                defaultContentType = "text/html";
                HttpSession session = ((HttpServletRequest) req).getSession(false);
                if( session != null)    {
                    if( session.getAttribute("displayAuthorize") != null)  {
                        if( !"true".equals(request.getParameter("user_oauth_approval")))    {
                            // Force login
                            ((HttpServletRequest) req).getSession(false).invalidate();
                            if( logger.isDebugEnabled())    {
                                logger.debug("invalidateSession ");
                            }
                            
                        }
                    }                    
                }
            }      
            
            // Debug headers
            
            if (logger.isDebugEnabled()) {
                Enumeration headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = (String) headerNames.nextElement();
                    String value = request.getHeader(name);
                    logger.debug("header " + name + "=" + value);
                }
            }
            
            
            
            checkSynchronizationSessionWithOAuth2(request);


            try {

                ClusterInfo.instance.set(new ClusterInfo(request));
                chain.doFilter(new CheckHeaderCompatibility(request, defaultContentType), res);
            } finally {
                ClusterInfo.instance.set(null);
            }
            
        }
        

     }

    /**
     * Synchronize Auth2 Bearer and local session portal session
     * 
     * The ACCESS_TOKEN contains the SESSIONID 
     *
     * @param request the request
     */
    
    
    public static void checkSynchronizationSessionWithOAuth2(HttpServletRequest request) {

        String sessionId = extractOAuth2Id(request);

        if (sessionId != null) {
            HttpSession session = request.getSession(false);
            

            if (logger.isDebugEnabled()) {
                if (session == null) {
                    logger.debug("before session is null");
                } else {
                    logger.debug("before session =" + session.getId());
                }
            }

            if (session == null)
                logger.warn("OAuth2 session is null");
            else {
                if (!session.getId().equals(sessionId)) {
                    logger.warn("OAuth2 session is not synchronized  " + session.getId() + " instead of" + sessionId);
                }
            }
        }

    }


    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        WSUtilPortlet.servletContext = filterConfig.getServletContext();
    }
}