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

import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.impl.URLParameterConstants;
import org.osivia.portal.kernel.common.portal.PagePortletControllerContext;
import org.osivia.portal.kernel.common.portal.PortalPrepareResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Controller filter.
 *
 * @author Cédric Krommenhoek
 * @see Filter
 */
@Component
public class ControllerFilter implements Filter {

    /** Filter config. */
    private FilterConfig filterConfig;
    /** Spring web application context. */
    private WebApplicationContext webApplicationContext;
    /** Portlet invoker. */
    private PortletInvoker invoker;


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
        this.webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        this.invoker = this.webApplicationContext.getBean("ConsumerPortletInvoker", PortletInvoker.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            this.doFilter(httpServletRequest, httpServletResponse, chain);
        } catch (PortletInvokerException e) {
            throw new ServletException(e);
        }
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
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException,
            PortletInvokerException {
        // Introspection phase
        PortalPrepareResponse prepareResponse = new PortalPrepareResponse(request, response);
        chain.doFilter(request, prepareResponse);

        // Populated context
        PagePortletControllerContext context = new PagePortletControllerContext(request, response, this.invoker, prepareResponse);

        // Invocation type
        String type = request.getParameter(URLParameterConstants.TYPE);


        chain.doFilter(request, response); // FIXME
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
