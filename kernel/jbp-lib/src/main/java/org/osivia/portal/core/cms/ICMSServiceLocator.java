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
package org.osivia.portal.core.cms;

public interface ICMSServiceLocator {
	
	String MBEAN_NAME = "osivia:service=CmsServiceLocator";

	void register(ICMSService service);
	
	ICMSService getCMSService();
	
	void register(String repositoryName, ICMSService service);
	    
	ICMSService getCMSService(String repositoryName) ;

}
