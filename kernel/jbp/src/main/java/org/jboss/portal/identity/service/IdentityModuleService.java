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
import org.jboss.portal.identity.IdentityConfiguration;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.ServiceJNDIBinder;
import org.jboss.portal.identity.event.IdentityEventBroadcaster;

import java.util.Map;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class IdentityModuleService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityModuleService.class);

   private String jndiName;

   private String connectionJNDIName;

   private IdentityContext identityContext;

   private IdentityConfiguration identityConfiguration;

   private ServiceJNDIBinder jndiBinder;

   private Map initOptions;

   protected String moduleType;

   //restrict instance creation
   private IdentityModuleService()
   {

   }

   protected IdentityModuleService(String type)
   {
      moduleType = type;
   }

   public void start() throws Exception
   {

      //
      if (jndiName != null && jndiBinder != null)
      {
         log.debug("Binding identity module to JNDI with name: " + jndiName);
         //jndiBinding = new JNDI.Binding(jndiName, this);
         //jndiBinding.bind();
         jndiBinder.bind(jndiName, this);
      }

      if (identityContext == null)
      {
         throw new IdentityException("Cannot register module in context - missing reference");
      }
      else
      {
         //identityContext.register(this, IdentityContext.TYPE_ROLE_MODULE);
         identityContext.register(this, this.moduleType);
      }
   }


   public void stop() throws Exception
   {
      if (jndiName != null && jndiBinder != null)
      {
         //jndiBinding.unbind();
         //jndiBinding = null;
         jndiBinder.unbind(jndiName);
      }

      if (identityContext == null)
      {
         log.error("Cannot unregister module in context - missing reference");
      }
      else
      {
         //identityContext.unregister(IdentityContext.TYPE_ROLE_MODULE);
         identityContext.unregister(this.moduleType);
      }

   }

   protected IdentityConfiguration getIdentityConfiguration() //throws IdentityException
   {
      /*if (identityConfiguration == null)
      {
         this.identityConfiguration = (IdentityConfiguration)identityContext.getObject(IdentityContext.TYPE_IDENTITY_CONFIGURATION);
      }
      return identityConfiguration;*/
      try
      {
         if (identityConfiguration == null)
         {
            this.identityConfiguration = (IdentityConfiguration)identityContext.getObject(IdentityContext.TYPE_IDENTITY_CONFIGURATION);
         }
         return identityConfiguration;
      }
      catch (IdentityException e)
      {
         throw new RuntimeException("Can't obtain IdentityConfiguration", e);
      }
   }

   protected IdentityEventBroadcaster getIdentityEventBroadcaster() throws IdentityException
   {
      return (IdentityEventBroadcaster)getIdentityContext().getObject(IdentityContext.TYPE_IDENTITY_EVENT_BROADCASTER);
   }

   public IdentityContext getIdentityContext()
   {
      return identityContext;
   }

   public void setIdentityContext(IdentityContext identityContext)
   {
      this.identityContext = identityContext;
   }

   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String JNDIName)
   {
      this.jndiName = JNDIName;
   }

   public Map getInitOptions()
   {
      return initOptions;
   }


   public void setInitOptions(Map initOptions)
   {
      this.initOptions = initOptions;
   }


   public String getModuleType()
   {
      return moduleType;
   }


   public void setModuleType(String moduleType)
   {
      this.moduleType = moduleType;
   }

   public String getConnectionJNDIName()
   {
      return connectionJNDIName;
   }

   public void setConnectionJNDIName(String connectionJNDIName)
   {
      this.connectionJNDIName = connectionJNDIName;
   }

   public ServiceJNDIBinder getJndiBinder()
   {
      return jndiBinder;
   }

   public void setJndiBinder(ServiceJNDIBinder jndiBinder)
   {
      this.jndiBinder = jndiBinder;
   }
}
