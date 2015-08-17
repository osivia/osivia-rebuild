package org.osivia.portal.kernel.filter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.io.SerializationFilter;
import org.jboss.portal.common.util.Base64;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.URLFormat;
import org.jboss.portal.portlet.controller.PortletController;
import org.jboss.portal.portlet.controller.impl.ControllerRequestFactory;
import org.jboss.portal.portlet.controller.impl.ControllerRequestParameterNames;
import org.jboss.portal.portlet.controller.impl.PortletURLRenderer;
import org.jboss.portal.portlet.controller.impl.URLParameterConstants;
import org.jboss.portal.portlet.controller.request.ControllerRequest;
import org.jboss.portal.portlet.controller.request.PortletActionRequest;
import org.jboss.portal.portlet.controller.response.ControllerResponse;
import org.jboss.portal.portlet.controller.response.PageUpdateResponse;
import org.jboss.portal.portlet.controller.response.PortletResponse;
import org.jboss.portal.portlet.controller.response.ResourceResponse;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalState;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalStateSerialization;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.ErrorResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UnavailableResponse;
import org.jboss.portal.web.util.RequestDecoder;
import org.osivia.portal.kernel.portal.PagePortletControllerContext;
import org.osivia.portal.kernel.portal.PortalPrepareResponse;
import org.osivia.portal.kernel.portal.PortalRenderResponse;

/**
 * Controller filter.
 *
 * @author Cédric Krommenhoek
 * @see Filter
 */
public class ControllerFilter implements Filter {

    /** Filter config. */
    private FilterConfig filterConfig;
    /** Redirect after action indicator. */
    private boolean redirectAfterAction;


    /**
     * Constructor.
     */
    public ControllerFilter() {
        super();

        this.redirectAfterAction = false;
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
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        try {
            this.doFilter(servletRequest, servletResponse, chain);
        } catch (PortletInvokerException e) {
            throw new ServletException(e);
        }
    }


    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException,
            PortletInvokerException {
        ServletContext servletContext = this.filterConfig.getServletContext();
        PortletInvoker portletInvoker = (PortletInvoker) servletContext.getAttribute("ConsumerPortletInvoker");

        PortalPrepareResponse prepareResponse = new PortalPrepareResponse(request, response);

        chain.doFilter(request, prepareResponse);

        PagePortletControllerContext context = new PagePortletControllerContext(request, response, portletInvoker, servletContext, prepareResponse);

        String type = request.getParameter(URLParameterConstants.TYPE);

        PortletPageNavigationalState pageNavigationalState = null;
        if (URLParameterConstants.PORTLET_TYPE.equals(type)) {
            ControllerRequestFactory factory = new ControllerRequestFactory(context.getPageNavigationalStateSerialization());
            RequestDecoder decoder = new RequestDecoder(request);
            PortletController portletController = new PortletController();
            ControllerRequest controllerRequest = factory.decode(decoder.getQueryParameters(), decoder.getBody());
            ControllerResponse controllerResponse = portletController.process(context, controllerRequest);

            if (controllerResponse instanceof PageUpdateResponse) {
                PageUpdateResponse pageUpdateResponse = (PageUpdateResponse) controllerResponse;

                pageNavigationalState = pageUpdateResponse.getPageNavigationalState();

                request.setAttribute("bilto", context);

                if (request instanceof PortletActionRequest && this.redirectAfterAction) {
                    PortletURLRenderer renderer = new PortletURLRenderer(pageNavigationalState, context.getClientRequest(), context.getClientResponse(),
                            context.getPageNavigationalStateSerialization());

                    URLFormat urlFormat = new URLFormat(null, null, true, null);
                    String url = renderer.renderURL(urlFormat);

                    response.sendRedirect(url);
                    return;
                }
            } else if (controllerResponse instanceof ResourceResponse) {
                ResourceResponse resourceResponse = (ResourceResponse) controllerResponse;
                PortletInvocationResponse pir = resourceResponse.getResponse();

                if (pir instanceof ContentResponse) {
                    ContentResponse contentResponse = (ContentResponse) pir;

                    if (contentResponse.getType() == ContentResponse.TYPE_EMPTY) {
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        String contentType = contentResponse.getContentType();
                        if (contentType != null) {
                            response.setContentType(contentType);
                        }

                        if (contentResponse.getType() == ContentResponse.TYPE_BYTES) {
                            ServletOutputStream out = null;
                            try {
                                out = response.getOutputStream();
                                out.write(contentResponse.getBytes());
                            } finally {
                                IOTools.safeClose(out);
                            }
                        } else {
                            Writer writer = null;
                            try {
                                writer = response.getWriter();
                                writer.write(contentResponse.getChars());
                            } finally {
                                writer.close();
                            }
                        }
                    }
                }

                return;
            } else if (controllerResponse instanceof PortletResponse) {
                PortletResponse portletResponse = (PortletResponse) controllerResponse;
                PortletInvocationResponse pir = portletResponse.getResponse();

                if (pir instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) pir;

                    //
                    if (errorResponse.getCause() != null) {
                        throw new ServletException("portlet_error", errorResponse.getCause());
                    } else {
                        throw new ServletException("portlet_error");
                    }
                } else if (pir instanceof UnavailableResponse) {
                    throw new ServletException("unavailable");
                }
            }
        } else {
            PortletPageNavigationalStateSerialization serialization = new PortletPageNavigationalStateSerialization(context.getStateControllerContext());
            pageNavigationalState = null;
            String blah = request.getParameter(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE);
            if (blah != null) {
                byte[] bytes = Base64.decode(blah, true);
                pageNavigationalState = IOTools.unserialize(serialization, SerializationFilter.COMPRESSOR, bytes);
            }
        }

        PortalRenderResponse renderResponse = new PortalRenderResponse(request, response, context, pageNavigationalState, prepareResponse);

        chain.doFilter(request, renderResponse);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
