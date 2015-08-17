package org.osivia.portal.kernel.common.spring;

import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.CCPPInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ConsumerCacheInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.aspects.portlet.EventPayloadInterceptor;
import org.jboss.portal.portlet.aspects.portlet.PortletCustomizationInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ProducerCacheInterceptor;
import org.jboss.portal.portlet.aspects.portlet.RequestAttributeConversationInterceptor;
import org.jboss.portal.portlet.aspects.portlet.SecureTransportInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ValveInterceptor;
import org.jboss.portal.portlet.container.ContainerPortletDispatcher;
import org.jboss.portal.portlet.container.ContainerPortletInvoker;
import org.jboss.portal.portlet.impl.state.StateConverterV0;
import org.jboss.portal.portlet.impl.state.StateManagementPolicyService;
import org.jboss.portal.portlet.impl.state.producer.PortletStatePersistenceManagerService;
import org.jboss.portal.portlet.mc.PortletApplicationDeployer;
import org.jboss.portal.portlet.state.StateConverter;
import org.jboss.portal.portlet.state.StateManagementPolicy;
import org.jboss.portal.portlet.state.producer.PortletStatePersistenceManager;
import org.jboss.portal.portlet.state.producer.ProducerPortletInvoker;
import org.jboss.portal.web.ServletContainer;
import org.jboss.portal.web.ServletContainerFactory;
import org.jboss.portal.web.impl.DefaultServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Portal configuration.
 *
 * @author Cédric Krommenhoek
 */
@Configuration
// @ComponentScan(basePackages = "org.osivia.portal")
public class PortalConfiguration {

    /**
     * Constructor.
     */
    public PortalConfiguration() {
        super();
    }


    /**
     * Portlet application deployer.
     *
     * @return portlet application deployer
     */
    @Bean(name = {"PortletApplicationDeployer", "PortletApplicationRegistry"})
    public PortletApplicationDeployer getPortletApplicationDeployer() {
        PortletApplicationDeployer portletApplicationDeployer = new PortletApplicationDeployer();
        portletApplicationDeployer.setServletContainer(this.getServletContainer());
        portletApplicationDeployer.setContainerPortletInvoker(this.getContainerPortletInvoker());

        // Valve interceptor circular dependency
        ValveInterceptor valveInterceptor = this.getValveInterceptor();
        valveInterceptor.setPortletApplicationRegistry(portletApplicationDeployer);

        return portletApplicationDeployer;
    }


    /**
     * Servlet container factory.
     *
     * @return servlet container factory
     */
    @Bean(name = "ServletContainerFactory")
    public ServletContainerFactory getServletContainerFactory() {
        return DefaultServletContainerFactory.getInstance();
    }


    /**
     * Servlet container.
     *
     * @return servlet container
     */
    @Bean(name = "ServletContainer")
    public ServletContainer getServletContainer() {
        ServletContainerFactory factory = this.getServletContainerFactory();
        return factory.getServletContainer();
    }


    /**
     * Producer persistence manager.
     *
     * @return portlet state persistence manager
     */
    @Bean(name = "ProducerPersistenceManager")
    public PortletStatePersistenceManager getProducerPersistenceManager() {
        return new PortletStatePersistenceManagerService();
    }


    /**
     * Producer state management policy.
     *
     * @return state management policy
     */
    @Bean(name = "ProducerStateManagementPolicy")
    public StateManagementPolicy getProducerStateManagementPolicy() {
        StateManagementPolicyService stateManagementPolicy = new StateManagementPolicyService();
        stateManagementPolicy.setPersistLocally(true);
        return stateManagementPolicy;
    }


    /**
     * Producer state converter.
     *
     * @return state converter
     */
    @Bean(name = "ProducerStateConverter")
    public StateConverter getProducerStateConverter() {
        return new StateConverterV0();
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
        portletCustomizationInterceptor.setNext(this.getProducerPortletInvoker());
        return portletCustomizationInterceptor;
    }


