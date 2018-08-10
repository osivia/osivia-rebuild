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
package org.jboss.portal.theme.render.renderer;

import org.jboss.portal.theme.render.ObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;

/**
 * A region renderer is responsible to create the markup container for a set of portlets on a page region. <p>The
 * Portlets on a portal page can be assigned to regions of that page. Regions are sub elements of a page, that will be
 * used by a layout to position groups of portlets over the areas of the page. The region renderer can be invoked to
 * render the entire content of the region. It is assumed to take over the responsibility of delegating to the
 * <code>WindowRenderer</code> for each portlet that needs to be rendered in the region in question.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.PortalRenderSet
 * @see WindowRenderer
 */
public interface RegionRenderer
   extends ObjectRenderer
{
   void renderBody(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException;

   void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException;

   void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException;
}
