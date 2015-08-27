package org.osivia.portal.kernel.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.portlet.PortletInvokerException;

/**
 * Error handling filter.
 *
 * @author Cédric Krommenhoek
 * @see Filter
 */
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
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        this.doFilter(httpServletRequest, httpServletResponse, chain);
    }

    /**
     * Filter.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param chain filter chain
     * @throws IOException
     * @throws ServletException
     * @throws PortletInvokerException
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ServletException e) {
            String message = e.getMessage();
            if ("403".equals(message)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else if ("404".equals(message)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                throw e;
            }
        } catch (IOException e) {
            throw e;
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
