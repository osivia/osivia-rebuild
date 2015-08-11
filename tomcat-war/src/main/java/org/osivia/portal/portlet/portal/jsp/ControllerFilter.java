package org.osivia.portal.portlet.portal.jsp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ControllerFilter implements Filter {

    /** Filter config. */
    private FilterConfig filterConfig;


    /**
     * Constructor.
     */
    public ControllerFilter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            ServletContext servletContext = this.filterConfig.getServletContext();

        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
