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
package org.jboss.portal.server;

import org.jboss.portal.common.util.Version;

import java.nio.charset.Charset;

/**
 * Defines various constants for the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12879 $
 */
public class PortalConstants
{

   /** Current version. */
   public static final Version VERSION = new Version("JBoss Portal", 2, 7, 2, new Version.Qualifier(Version.Qualifier.Prefix.GA), "Community");

   /** The default portal name. */
   public static final String DEFAULT_PORTAL_NAME = "default";

   /** . */
   public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

   /** . */
   public static final String MULTIPART_FORM_DATA = "multipart/form-data";

   /** . */
   public static final Charset UTF_8 = Charset.forName("UTF-8");

   // Portal properties

   /** . */
   public static final String PORTAL_PROP_NAME_KEY = "org.jboss.portal.property.name";

   // Session map key prefixes

   /** Generic session objects. */
   public static final String PORTAL_SESSION_MAP_KEY = "portal.session";

   /** Generic session objects. */
   public static final String PORTAL_PRINCIPAL_MAP_KEY = "portal.principal";
}
