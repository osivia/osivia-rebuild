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
package org.osivia.portal.core.container.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.core.container.persistent.PageImplBase;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer;
import org.osivia.portal.core.dynamic.DynamicWindowBean;





@SuppressWarnings("unchecked")
public abstract class DynamicPage extends PageImplBase {

	protected static final Log logger = LogFactory.getLog(DynamicPage.class);

	protected StaticPortalObjectContainer.ContainerContext containerContext;
	protected DynamicPortalObjectContainer dynamicContainer;


	Map<String, DynamicWindow> dynamicChilds;

	Map<String, DynamicPage> dynamicSubPages;

	protected Map<String, String> properties = null;

	abstract DynamicWindow createSessionWindow ( DynamicWindowBean dynamicWindowBean);

	protected Map<String, DynamicWindow> getDynamicWindows ()	{
		if( this.dynamicChilds == null){

			this.dynamicChilds = new HashMap<String, DynamicWindow>();

            for (DynamicWindowBean dynamicWindow : this.dynamicContainer.getPageWindows( this.getId())) {
				DynamicWindow child = this.createSessionWindow (dynamicWindow);
				this.dynamicChilds.put(child.getName(), child);
			}
		}
		return this.dynamicChilds;
	}

}
