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
package org.osivia.portal.core.tracker;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.page.PageProperties;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;


public class ServerTrackerInterceptor extends ServerInterceptor {

    protected static final Log logger = LogFactory.getLog(ServerTrackerInterceptor.class);


    private ITracker tracker;


    public PortalObjectContainer portalObjectContainer;


    public ITracker getTracker() {
        return this.tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }


    @Override
    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {

        // réinitialisation des propriétes des windows
        PageProperties.init();

        DynamicPortalObjectContainer.clearCache();

        this.getTracker().init();

        this.getTracker().pushState(invocation);

        ServerInvocationContext context = invocation.getServerContext();

        this.getTracker().setHttpRequest(context.getClientRequest());

        HttpSession session = context.getClientRequest().getSession(true);

        this.getTracker().setHttpSession(session);


        try {
            // Continue invocation
            invocation.invokeNext();
        }

        finally {
            this.getTracker().popState();

        }

        PageProperties.remove();
        this.getTracker().remove();
        DynamicPortalObjectContainer.removeCache();


    }


}
