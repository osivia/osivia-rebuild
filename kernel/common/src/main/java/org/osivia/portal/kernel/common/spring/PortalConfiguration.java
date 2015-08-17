package org.osivia.portal.kernel.common.spring;

import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ConsumerCacheInterceptor;
import org.jboss.portal.portlet.aspects.portlet.PortletCustomizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Portal configuration.
 *
 * @author Cédric Krommenhoek
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal")
public class PortalConfiguration {

    /**
     * Constructor.
     */
    public PortalConfiguration() {
        super();
    }


    /**
     * Consumer portlet invoker.
     *
     * @return portlet invoker
     */
    @Bean(name = "ConsumerPortletInvoker")
    public PortletInvoker getConsumerPortletInvoker() {
        return new PortletInvokerInterceptor(this.getConsumerCacheInterceptor());
    }


    /**
     * Consumer cache interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ConsumerCacheInterceptor")
    public PortletInvoker getConsumerCacheInterceptor() {
        ConsumerCacheInterceptor consumerCacheInterceptor = new ConsumerCacheInterceptor();
        consumerCacheInterceptor.setNext(this.getPortletCustomizationInterceptor());
        return consumerCacheInterceptor;
    }


    /**
     * Portlet customization interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "PortletCustomizationInterceptor")
    public PortletInvoker getPortletCustomizationInterceptor() {
        PortletCustomizationInterceptor portletCustomizationInterceptor = new PortletCustomizationInterceptor();
        // portletCustomizationInterceptor.setNext(next); FIXME
        return portletCustomizationInterceptor;
    }

}
