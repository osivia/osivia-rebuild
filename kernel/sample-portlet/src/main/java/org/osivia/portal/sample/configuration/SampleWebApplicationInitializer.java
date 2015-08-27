package org.osivia.portal.sample.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

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
        // TODO Auto-generated method stub

    }

}
