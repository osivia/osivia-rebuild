package org.osivia.portal.cms.portlets.edition.page.apps.modify.configuration;

import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.portlet.PortletAppUtils;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.ui.layout.LayoutItemsService;
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

import javax.portlet.PortletConfig;

/**
 * Sample configuration.
 *
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "org.osivia.portal.cms.portlets.edition.page.apps.modify")
public class ModifyConfiguration implements PortletConfigAware {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Constructor.
     */
    public ModifyConfiguration() {
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
        viewResolver.setPrefix("/WEB-INF/jsp/edition/apps/modify/");
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

    @Bean
    public IAppsService getAppsService() {
        return Locator.getService(IAppsService.class);
    }

    /**
     * Get layout items service.
     *
     * @return layout items service
     */
    @Bean
    public LayoutItemsService getLayoutItemsService() {
        return Locator.getService(LayoutItemsService.class);
    }
    
    @Bean
    public ThemeService getThemeService() {
        return Locator.getService("portal:service=ThemeService", ThemeService.class);
    }    

}
