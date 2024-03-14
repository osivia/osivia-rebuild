/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.core.controller.command;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SignOutResponse;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;

/**
 * A global signout.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8786 $
 */
public class SignOutCommand extends ControllerCommand
{

   /** . */
   private static final CommandInfo info = new ActionCommandInfo(false);

   /** . */
   private String location;

   public SignOutCommand()
   {
      this(null);
   }

   public SignOutCommand(String location)
   {
      this.location = location;
   }

   public CommandInfo getInfo()
   {
      return info;
   }

   public String getLocation()
   {
      return location;
   }

   public ControllerResponse execute() throws ControllerException
   {
      if( location == null) {
          ControllerContext controllerContext = this.getControllerContext();

          PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
 
          // Apply black list
          UniversalID hostID = PortalObjectUtils.getHostPortalID(portalCtx.getHttpServletRequest());
          
          if( hostID != null) {

              String propertyName = "osivia.cms.repository."+hostID.getRepositoryName()+".logout.url";
              String locationProp = System.getProperty(propertyName);
              if( StringUtils.isNotEmpty(locationProp)) {
                  location = locationProp;
              }
          }   


      }
       
      if (location != null)
      {
         return new SignOutResponse(location);
      }
      else
      {
         return new SignOutResponse();
      }
   }
}
