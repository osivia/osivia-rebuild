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
package org.osivia.portal.core.user;

import org.jboss.logging.Logger;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.p3p.P3PConstants;
import org.jboss.portal.identity.CachedUserImpl;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The interceptor is responsible for managing the user identity lifecycle based on the principal name returned by the
 * <code>HttpServletRequest.getUserPrincipal()</code> method.
 * <p/>
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12465 $
 */
public class MinimalUserInterceptor extends ServerInterceptor
{

   /** . */
   public static final String PROFILE_KEY = "profile";

   /** . */
   public static final String USER_KEY = "user";

   /** Our logger. */
   private static final Logger log = Logger.getLogger(MinimalUserInterceptor.class);

   /** User. */
   protected UserModule userModule = null;

   /** UserProfile */
   protected UserProfileModule userProfileModule = null;

   /** . */
   protected boolean cacheUser = true;



   public void setUserModule(UserModule userModule)
   {
      this.userModule = userModule;
   }

   protected void invoke(ServerInvocation invocation) throws Exception, InvocationException
   {
      boolean trace = log.isTraceEnabled();
      HttpServletRequest req = invocation.getServerContext().getClientRequest();

      // Get scope
      AttributeResolver principalScopeResolver = invocation.getContext().getAttributeResolver(ServerInvocation.PRINCIPAL_SCOPE);

      // Get the id
      Principal userPrincipal = req.getUserPrincipal();

      // The user and its profile
      User user = null;
      Map<String, String> profile = null;

      // Fetch user if we can
      if (userPrincipal != null)
      {
         String userName = userPrincipal.getName();

         //
         try
         {
            if (trace)
            {
               log.trace("About to fetch user=" + userName);
            }

            // Try to obtain cached user
            user = (User)principalScopeResolver.getAttribute(USER_KEY);

            //
            if (user == null)
            {
               // Fetch user info
               //user = getUserModule().findUserByUserName(userName);
               // Build detached pojo
               user = new CachedUserImpl(userPrincipal.getName(), userPrincipal.getName());

               // Cache
               invocation.getContext().setAttribute(ServerInvocation.PRINCIPAL_SCOPE, USER_KEY, user);

               // Get a detached object
               profile = new HashMap();

               // Cache
               invocation.getContext().setAttribute(ServerInvocation.PRINCIPAL_SCOPE, PROFILE_KEY, profile);


            }

            //
            if (trace)
            {
               log.trace("Found user=" + userName);
            }
         }
         catch (Exception e)
         {
            log.error("Cannot retrieve user=" + userName, e);
            throw new InvocationException("Cannot fetch user=" + userName, e);
         }
      }

      try
      {
         // Continue the invocation
         invocation.invokeNext();
      }
      finally
      {
         if (!cacheUser)
         {
            principalScopeResolver.setAttribute(USER_KEY, null);
            principalScopeResolver.setAttribute(PROFILE_KEY, null);
         }
      }
   }

   public boolean isCacheUser()
   {
      return cacheUser;
   }

   public void setCacheUser(boolean cacheUser)
   {
      this.cacheUser = cacheUser;
   }

}
