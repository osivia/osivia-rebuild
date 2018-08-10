/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity.metadata.config;

import org.jboss.portal.identity.metadata.profile.ProfileMetaData;
import org.jboss.portal.identity.metadata.profile.ProfileMetaDataFactory;
import org.jboss.portal.identity.IdentityException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.ObjectModelFactory;

import java.net.URL;
import java.io.InputStream;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class ConfigurationParser
{

   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(ConfigurationParser.class);


   //TODO: merge this two methods (redundancy)

    /**
    * Parse identity config file
    * @param configFile
    * @return
    * @throws org.jboss.portal.identity.IdentityException
     */
   public static ProfileMetaData parseProfileConfiguration(String configFile) throws IdentityException
    {
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      ProfileMetaData meta;
      try
      {
         log.info("Processing identity profile configuration");
         if (log.isDebugEnabled())
         {
            log.debug("config file: " + configFile);
         }
         URL config = tcl.getResource(configFile);
         InputStream in = config.openStream();
         // create unmarshaller
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();

         // create an instance of ObjectModelFactory
         ObjectModelFactory factory = new ProfileMetaDataFactory();

         // let the object model factory to create an instance of Map and populate it with data from XML
         meta = (ProfileMetaData)unmarshaller.unmarshal(in, factory, null);

         // close the XML stream
         in.close();
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot parse identity profile configuration file", e);
      }
      return meta;
   }

   public static IdentityConfigurationMetaData parseIdentityConfiguration(String configFile) throws IdentityException
   {
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      IdentityConfigurationMetaData meta;
      try
      {
         log.info("Processing portal identity configuration");
         log.debug("config file: " + configFile);
         URL config = tcl.getResource(configFile);
         InputStream in = config.openStream();
         // create unmarshaller
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();

         // create an instance of ObjectModelFactory
         ObjectModelFactory factory = new IdentityConfigurationMetaDataFactory();

         // let the object model factory to create an instance of Map and populate it with data from XML
         meta = (IdentityConfigurationMetaData)unmarshaller.unmarshal(in, factory, null);

         // close the XML stream
         in.close();
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot parse identity configuration file", e);
      }
      return meta;
   }
}
