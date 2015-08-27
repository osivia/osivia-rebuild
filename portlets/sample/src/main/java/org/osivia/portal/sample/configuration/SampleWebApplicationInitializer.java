package org.osivia.portal.sample.configuration;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.ViewRendererServlet;

/**
 * Sample web application initializer.
 *
 * @author Cédric Krommenhoek
 * @see WebApplicationInitializer
 */
public class SampleWebApplicationInitializer implements WebApplicationInitializer {

    /**
     * Constructor.
     */
    public SampleWebApplicationInitializer() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        this.addViewRendererServlet(servletContext);
    }


    /**
     * Add view renderer servlet.
     *
     * @param servletContext
     */
    private void addViewRendererServlet(ServletContext servletContext) {
        Servlet servlet = new ViewRendererServlet();
        ServletRegistration.Dynamic registration = servletContext.addServlet(ViewRendererServlet.class.getSimpleName(), servlet);
        registration.addMapping("/WEB-INF/servlet/view");
    }

}
