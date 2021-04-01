package org.osivia.portal.services.cms.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.jboss.portal.core.controller.command.mapper.CommandFactoryDelegate;
import org.jboss.portal.core.controller.command.mapper.DelegatingCommandFactoryService;
import org.jboss.portal.core.controller.command.mapper.DelegatingURLFactory;
import org.jboss.portal.core.model.portal.PortalObjectCommandFactory;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectURLFactory;
import org.jboss.portal.core.model.portal.command.mapping.DefaultPortalObjectPathMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The Class RuntimeBeanBuilder.
 * creates dynamic bean relative to portal namespaces
 */
@Component
public class RuntimeBeanBuilder {
    private static final String OSIVIA_CMS_REPOSITORY_SUFFIX = ".className";
    private static final String OSIVIA_CMS_REPOSITORY_PREFIX = "osivia.cms.repository.";

    private static final String OSIVIA_CMS_SERVICE_INTEGRATION_PREFIX = "osivia.cms.integration.";    
    private static final String OSIVIA_CMS_SERVICE_INTEGRATION_BEAN_NAME_SUFFIX = ".beanName";
    private static final String OSIVIA_CMS_SERVICE_INTEGRATION_CLASSNAME_SUFFIX = ".className";


    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    @Qualifier("portal:container=PortalObject")
    private PortalObjectContainer container;
    
    @Autowired
    @Qualifier("portal:urlFactory=Delegating")
    private DelegatingURLFactory factory;
    
    
    @Autowired
    @Qualifier("portal:commandFactory=Delegating")
    private DelegatingCommandFactoryService delegatingFactory;
    
    
    
    void createBean( String beanName, BeanDefinitionBuilder builder)    {
        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configContext.getBeanFactory();        
        beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
        // force creation
        configContext.getBean(beanName);
    }
    
