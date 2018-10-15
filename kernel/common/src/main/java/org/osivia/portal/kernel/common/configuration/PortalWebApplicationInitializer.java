package org.osivia.portal.kernel.common.configuration;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.portal.common.mc.bootstrap.WebBootstrap;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Portal web application initializer.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAnnotationConfigDispatcherServletInitializer
 */
public class PortalWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

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
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{PortalConfiguration.class};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{DispatcherConfiguration.class};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getServletMappings() {
        //return new String[]{"/id/*"};
        return new String[]{};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Filter[] getServletFilters() {
//        Filter cmsFilter = new CMSFilter();
//        Filter errorFilter = new ErrorHandlingFilter();
//
//        return new Filter[]{cmsFilter, errorFilter};
    	return new Filter[]{};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

 //       servletContext.addListener(new WebBootstrap());
        servletContext.addListener(new PortalListener());
    }

}
