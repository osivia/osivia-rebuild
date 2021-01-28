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
package org.osivia.portal.core.content;

import java.util.Locale;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;





public class ContentURLFactory extends URLFactoryDelegate
{

   /** . */
   private String path;

   public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd)
   {
      if (cmd == null)
      {
         throw new IllegalArgumentException("No null command accepted");
      }
      if (cmd instanceof ViewContentCommand)
      {
          ViewContentCommand cmsCommand = (ViewContentCommand)cmd;

         //
         AbstractServerURL asu = new AbstractServerURL();
         //asu.setPortalRequestPath(path);
         String contentId = cmsCommand.getContentId();

         String portalRequestPath = this.path;


         if( contentId != null)	{
        	 portalRequestPath += "/" +contentId.replaceAll(":", "/");
         }
         
         Locale locale = cmsCommand.getLocale();
         portalRequestPath += "/" +locale.toString();
         
         boolean preview = cmsCommand.isPreview();
         portalRequestPath += "/" + BooleanUtils.toStringTrueFalse(preview) ; 

         asu.setPortalRequestPath(portalRequestPath);

         return asu;
      }
      return null;
   }

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}



}

