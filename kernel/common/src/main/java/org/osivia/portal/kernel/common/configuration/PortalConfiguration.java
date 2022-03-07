package org.osivia.portal.kernel.common.configuration;

import javax.annotation.PreDestroy;

import org.jboss.portal.core.aspects.portlet.AjaxInterceptor;
import org.jboss.portal.core.aspects.portlet.HeaderInterceptor;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.CCPPInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ConsumerCacheInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.aspects.portlet.ContextTrackerInterceptor;
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
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.mt.ContextDispatcherWrapperInterceptor;
import org.osivia.portal.core.tracker.ITracker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Portal configuration.
 *
 * @author Cédric Krommenhoek
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal,org.jboss.portal")
@ImportResource("classpath*:spring-beans.xml")
public class PortalConfiguration implements ApplicationContextAware {

    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;



    
    /**
     * Constructor.
     */
    public PortalConfiguration() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Locator.registerApplicationContext(applicationContext);
    }


    /**
     * Portlet application deployer.
     *
     * @return portlet application deployer
     */
/*    
    @Bean(name = {"PortletApplicationDeployer", "PortletApplicationRegistry"})
    public PortletApplicationDeployer getPortletApplicationDeployer() {
        PortletApplicationDeployer portletApplicationDeployer = new PortletApplicationDeployer();
        portletApplicationDeployer.setServletContainerFactory(this.applicationContext.getBean("ServletContainerFactory", ServletContainerFactory.class));
        portletApplicationDeployer.setContainerPortletInvoker(this.applicationContext.getBean("ContainerPortletInvoker", ContainerPortletInvoker.class));
        
//        portletApplicationDeployer.start();

        // Valve interceptor circular dependency
        ValveInterceptor valveInterceptor = this.applicationContext.getBean("ValveInterceptor", ValveInterceptor.class);
        valveInterceptor.setPortletApplicationRegistry(portletApplicationDeployer);

        return portletApplicationDeployer;
    }
*/

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
        ServletContainerFactory factory = this.applicationContext.getBean("ServletContainerFactory", ServletContainerFactory.class);
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
        return new PortletInvokerInterceptor(this.applicationContext.getBean("ConsumerCacheInterceptor", PortletInvoker.class));
    }


    /**
     * Consumer cache interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ConsumerCacheInterceptor")
    public PortletInvoker getConsumerCacheInterceptor() {
        ConsumerCacheInterceptor consumerCacheInterceptor = new ConsumerCacheInterceptor();
        consumerCacheInterceptor.setNext(this.applicationContext.getBean("PortletCustomizationInterceptor", PortletInvoker.class));
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
        portletCustomizationInterceptor.setNext(this.applicationContext.getBean("ProducerPortletInvoker", PortletInvoker.class));
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
        producerPortletInvoker.setNext(this.applicationContext.getBean("ContainerPortletInvoker", ContainerPortletInvoker.class));
        producerPortletInvoker.setPersistenceManager(this.applicationContext.getBean("ProducerPersistenceManager", PortletStatePersistenceManager.class));
        producerPortletInvoker.setStateManagementPolicy(this.applicationContext.getBean("ProducerStateManagementPolicy", StateManagementPolicy.class));
        producerPortletInvoker.setStateConverter(this.applicationContext.getBean("ProducerStateConverter", StateConverter.class));
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
        containerPortletInvoker.setNext(this.applicationContext.getBean("ValveInterceptor", ValveInterceptor.class));
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
        valveInterceptor.setNext(this.applicationContext.getBean("SecureTransportInterceptor", PortletInvoker.class));
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
        secureTransportInterceptor.setNext(this.applicationContext.getBean("ContextDispatcherInterceptor", PortletInvoker.class));
        return secureTransportInterceptor;
    }


    /**
     * Context dispatcher interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ContextDispatcherInterceptor")
    public PortletInvoker getContextDispatcherInterceptor() {
        ContextDispatcherWrapperInterceptor contextDispatcherInterceptor = new ContextDispatcherWrapperInterceptor();
        contextDispatcherInterceptor.setServletContainerFactory(this.applicationContext.getBean("ServletContainerFactory", ServletContainerFactory.class));
        contextDispatcherInterceptor.setTracker(this.applicationContext.getBean("osivia:service=Tracker", ITracker.class));
        contextDispatcherInterceptor.setNext(this.applicationContext.getBean("ContextTrackerInterceptor", PortletInvoker.class));
        return contextDispatcherInterceptor;
    }
    
    
    @Bean(name = "ContextTrackerInterceptor")
    public PortletInvoker getContextTrackerInterceptor() {
        ContextTrackerInterceptor contextTrackInterceptor = new ContextTrackerInterceptor();
        contextTrackInterceptor.setNext(this.applicationContext.getBean("AjaxInterceptor", PortletInvoker.class));
        return contextTrackInterceptor;
    }
    

    /**
     * Ajax interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "AjaxInterceptor")
    public PortletInvoker getAjaxInterceptor() {
        AjaxInterceptor ajaxInterceptor = new AjaxInterceptor();
        ajaxInterceptor.setNext(this.applicationContext.getBean("ProducerCacheInterceptor", PortletInvoker.class));
        return ajaxInterceptor;
    } 
    

    /**
     * Producer cache interceptor.
     *
     * @return portlet invoker
     */
    @Bean(name = "ProducerCacheInterceptor")
    public PortletInvoker getProducerCacheInterceptor() {
        ProducerCacheInterceptor producerCacheInterceptor = new ProducerCacheInterceptor();
        producerCacheInterceptor.setNext(this.applicationContext.getBean("CCPPInterceptor", PortletInvoker.class));
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
        ccppInterceptor.setNext(this.applicationContext.getBean("RequestAttributeConversationInterceptor", PortletInvoker.class));
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
        requestAttributeConversationInterceptor.setNext(this.applicationContext.getBean("EventPayloadInterceptor", PortletInvoker.class));
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
        eventPayloadInterceptor.setNext(this.applicationContext.getBean("HeaderInterceptor", PortletInvoker.class));
        return eventPayloadInterceptor;
    }

    
    
    @Bean(name = "HeaderInterceptor")
    public PortletInvoker getHeaderInterceptor() {
    	HeaderInterceptor headerInterceptor = new HeaderInterceptor();
    	headerInterceptor.setNext(this.applicationContext.getBean("PortletContainerDispatcher", PortletInvoker.class));
        return headerInterceptor;
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
