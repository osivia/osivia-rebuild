package org.osivia.portal.cms.portlets.edition.page.main.configuration;

import javax.portlet.PortletConfig;

import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.ha.IHAService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.portlet.PortletAppUtils;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.portlet.context.PortletConfigAware;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Sample configuration.
 *
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal.cms.portlets.edition.page.main")
public class EditionConfiguration implements PortletConfigAware {

    /**
     * Constructor.
     */
    public EditionConfiguration() {
        super();
    }
    

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;



    @Override
    public void setPortletConfig(PortletConfig portletConfig) {
        PortletAppUtils.registerApplication(portletConfig, applicationContext);            
    }

    
    
    /**z
     * Get view resolver.
     *
     * @return view resolver
     */
    @Bean
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setCache(true);
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/edition/page/main/");
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
    public IPortalUrlFactory getPortalURLFactory() {
        return Locator.getService(IPortalUrlFactory.class);
    }
    
    @Bean
    public IDynamicService getDynamicWindowService() {
        return Locator.getService(IDynamicService.class);
    }

    @Bean
    public IPreviewModeService getPreviewModeService() {
        return Locator.getService(IPreviewModeService.class);
    }
    
    @Bean
    public ILocaleService getLocaleService() {
        return Locator.getService(ILocaleService.class);
    }
    
    /**
     * Get internationalization bundle factory.
     *
     * @return internationalization bundle factory
     */
    @Bean
    public IBundleFactory getBundleFactory() {
        IInternationalizationService internationalizationService = Locator.getService( IInternationalizationService.MBEAN_NAME, IInternationalizationService.class);
        return internationalizationService.getBundleFactory(this.getClass().getClassLoader(), this.applicationContext);
    }
    

	@Bean
	public IHAService getHAService() {
		return Locator.getService(IHAService.MBEAN_NAME, IHAService.class);
	}

}
