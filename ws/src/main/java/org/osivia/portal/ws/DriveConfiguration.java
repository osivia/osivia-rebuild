package org.osivia.portal.ws;

import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
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
public class DriveConfiguration {

    /**
     * Constructor.
     */
    public DriveConfiguration() {
        super();
    }



    @Bean
    public CMSService getCMSService() {
        return Locator.getService(CMSService.class);
    }
    
    @Bean
    public IPortalUrlFactory getPortalURLFactory() {
        return Locator.getService(IPortalUrlFactory.class);
    }

}
