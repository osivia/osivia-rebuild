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
 * The portlet renderer is responsible for adding the created markup of a portlet to the page. <p>The portlet renderer
 * is intended to be called by the <code>WindowRenderer</code> as part of the generation of the markup for a portlet
 * window. The portlet renderer is not responsible for generating the decoration markup, like the title. The
 * <code>DecorationRenderer</code> is responsible for that. It is also called by the <code>WindowRenderer</code> as part
 * of the render process for one portlet window.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.PortalRenderSet
 * @see WindowRenderer
 * @see DecorationRenderer
 */
public interface PortletRenderer
   extends ObjectRenderer
{
   /**
    * Render the markup of a portlet. <p>Note: this is not calling the portlet container to produce the markup. The
    * markup has already been created. The PortletRenderer only places the markup on the page, and can gnerate some
    * containing markup around it if it chooses to do so</p>.
    *
    * @param rendererContext the state holder to provide information about the region, it's portlets, and the render
    *                        set, which allows access to the other renderer interfaces of the render set.
    * @throws org.jboss.portal.theme.render.RenderException
    *
    */
   void render(RendererContext rendererContext, PortletRendererContext prc) throws RenderException;
}
