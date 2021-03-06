package org.osivia.portal.kernel.common.configuration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Dispatcher configuration.
 *
 * @author Cédric Krommenhoek
 */
@Configuration
@EnableWebMvc

public class DispatcherConfiguration extends WebMvcConfigurerAdapter {

    /**
     * Constructor.
     */
    public DispatcherConfiguration() {
        super();
    }




}