    /**
     * Producer portlet invoker.
     *
     * @return portlet invoker
     */
    @Bean(name = "ProducerPortletInvoker")
    public PortletInvoker getProducerPortletInvoker() {
        ProducerPortletInvoker producerPortletInvoker = new ProducerPortletInvoker();
        producerPortletInvoker.setNext(this.getContainerPortletInvoker());
        producerPortletInvoker.setPersistenceManager(this.getProducerPersistenceManager());
        producerPortletInvoker.setStateManagementPolicy(this.getProducerStateManagementPolicy());
        producerPortletInvoker.setStateConverter(this.getProducerStateConverter());
        return producerPortletInvoker;
    }


    /**
     * Container portlet invoker.
     *
     * @return container portlet invoker
     */
    @Bean(name = "ContainerPortletInvoker")
    public ContainerPortletInvoker getContainerPortletInvoker() {
        ContainerPortletInvoker containerPortletInvoker = new ContainerPortletInvoker();
        containerPortletInvoker.setNext(this.getValveInterceptor());
        return containerPortletInvoker;
    }


    /**
     * Valve interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ValveInterceptor")
    public ValveInterceptor getValveInterceptor() {
        ValveInterceptor valveInterceptor = new ValveInterceptor();
        valveInterceptor.setNext(this.getSecureTransportInterceptor());
        return valveInterceptor;
    }


    /**
     * Secure transport interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "SecureTransportInterceptor")
    public PortletInvoker getSecureTransportInterceptor() {
        SecureTransportInterceptor secureTransportInterceptor = new SecureTransportInterceptor();
        secureTransportInterceptor.setNext(this.getContextDispatcherInterceptor());
        return secureTransportInterceptor;
    }


    /**
     * Context dispatcher interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ContextDispatcherInterceptor")
    public PortletInvoker getContextDispatcherInterceptor() {
        ContextDispatcherInterceptor contextDispatcherInterceptor = new ContextDispatcherInterceptor();
        contextDispatcherInterceptor.setServletContainerFactory(this.getServletContainerFactory());
        contextDispatcherInterceptor.setNext(this.getProducerCacheInterceptor());
        return contextDispatcherInterceptor;
    }


    /**
     * Producer cache interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ProducerCacheInterceptor")
    public PortletInvoker getProducerCacheInterceptor() {
        ProducerCacheInterceptor producerCacheInterceptor = new ProducerCacheInterceptor();
        producerCacheInterceptor.setNext(this.getCCPPInterceptor());
        return producerCacheInterceptor;
    }


    /**
     * CCPP interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "CCPPInterceptor")
    public PortletInvoker getCCPPInterceptor() {
        CCPPInterceptor ccppInterceptor = new CCPPInterceptor();
        ccppInterceptor.setNext(this.getRequestAttributeConversationInterceptor());
        return ccppInterceptor;
    }


    /**
     * Request attribute conversation interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "RequestAttributeConversationInterceptor")
    public PortletInvoker getRequestAttributeConversationInterceptor() {
        RequestAttributeConversationInterceptor requestAttributeConversationInterceptor = new RequestAttributeConversationInterceptor();
        requestAttributeConversationInterceptor.setNext(this.getEventPayloadInterceptor());
        return requestAttributeConversationInterceptor;
    }


    /**
     * Event payload interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "EventPayloadInterceptor")
    public PortletInvoker getEventPayloadInterceptor() {
        EventPayloadInterceptor eventPayloadInterceptor = new EventPayloadInterceptor();
        eventPayloadInterceptor.setNext(this.getPortletContainerDispatcher());
        return eventPayloadInterceptor;
    }


    /**
     * Portlet container dispatcher.
     *
     * @return portlet invoker
     */
    @Bean(name = "PortletContainerDispatcher")
    public PortletInvoker getPortletContainerDispatcher() {
        return new ContainerPortletDispatcher();
    }

}
