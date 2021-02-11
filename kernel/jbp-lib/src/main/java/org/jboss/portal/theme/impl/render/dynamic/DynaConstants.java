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
package org.jboss.portal.theme.impl.render.dynamic;

/**
 * todo: 1/ dirty view state detections 2/ clarify dyna options inheritance semantics 3/ handle security direction
 * provided by the server when the user is not authenticated anymore 4/ disabling based on the user agent capabilities
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class DynaConstants
{

   /** The base url property for the resources. */
   public static final String RESOURCE_BASE_URL = "theme.dyna.resource_base_url";

   /** The base server URL. */
   public static final String SERVER_BASE_URL = "theme.dyna.server_base_url";

   /** The id for view state. */
   public static final String VIEW_STATE = "theme.dyna.view_state";
   
   /** The session check for ajax. */
   public static final String SESSION_CHECK = "theme.dyna.session_check";

   /** . */
   public static final String RENDER_OPTIONS = "dyna_render_options";

   /** . */
   public static final String RENDER_STATUS = "dyna_render_status";

}
