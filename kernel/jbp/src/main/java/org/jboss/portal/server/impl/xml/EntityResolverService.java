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
package org.jboss.portal.server.impl.xml;

import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class EntityResolverService extends AbstractJBossService implements EntityResolver
{

   /** . */
   private Element dtdMapping;

   /** . */
   private Properties dtdMappingProps;

   public Element getDTDMapping()
   {
      return dtdMapping;
   }

   public void setDTDMapping(Element dtdMapping)
   {
      this.dtdMapping = dtdMapping;

      //
      if (this.dtdMapping != null)
      {
         dtdMappingProps = XMLTools.loadXMLProperties(dtdMapping);
      }
      else
      {
         dtdMappingProps = null;
      }
   }

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
   {

      // Let the parser open a regular URI connection to systemId
      if (publicId == null)
      {
         return null;
      }

      //
      String dtdResourceName = (String)dtdMappingProps.get(publicId);
      if (dtdResourceName != null)
      {

         log.debug("Looking up resource " + dtdResourceName + " for dtd publicId=" + publicId + ", systemId=" + systemId);
         InputStream dtdStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dtdResourceName);
         if (dtdStream != null)
         {
            return new InputSource(dtdStream);
         }
         else
         {
            log.debug("No resource found for dtd publicId=" + publicId + ", systemId=" + systemId);
         }
      }
      else
      {
         log.debug("No resource name found for dtd publicId=" + publicId + ", systemId=" + systemId);
      }

      //
      return null;
   }

   public String toString()
   {
      return "EntityResolver[" + getServiceName() + "]";
   }
}
