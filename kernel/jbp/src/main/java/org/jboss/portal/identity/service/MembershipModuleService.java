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

import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.event.MembershipChangedEvent;
import org.jboss.portal.identity.info.ProfileInfo;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public abstract class MembershipModuleService extends IdentityModuleService implements MembershipModule {
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(MembershipModuleService.class);

   private ProfileInfo profileInfo;

   protected MembershipModuleService()
   {
      super(IdentityContext.TYPE_MEMBERSHIP_MODULE);
   }

   public ProfileInfo getProfileInfo()
   {
      return profileInfo;
   }

   public void setProfileInfo(ProfileInfo profileInfo)
   {
      this.profileInfo = profileInfo;
   }

   protected void fireMembershipChangedEvent(Set userIds, Set roleIds) throws IdentityException
   {
      MembershipChangedEvent event = new MembershipChangedEvent(userIds, roleIds);
      getIdentityEventBroadcaster().fireEvent(event);      
   }

   protected void fireMembershipChangedEvent(User user, Set roles) throws IdentityException
   {

      Set roleIds = new HashSet();
      Set userIds = new HashSet();

      userIds.add(user.getId());

      for (Iterator iterator = roles.iterator(); iterator.hasNext();)
      {
         Role role = (Role)iterator.next();
         roleIds.add(role.getId());
      }

      fireMembershipChangedEvent(userIds, roleIds);
   }

   protected void fireMembershipChangedEvent(Role role, Set users) throws IdentityException
   {

      Set roleIds = new HashSet();
      Set userIds = new HashSet();

      roleIds.add(role.getId());

      for (Iterator iterator = users.iterator(); iterator.hasNext();)
      {
         User user = (User)iterator.next();
         userIds.add(user.getId());
      }

      fireMembershipChangedEvent(userIds, roleIds);
   }


}
