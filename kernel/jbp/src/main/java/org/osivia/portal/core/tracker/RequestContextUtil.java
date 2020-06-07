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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Utility functions to retrieve request scope datas
 *
 */
public class RequestContextUtil {

	public static ITracker currentTracker;

	public static ServerInvocation getServerInvocation() {

		if (currentTracker != null) {
			Stack stack = currentTracker.getStack();
			if (stack.size() > 0) {
				return (ServerInvocation) stack.get(0);
			}

			
		}
		
		return null;

	}
	
	
	   public static ControllerContext getControllerContext() {
	       
	       HttpServletRequest request = currentTracker.getHttpRequest();
	       
	        ControllerContext controllerContext = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);
	        return controllerContext;
	    }


}
