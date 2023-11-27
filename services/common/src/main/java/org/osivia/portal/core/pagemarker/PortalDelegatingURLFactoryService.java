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
package org.osivia.portal.core.pagemarker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.DelegatingURLFactoryService;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.cache.CacheLevel;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.portalcommands.PortalCommandFactory;
import org.osivia.portal.core.tracker.ITracker;


/**
 *
 * ajout d'un mot cle /viewstate dans l'url pour associer quand c'est nécessaire
 * l'état de la page
 *
 * @author jeanseb
 *
 */

public class PortalDelegatingURLFactoryService extends DelegatingURLFactoryService {

    protected static final Log logger = LogFactory.getLog(PortalDelegatingURLFactoryService.class);



    @Override
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {


        ServerURL url = super.doMapping(controllerContext, invocation, cmd);


        boolean pageMarkerInsertion = false;
        Integer pageMarker = null;
        
        // Ressource with ID must preserve navigation context
        if (cmd instanceof InvokePortletWindowResourceCommand) {
           
           pageMarkerInsertion = true;
           
           InvokePortletWindowResourceCommand resCmd = ((InvokePortletWindowResourceCommand) cmd);
           StateString resState = resCmd.getResourceState();
           if( resState != null && resState instanceof ParametersStateString) {
               String cache = ((ParametersStateString) resState).getValue("_cacheScope");
               if( "PAGE".equals(cache))    {
                    // Cache inside current page
                    pageMarker = (Integer) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "pageFirstViewState");
               }
           }
         }



        if (pageMarkerInsertion) {
            
            if( pageMarker == null) {
                pageMarker = PageMarkerUtils.getViewState(controllerContext);
            }


            if ((url != null) && (pageMarker != null)) {
                url.setPortalRequestPath(PortalCommandFactory.VIEW_STATE_SLASH + pageMarker + url.getPortalRequestPath());
            }
        }


        return url;
    }

}
