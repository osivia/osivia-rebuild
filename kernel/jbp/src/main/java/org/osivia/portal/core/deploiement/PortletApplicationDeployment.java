/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.deploiement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.management.MBeanServer;
import org.jboss.deployment.DeploymentException;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.core.deployment.jboss.PortletAppDeployment;
import org.jboss.portal.core.deployment.jboss.PortletAppDeploymentFactory;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.directory.IDirectoryServiceLocator;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.formatters.IFormatter;

import org.osivia.portal.core.web.IWebIdService;

/**
 * Portlet application deployment.
 *
 * @see PortletAppDeployment
 */
public class PortletApplicationDeployment extends PortletAppDeployment {

    /**
     * Constructor.
     *
     * @param url URL
     * @param pwa portal web app
     * @param listener managed object registry event listener
     * @param mbeanServer MBean server
     * @param factory portlet app deployment factory
     */
    public PortletApplicationDeployment(URL url, PortalWebApp pwa, ManagedObjectRegistryEventListener listener, MBeanServer mbeanServer,
            PortletAppDeploymentFactory factory) {
        super(url, pwa, listener, mbeanServer, factory);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws DeploymentException {
        // Inject services
        this.injectStandardService(Constants.CACHE_SERVICE_NAME, ICacheService.class, "osivia:service=CacheServices");
        this.injectStandardService(Constants.STATUS_SERVICE_NAME, IStatusService.class, "osivia:service=StatusServices");
        this.injectStandardService(Constants.URL_SERVICE_NAME, IPortalUrlFactory.class, "osivia:service=UrlFactory");
        this.injectStandardService(Constants.WEBID_SERVICE_NAME, IWebIdService.class, IWebIdService.MBEAN_NAME);
        this.injectStandardService(Constants.FORMATTER_SERVICE_NAME, IFormatter.class,
                "osivia:service=FormatterService");
        this.injectStandardService(Constants.NOTIFICATIONS_SERVICE_NAME, INotificationsService.class, INotificationsService.MBEAN_NAME);
        this.injectStandardService(Constants.INTERNATIONALIZATION_SERVICE_NAME, IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.injectStandardService(Constants.CONTRIBUTION_SERVICE_NAME, IContributionService.class, IContributionService.MBEAN_NAME);
        this.injectStandardService(Constants.DIRECTORY_SERVICE_LOCATOR_NAME, IDirectoryServiceLocator.class, IDirectoryServiceLocator.MBEAN_NAME);


        InputStream source = null;
        try {
            // spring
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/spring.tld"));
            this.pwa.importFile("/WEB-INF/theme", "spring.tld", source, false);
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/spring-form.tld"));
            this.pwa.importFile("/WEB-INF/theme", "spring-form.tld", source, false);

            // displaytag.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/displaytag.tld"));
            this.pwa.importFile("/WEB-INF/theme", "displaytag.tld", source, false);

            // displaytag-el.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/displaytag-el.tld"));
            this.pwa.importFile("/WEB-INF/theme", "displaytag-el.tld", source, false);
        } catch (IOException e) {
            throw new DeploymentException("Cannot import taglib", e);
        } finally {
            IOTools.safeClose(source);
        }

        //
        super.start();
    }


    /**
     * Utility method used to inject standard service.
     * @param <T>
     *
     * @param serviceName service name
     * @param serviceClass service class
     * @param serviceRef service reference
     */
    protected <T> void injectStandardService(String serviceName, Class<T> serviceClass, String serviceRef) {
        try {
            Object proxy = Locator.getService(serviceRef, serviceClass);
            this.pwa.getServletContext().setAttribute(serviceName, proxy);
        } catch (Exception e) {
            this.log.error("Was not able to create service proxy", e);
        }
    }

}
