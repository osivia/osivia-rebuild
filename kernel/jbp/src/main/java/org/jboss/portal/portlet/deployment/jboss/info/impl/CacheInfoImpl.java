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
package org.jboss.portal.portlet.deployment.jboss.info.impl;

import org.apache.log4j.Logger;
import org.jboss.portal.portlet.deployment.jboss.metadata.JBossPortletMetaData;
import org.jboss.portal.portlet.impl.metadata.portlet.PortletMetaData;
import org.jboss.portal.portlet.info.CacheInfo;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6697 $
 */
public class CacheInfoImpl implements CacheInfo
{

   /** Cache uses strong references. */
   public static int REF_STRONG = 0;

   /** Cache uses soft references. */
   public static int REF_SOFT = 1;

   /** . */
   private static Logger log = Logger.getLogger(CacheInfoImpl.class);

   /** . */
   private int expirationTimeSecs;

   /** . */
   private int referenceType;

   public CacheInfoImpl(PortletMetaData portletMD, JBossPortletMetaData jbossPortletMD)
   {
      expirationTimeSecs = portletMD.getExpirationCache();
      if (expirationTimeSecs < 0 && expirationTimeSecs != -1)
      {
         log.warn("Seen bad caching expiration value " + expirationTimeSecs + " disable caching instead");
         expirationTimeSecs = 0;
      }

      referenceType = REF_STRONG;
      if (jbossPortletMD != null)
      {
         referenceType = jbossPortletMD.getCacheRefType() == null ? REF_STRONG : jbossPortletMD.getCacheRefType().intValue();
      }
   }

   public int getExpirationSecs()
   {
      return expirationTimeSecs;
   }

   public int getReferenceType()
   {
      return referenceType;
   }
}