    @PostConstruct
    public void buildNamespaces()   {
      // Load system properties
        
      // Load system properties
        
        String fileName = System.getProperty("org.osivia.portal.kernel.tomcat.ExternalPropertySource.file");
        if (fileName != null) {

            try (InputStream input = new FileInputStream(fileName)) {

                Properties props = new Properties();

                // load a properties file
                props.load(input);
                
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    System.setProperty((String)entry.getKey(), (String)entry.getValue());
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }        
 

            
        /* Portal namespace beans */
        
        Properties properties = System.getProperties();
        for(Object propertyName : properties.keySet())  {
            if( propertyName instanceof String) {
                String sPropertyName = (String) propertyName;
                if( sPropertyName.startsWith(OSIVIA_CMS_REPOSITORY_PREFIX) && sPropertyName.endsWith(OSIVIA_CMS_REPOSITORY_SUFFIX)) {
                    String repositoryName = sPropertyName.substring(OSIVIA_CMS_REPOSITORY_PREFIX.length() , sPropertyName.indexOf(OSIVIA_CMS_REPOSITORY_SUFFIX));
                    
                    buildPortalBeans( repositoryName) ;
                }
            }
         }
        
        /* Service integration beans */
        

        for(Object propertyName : properties.keySet())  {
            if( propertyName instanceof String) {
                String sPropertyName = (String) propertyName;
                if( sPropertyName.startsWith(OSIVIA_CMS_SERVICE_INTEGRATION_PREFIX) && sPropertyName.endsWith(OSIVIA_CMS_SERVICE_INTEGRATION_BEAN_NAME_SUFFIX)) {
                    String integrationServiceName = sPropertyName.substring(OSIVIA_CMS_SERVICE_INTEGRATION_PREFIX.length() , sPropertyName.indexOf(OSIVIA_CMS_SERVICE_INTEGRATION_BEAN_NAME_SUFFIX));
                    String beanName = System.getProperty(OSIVIA_CMS_SERVICE_INTEGRATION_PREFIX+integrationServiceName+OSIVIA_CMS_SERVICE_INTEGRATION_BEAN_NAME_SUFFIX);
                    String className = System.getProperty(OSIVIA_CMS_SERVICE_INTEGRATION_PREFIX+integrationServiceName+OSIVIA_CMS_SERVICE_INTEGRATION_CLASSNAME_SUFFIX);

                    buildIntegrationBean( beanName, className) ;
                }
            }
         }
        

    }
    

    public void buildPortalBeans( String repositoryName) {
        
/*
        <bean class="org.jboss.portal.core.model.portal.command.mapping.DefaultPortalObjectPathMapper"
              id="portal:service=PortalObjectPathMapper,type=Templates" >
          <property name="Container" ref="portal:container=PortalObject"/>
          <property name="Namespace" value="templates" />
       </bean>
*/
        
        BeanDefinitionBuilder mapper =
                BeanDefinitionBuilder.rootBeanDefinition(DefaultPortalObjectPathMapper.class).setScope(BeanDefinition.SCOPE_SINGLETON)
                                     .addPropertyValue("Namespace", repositoryName).addPropertyReference("Container", "portal:container=PortalObject");
        createBean( "portal:service=PortalObjectPathMapper,type="+repositoryName, mapper);

/*         
        <bean class="org.jboss.portal.core.model.portal.PortalObjectURLFactory"
                id="portal:urlFactory=PortalObject,type=Templates" >
            
            <property name="Namespace" value="templates" />
            <property name="Path" value="/templates" />
            <property name="Factory" ref="portal:urlFactory=Delegating"/>
            <property name="Mapper" ref="portal:service=PortalObjectPathMapper,type=Templates"/>
         </bean>
*/        
        BeanDefinitionBuilder portalObjectFactory =
        BeanDefinitionBuilder.rootBeanDefinition(PortalObjectURLFactory.class).setScope(BeanDefinition.SCOPE_SINGLETON)
        .addPropertyValue("Namespace", repositoryName)
        .addPropertyValue("Path", "/"+repositoryName)
        .addPropertyReference("Factory", "portal:urlFactory=Delegating")
        .addPropertyReference("Mapper", "portal:service=PortalObjectPathMapper,type="+repositoryName);
        createBean( "portal:urlFactory=PortalObject,type="+repositoryName, portalObjectFactory);
/*        
        <bean class="org.jboss.portal.core.model.portal.PortalObjectCommandFactory" id="portal:commandFactory=Templates">
        <property name="Mapper" ref="portal:service=PortalObjectPathMapper,type=Templates"/>
     </bean>
*/     
        BeanDefinitionBuilder portalCommandFactory =
        BeanDefinitionBuilder.rootBeanDefinition(PortalObjectCommandFactory.class).setScope(BeanDefinition.SCOPE_SINGLETON)
        .addPropertyReference("Mapper", "portal:service=PortalObjectPathMapper,type="+repositoryName);
        createBean( "portal:commandFactory="+repositoryName, portalCommandFactory);
/*        
        <bean class="org.jboss.portal.core.controller.command.mapper.CommandFactoryDelegate"
                id="portal:commandFactory=Delegate,path=Templates" >
            <property name="Path" value="/templates" />
            <property name="DelegatingFactory" ref="portal:commandFactory=Delegating"/>
            <property name="DelegateFactory" ref="portal:commandFactory=Templates"/>
         </bean>
 */
        BeanDefinitionBuilder portalCommandFactoryDelegate =
        BeanDefinitionBuilder.rootBeanDefinition(CommandFactoryDelegate.class).setScope(BeanDefinition.SCOPE_SINGLETON)
        .addPropertyValue("Path", "/"+repositoryName)
        .addPropertyReference("DelegatingFactory", "portal:commandFactory=Delegating")
        .addPropertyReference("DelegateFactory", "portal:commandFactory="+repositoryName);
        createBean( "portal:commandFactory=Delegate,path="+repositoryName, portalCommandFactoryDelegate);

        
    }
    
    public void buildIntegrationBean(String beanName, String className) {

        // Integration service

        /*
         * <mbean
         * code="fr.toutatice.portail.cms.nuxeo.services.NuxeoService"
         * name="osivia:service=NuxeoService">
         * 
         * <depends optional-attribute-name="Profiler" proxy-type="attribute">osivia:service=ProfilerService</depends>
         */


        BeanDefinitionBuilder nuxeoService;
        try {
            nuxeoService = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(className)).setScope(BeanDefinition.SCOPE_SINGLETON)
                    .addPropertyReference("Profiler", "osivia:service=ProfilerService");
            createBean(beanName, nuxeoService);
        } catch (ClassNotFoundException e) {
            System.err.println("Can't start " + className + " : class not found");
        }

    }
}
