package org.osivia.portal.portlet.portal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorHandlingFilter implements Filter {

    /**
     * Constructor.
     */
    public ErrorHandlingFilter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Do nothing
    }

}
