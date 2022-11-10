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
package org.osivia.portal.core.cms.sessions;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.statistics.IStatisticsService;
import org.osivia.portal.spi.cms.ICMSIntegration;
import org.osivia.portal.api.preferences.UpdateUserPreferencesService;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * This listener listens to the main portal session events to notify the CMS system of the end of the user session.
 */
public class SessionListener implements HttpSessionListener {

    public static long activeSessions = 0;

    public static String activeSessionSync = "activeSessionSync";
    
    public static Log log = LogFactory.getLog(SessionListener.class);


    /**
     * Nuxeo service.
     */
    private final ICMSIntegration integrationService;
    /**
     * Statistics service.
     */
    private final IStatisticsService statisticsService;
    /**
     * Update user preferences service.
     */
    private  UpdateUserPreferencesService updateUserPreferencesService = null;


    /**
     * Constructor.
     */
    public SessionListener() {
        super();

        // Nuxeo service
        this.integrationService = Locator.findMBean(ICMSIntegration.class, "osivia:service=NuxeoService");
        

        // Statistics service
        this.statisticsService = Locator.findMBean(IStatisticsService.class, IStatisticsService.MBEAN_NAME);

        
    }

    private  UpdateUserPreferencesService getUpdateUserPreferencesService()  {
        if( this.updateUserPreferencesService == null)
            this.updateUserPreferencesService = DirServiceFactory.getService(UpdateUserPreferencesService.class);
        return this.updateUserPreferencesService;
           
    }
    

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        synchronized (activeSessionSync) {
            activeSessions++;
        }
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        synchronized (activeSessionSync) {
            activeSessions--;
        }

        this.integrationService.sessionDestroyed(sessionEvent);

        
        // HTTP session
        HttpSession httpSession = sessionEvent.getSession();
        
        

        try {
        	if( BooleanUtils.toBoolean(System.getProperty("portal.user.preferences"))) {
        		this.statisticsService.aggregateUserStatistics(httpSession);
        		getUpdateUserPreferencesService().update(httpSession);
        	}
        } catch (Exception e) {
            log.error("Update preferences ", e);
        }
        
    }

}
