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
package org.osivia.portal.jpb.services;

import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.NoSuchPortalObjectException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;

import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 9134 $
 */
public class PageImplMock extends PortalObjectImplMock implements Page {

	public PageImplMock() {
		this(true);
	}

	public PageImplMock(boolean hibernate) {
		super(hibernate);
	}

	public Portal getPortal() {
		PortalObject object = this;
		while (object != null && !(object instanceof Portal)) {
			object = object.getParent();
		}
		return (Portal) object;
	}

	public Page getPage(String name) {
		PortalObject child = getChild(name);
		if (child instanceof Page) {
			return (Page) child;
		}
		return null;
	}

	public Window getWindow(String name) {
		PortalObject child = getChild(name);
		if (child instanceof Window) {
			return (Window) child;
		}
		return null;
	}

	public int getType() {
		return PortalObject.TYPE_PAGE;
	}

	protected PortalObjectImplMock cloneObject() {
		PageImplMock clone = new PageImplMock();
		clone.setDeclaredPropertyMap(new HashMap(getDeclaredPropertyMap()));
		clone.setListener(getListener());
		clone.setDisplayName(getDisplayName());
		return clone;
	}

	@Override
	public void destroyChild(String name) throws NoSuchPortalObjectException, IllegalArgumentException {
	}

	@Override
	public Window createWindow(String name, ContentType contentType, String contentURI)
			throws DuplicatePortalObjectException, IllegalArgumentException {

		return null;
	}

	@Override
	public Page createPage(String name) throws DuplicatePortalObjectException, IllegalArgumentException {

		return null;
	}
}
