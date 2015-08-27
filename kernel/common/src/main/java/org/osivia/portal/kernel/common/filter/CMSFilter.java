package org.osivia.portal.kernel.common.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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

import org.easymock.EasyMock;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.io.SerializationFilter;
import org.jboss.portal.common.util.Base64;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.controller.PortletController;
import org.jboss.portal.portlet.controller.impl.ControllerRequestFactory;
import org.jboss.portal.portlet.controller.impl.ControllerRequestParameterNames;
import org.jboss.portal.portlet.controller.impl.URLParameterConstants;
import org.jboss.portal.portlet.controller.request.ControllerRequest;
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
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentNotFoundException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.PageContainer;
import org.osivia.portal.api.cms.model.Template;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.model.Window;
import org.osivia.portal.kernel.common.model.PortalPortletControllerContext;
import org.osivia.portal.kernel.common.model.WindowResult;
import org.osivia.portal.kernel.common.response.IntrospectionResponse;
import org.osivia.portal.kernel.common.response.RenderResponse;
import org.osivia.portal.services.common.model.WindowImpl;

/**
 * CMS filter.
 *
 * @author Cédric Krommenhoek
 * @see Filter
 */
public class CMSFilter implements Filter {

    /** Servlet context. */
    private ServletContext servletContext;
    /** Portlet invoker. */
    private PortletInvoker invoker;
    /** CMS service. */
    private CMSService cmsService;


    /**
     * Constructor.
     */
    public CMSFilter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();

        this.invoker = (PortletInvoker) this.servletContext.getAttribute("ConsumerPortletInvoker");

        // WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        // this.invoker = webApplicationContext.getBean("ConsumerPortletInvoker", PortletInvoker.class);


        SortedSet<Window> windowsCol1 = new TreeSet<Window>();

        WindowImpl window1 = new WindowImpl("window-1", "samples-remotecontroller-portlet", "RemoteControl");
        window1.setOrder(1);
        window1.setRegion("col1");
        windowsCol1.add(window1);


        SortedSet<Window> windowsCol2 = new TreeSet<Window>();

        WindowImpl window2 = new WindowImpl("window-2", "osivia-portal-portlets-sample", "Sample");
        window2.setOrder(1);
        window2.setRegion("col2");
        windowsCol2.add(window2);


        Template page = EasyMock.createMock("Page", Template.class);
        EasyMock.expect(page.getPageContainer()).andStubReturn(page);
        EasyMock.expect(page.getTemplate()).andStubReturn(page);
        EasyMock.expect(page.getLayout()).andReturn("2-cols").anyTimes();
        EasyMock.expect(page.getWindows("col1")).andReturn(windowsCol1).anyTimes();
        EasyMock.expect(page.getWindows("col2")).andReturn(windowsCol2).anyTimes();

        this.cmsService = EasyMock.createMock("CMSService", CMSService.class);
        try {
            EasyMock.expect(this.cmsService.getDocument(EasyMock.anyObject(ServletRequest.class))).andStubReturn(page);
        } catch (CMSException e) {
            throw new ServletException(e);
        }


        EasyMock.replay(page);
        EasyMock.replay(this.cmsService);
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
        } catch (DocumentNotFoundException e) {
            throw new ServletException("404", e);
        } catch (CMSException e) {
            throw new ServletException(e);
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
     * @throws CMSException
     * @throws PortletInvokerException
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException, CMSException,
            PortletInvokerException {
        Document document = this.cmsService.getDocument(request);
        PageContainer pageContainer = document.getPageContainer();
        Template template = pageContainer.getTemplate();

        // Introspection phase
        IntrospectionResponse introspectionResponse = new IntrospectionResponse(response, template);
        chain.doFilter(request, introspectionResponse);


        // Windows
        List<Window> windows = introspectionResponse.getWindows();

        // Invocation type
        String type = request.getParameter(URLParameterConstants.TYPE);


        // Portlet controller
        PortletController portletController = new PortletController();
        // Portlet container context
        PortalPortletControllerContext portletControllerContext = new PortalPortletControllerContext(request, response, this.invoker, windows);
        // Portlet page navigational state
        PortletPageNavigationalState pageNavigationalState = null;


        if (URLParameterConstants.PORTLET_TYPE.equals(type)) {
            ControllerRequestFactory factory = new ControllerRequestFactory(portletControllerContext.getPageNavigationalStateSerialization());
            RequestDecoder decoder = new RequestDecoder(request);
            ControllerRequest controllerRequest = factory.decode(decoder.getQueryParameters(), decoder.getBody());
            ControllerResponse controllerResponse = portletController.process(portletControllerContext, controllerRequest);

            if (controllerResponse instanceof PageUpdateResponse) {
                PageUpdateResponse pageUpdateResponse = (PageUpdateResponse) controllerResponse;
                pageNavigationalState = pageUpdateResponse.getPageNavigationalState();
                request.setAttribute("bilto", portletControllerContext);
            } else if (controllerResponse instanceof ResourceResponse) {
                ResourceResponse resourceResponse = (ResourceResponse) controllerResponse;
                PortletInvocationResponse portletInvocationResponse = resourceResponse.getResponse();

                if (portletInvocationResponse instanceof ContentResponse) {
                    ContentResponse contentResponse = (ContentResponse) portletInvocationResponse;

                    if (contentResponse.getType() == ContentResponse.TYPE_EMPTY) {
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        String contentType = contentResponse.getContentType();

                        if (contentType != null) {
                            response.setContentType(contentType);
                        }

                        if (contentResponse.getType() == ContentResponse.TYPE_BYTES) {
                            ServletOutputStream out = response.getOutputStream();
                            try {
                                out.write(contentResponse.getBytes());
                            } finally {
                                IOTools.safeClose(out);
                            }
                        } else {
                            Writer writer = response.getWriter();
                            try {
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
                PortletInvocationResponse portletInvocationResponse = portletResponse.getResponse();

                if (portletInvocationResponse instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) portletInvocationResponse;

                    if (errorResponse.getCause() != null) {
                        throw new ServletException("portlet_error", errorResponse.getCause());
                    } else {
                        throw new ServletException("portlet_error");
                    }
                } else if (portletInvocationResponse instanceof UnavailableResponse) {
                    throw new ServletException("unavailable");
                }
            }
        } else {
            PortletPageNavigationalStateSerialization serialization = new PortletPageNavigationalStateSerialization(
                    portletControllerContext.getStateControllerContext());
            String blah = request.getParameter(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE);
            if (blah != null) {
                byte[] bytes = Base64.decode(blah, true);
                pageNavigationalState = IOTools.unserialize(serialization, SerializationFilter.COMPRESSOR, bytes);
            }
        }


        List<WindowResult> windowResults = new ArrayList<WindowResult>(windows.size());
        for (Window window : windows) {
            Portlet portlet = portletControllerContext.getPortlet(window.getId());
            if (portlet != null) {
                PortletInvocationResponse portletResponse = portletController.render(portletControllerContext, null, pageNavigationalState, window.getId());
                WindowResult windowResult = new WindowResult(window, portletResponse);
                windowResults.add(windowResult);
            }
        }


        // Render phase
        RenderResponse renderResponse = new RenderResponse(response, pageNavigationalState, windowResults);
        chain.doFilter(request, renderResponse);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.servletContext = null;
        this.cmsService = null;
    }

}
