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

import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.ObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;

/**
 * A window renderer is responsible for the creation of the markup of each portlet window in a portal page region. <p>A
 * Portlet window is the frame (the container) around the markup gernerated by the Portlet. It includes things like the
 * title, and the portlet mode and portlet window state information. The window renderer is assumed to delegate to the
 * <code>DecorationRenderer</code> to generate the title and mode and state markup, and to the
 * <code>PortletRenderer</code> to render the markup produced by the portlet. Note that the renderer do not call the
 * portlet container to execute the doView doEdit etc. method. The renderer is only responsible for placing the created
 * markup inside the page, and creating the necessary markup around it.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.PortalRenderSet
 * @see RegionRenderer
 * @see DecorationRenderer
 * @see PortletRenderer
 */
public interface WindowRenderer
   extends ObjectRenderer
{
   /**
    * Render the markup of a portlet window.
    *
    * @param rendererContext the state holder to provide information about the region, it's portlets, and the render
    *                        set, which allows access to the other renderer interfaces of the render set.
    * @throws org.jboss.portal.theme.render.RenderException
    *
    * @see RegionRenderer#renderBody
    * @see PortletRenderer#render
    * @see DecorationRenderer#render
    * @see WindowContext
    * @see WindowResult
    */
   void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException;
}
