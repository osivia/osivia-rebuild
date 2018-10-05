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

package org.jboss.portal.theme;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.ContentInfo;

import java.util.Collection;

/**
 * Read Only information about the layout service.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 10337 $
 */
public interface LayoutServiceInfo
{
   /**
    * Get the layout for the provided registration id.
    *
    * @param id            the registration id of the layout to get
    * @param defaultOnNull true, if the default layout (if any is defined) should be returned in case the requested
    *                      layout is not found
    * @return the requested layout , of the default layout, or null
    */
   PortalLayout getLayout(ServerRegistrationID id, boolean defaultOnNull);

   /**
    * Get the layout for the provided name.
    *
    * @param name          the name of the layout to request
    * @param defaultOnNull true, if the default layout (if any is defined) should be returned in case the requested
    *                      layout is not found
    * @return the requested layout , of the default layout, or null
    */
   PortalLayout getLayout(String name, boolean defaultOnNull);

   PortalLayout getLayoutById(String layoutIdString);

   /**
    * Get the render set for the provided layout.
    *
    * @param renderSetName the name of the renderSet to find
    * @param mediaType     the mediatype for which to find the renderSet
    * @return the registered renderSet for the provided name and media type, or null if no such renderSet is registered
    */
   PortalRenderSet getRenderSet(String renderSetName, MediaType mediaType);

   /**
    * Retrieves the render set for this Layout.
    *
    * @param id        the registration id of the RenderSet to retrieve.
    * @param mediaType the media type that must be supported by the RenderSet to be retrieved
    * @return the registered RenderSet identified by the specified id and media type, <code>null</code> otherwise.
    * @since 2.4
    */
   PortalRenderSet getRenderSet(ServerRegistrationID id, MediaType mediaType);

   /**
    * Get a Set of portal layouts
    *
    * @return a Set of portal layouts
    */
   Collection getLayouts();

   /**
    * Retrieves the set of the ServerRegistrationID for registred render set
    *
    * @return the set of the ServerRegistrationID for registred render set
    */
   Collection getRenderSets();

   PortalRenderSet getRenderSet(LayoutInfo info, ContentInfo streamInfo, String renderSetName);
}
