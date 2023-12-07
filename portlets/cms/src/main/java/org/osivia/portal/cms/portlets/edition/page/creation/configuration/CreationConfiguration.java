package org.osivia.portal.cms.portlets.edition.page.creation.configuration;

import javax.portlet.PortletConfig;

import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
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
@ComponentScan(basePackages = "org.osivia.portal.cms.portlets.edition.page.creation")
public class CreationConfiguration implements PortletConfigAware {

    @Autowired
    private ApplicationContext applicationContext;
    
    /**
     * Constructor.
     */
    public CreationConfiguration() {
        super();
    }


    @Override
    public void setPortletConfig(PortletConfig portletConfig) {
            PortletAppUtils.registerApplication(portletConfig, applicationContext);            

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
        viewResolver.setPrefix("/WEB-INF/jsp/edition/page/creation/");
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
    public IPreviewModeService getPreviewModeService() {
        return Locator.getService(IPreviewModeService.class);
    }
    
    @Bean
    public ILocaleService getLocaleService() {
        return Locator.getService(ILocaleService.class);
    }

    @Bean
    public LayoutService getLayoutService() {
        return Locator.getService(LayoutService.class);        
    }
    
    @Bean
    public ThemeService getThemeService()   {
        return Locator.getService(ThemeService.class);           
    }
    
    @Bean
    public IBundleFactory getBundleFactory() {
        IInternationalizationService internationalizationService = Locator.getService( IInternationalizationService.MBEAN_NAME, IInternationalizationService.class);
        return internationalizationService.getBundleFactory(this.getClass().getClassLoader(), this.applicationContext);
    }
}
