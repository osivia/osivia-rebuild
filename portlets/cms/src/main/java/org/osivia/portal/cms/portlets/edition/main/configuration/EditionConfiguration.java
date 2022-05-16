package org.osivia.portal.cms.portlets.edition.main.configuration;

import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Sample configuration.
 *
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal.cms.portlets.edition.main")
public class EditionConfiguration {

    /**
     * Constructor.
     */
    public EditionConfiguration() {
        super();
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
        viewResolver.setPrefix("/WEB-INF/jsp/edition/main/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
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
}
