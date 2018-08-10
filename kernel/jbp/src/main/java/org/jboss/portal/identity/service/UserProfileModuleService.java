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
package org.jboss.portal.identity.service;

import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.event.IdentityEvent;
import org.jboss.portal.identity.event.UserProfileChangedEvent;
import org.jboss.portal.identity.metadata.profile.info.ProfileInfoSupport;
import org.jboss.portal.identity.metadata.config.ConfigurationParser;
import org.jboss.portal.identity.info.ProfileInfo;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class UserProfileModuleService extends IdentityModuleService implements UserProfileModule
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(UserProfileModuleService.class);

   protected ProfileInfo profileInfo;

   private String profileConfigFile;

   protected UserProfileModuleService()
   {
      super(IdentityContext.TYPE_USER_PROFILE_MODULE);
   }


   public void start() throws Exception
   {
      if (getProfileConfigFile() != null)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Processing profile configuration for the module....");  
         }
         profileInfo = new ProfileInfoSupport(ConfigurationParser.parseProfileConfiguration(getProfileConfigFile()));
      }

      super.start();
      
   }

//   public ProfileInfo getProfileInfo() throws IdentityException
//   {
//      return profileInfo;
//   }

   public void setProfileInfo(ProfileInfo profileInfo)
   {
      this.profileInfo = profileInfo;
   }

   public String getProfileConfigFile()
   {
      return profileConfigFile;
   }

   public void setProfileConfigFile(String profileConfigFile)
   {
      this.profileConfigFile = profileConfigFile;
   }

   protected void fireUserProfileChangedEvent(Object userId, String userName, String propertyName, Object newValue) throws IdentityException
   {
      IdentityEvent event = new UserProfileChangedEvent(userId, userName, propertyName, newValue);
      getIdentityEventBroadcaster().fireEvent(event);
   }

}

