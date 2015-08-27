package org.osivia.portal.kernel.common.configuration;

import java.util.EventListener;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.jboss.portal.common.mc.bootstrap.WebBootstrap;
import org.osivia.portal.kernel.common.filter.CMSFilter;
import org.osivia.portal.kernel.common.filter.ErrorHandlingFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Portal web application initializer.
 *
 * @author Cédric Krommenhoek
 * @see WebApplicationInitializer
 */
public class PortalWebApplicationInitializer implements WebApplicationInitializer {

    /**
     * Constructor.
     */
    public PortalWebApplicationInitializer() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, AnnotationConfigWebApplicationContext.class.getName());
        servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, PortalConfiguration.class.getName());

        this.addPortalListener(servletContext);
        this.addSpringListener(servletContext);

        this.addErrorHandlingFilter(servletContext);
        this.addCMSFilter(servletContext);

        this.addDispatcherServlet(servletContext);
    }


    /**
     * Add portal listener.
     *
     * @param servletContext servlet context
     */
    private void addPortalListener(ServletContext servletContext) {
        EventListener listener = new WebBootstrap();
        servletContext.addListener(listener);
    }


    /**
     * Add Spring Framework listener.
     *
     * @param servletContext servlet context
     */
    private void addSpringListener(ServletContext servletContext) {
        EventListener listener = new ContextLoaderListener();
        servletContext.addListener(listener);
    }


    /**
     * Add error handling filter.
     *
     * @param servletContext servlet context
     */
    private void addErrorHandlingFilter(ServletContext servletContext) {
        Filter filter = new ErrorHandlingFilter();
        FilterRegistration.Dynamic registration = servletContext.addFilter(ErrorHandlingFilter.class.getSimpleName(), filter);
        registration.addMappingForUrlPatterns(null, false, "*");
    }


    /**
     * Add CMS filter.
     *
     * @param servletContext servlet context
     */
    private void addCMSFilter(ServletContext servletContext) {
        Filter filter = new CMSFilter();
        FilterRegistration.Dynamic registration = servletContext.addFilter(CMSFilter.class.getSimpleName(), filter);
        registration.addMappingForUrlPatterns(null, false, "/id/*");
    }


    /**
     * Add dispatcher servlet.
     *
     * @param servletContext servlet context
     */
    private void addDispatcherServlet(ServletContext servletContext) {
        Servlet servlet = new DispatcherServlet();
        ServletRegistration.Dynamic registration = servletContext.addServlet("Dispatcher", servlet);
        registration.setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, AnnotationConfigWebApplicationContext.class.getName());
        registration.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, DispatcherConfiguration.class.getName());
        registration.setLoadOnStartup(1);
        registration.addMapping("/id/*");
    }

}
