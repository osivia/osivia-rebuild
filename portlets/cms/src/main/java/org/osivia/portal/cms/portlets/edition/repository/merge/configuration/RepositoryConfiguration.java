package org.osivia.portal.cms.portlets.edition.repository.merge.configuration;

import javax.portlet.PortletConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.portlet.PortletAppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.portlet.context.PortletConfigAware;
import org.springframework.web.portlet.multipart.CommonsPortletMultipartResolver;
import org.springframework.web.portlet.multipart.PortletMultipartResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Repository edition configuration.
 *
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal.cms.portlets.edition.repository.merge")
public class RepositoryConfiguration implements PortletConfigAware {

    @Autowired
    private ApplicationContext applicationContext;
    

    
    /**
     * Constructor.
     */
    public RepositoryConfiguration() {
        super();
    }


    @Override
    public void setPortletConfig(PortletConfig portletConfig) {
            PortletAppUtils.registerApplication(portletConfig, applicationContext);            

    }

    
    /**
     * Get multipart resolver.
     *
     * @return multipart resolver
     */
    @Bean(name = "portletMultipartResolver")
    public PortletMultipartResolver getMultipartResolver() {
        CommonsPortletMultipartResolver multipartResolver = new CommonsPortletMultipartResolver();
        multipartResolver.setDefaultEncoding(CharEncoding.UTF_8);
        multipartResolver.setMaxUploadSizePerFile(2 * FileUtils.ONE_MB);
        return multipartResolver;
    }
    
    
    /**
     * Get view resolver.
     *
     * @return view resolver
     */
    @Bean
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setCache(true);
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/edition/repository/merge/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * Get message source.
     *
     * @return message source
     */
    @Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("edition");
        return messageSource;
    }

    @Bean
    public CMSService getCMSService() {
        return Locator.getService(CMSService.class);
    }
    

    
    @Bean
    public IBundleFactory getBundleFactory() {
        IInternationalizationService internationalizationService = Locator.getService( IInternationalizationService.MBEAN_NAME, IInternationalizationService.class);
        return internationalizationService.getBundleFactory(this.getClass().getClassLoader(), this.applicationContext);
    }
    
    /**
     * Get notifications service.
     * 
     * @return notifications service
     */
    @Bean
    public INotificationsService getNotificationsService() {
        return Locator.getService( INotificationsService.MBEAN_NAME, INotificationsService.class);
    }
    
}
