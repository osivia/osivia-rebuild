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
package org.jboss.portal.identity.ldap;

import org.jboss.portal.identity.service.IdentityModuleService;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.ServiceJNDIBinder;
import org.jboss.portal.identity.IdentityContext;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * Keeps configuration of connection to LDAP server
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPConnectionContext
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityModuleService.class);

   public final static String CONNECTION_POOL = "com.sun.jndi.ldap.connect.pool";
   public final static String CONNECTION_POOL_DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
   public final static String CONNECTION_POOL_INITSIZE = "com.sun.jndi.ldap.connect.pool.initsize";
   public final static String CONNECTION_POOL_MAXSIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
   public final static String CONNECTION_POOL_PREFSIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
   public final static String CONNECTION_POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
   public final static String CONNECTION_POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";

   private boolean pooling;

   private String poolingDebug;

   private String poolingInitsize;

   private String poolingMaxsize;

   private String poolingPrefsize;

   private String poolingProtocol;

   private String poolingTimeout;

   private String jndiName;

   private ServiceJNDIBinder jndiBinder;

   private String name;

   private String contextFactory;

   private String adminDN;

   private String adminPassword;

   private String protocol;

   private String authentication = "simple";

   private String host;

   private String port;

   private String externalContextJndiName;

   private IdentityContext identityContext;


   public Hashtable getEnvironment()
   {
      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, this.getContextFactory());
      env.put(Context.PROVIDER_URL, "ldap://" + getHost() + ":" + getPort());
      env.put(Context.SECURITY_AUTHENTICATION, this.getAuthentication());
      if (this.getAdminDN() != null)
         env.put(Context.SECURITY_PRINCIPAL, this.getAdminDN());
      if (this.getAdminPassword() != null)
         env.put(Context.SECURITY_CREDENTIALS, this.getAdminPassword());
      
      if (this.getProtocol() != null)
      {
         env.put(Context.SECURITY_PROTOCOL, this.getProtocol());
      }

      if (isPooling())
      {
         env.put(CONNECTION_POOL, "true");
         if (getPoolingDebug() != null)
         {
            System.setProperty(CONNECTION_POOL_DEBUG, getPoolingDebug());
         }
         if (getPoolingInitsize() != null)
         {
            System.setProperty(CONNECTION_POOL_INITSIZE, getPoolingInitsize());
         }
         if (getPoolingMaxsize() != null)
         {
            System.setProperty(CONNECTION_POOL_MAXSIZE, getPoolingMaxsize());
         }
         if (getPoolingPrefsize() != null)
         {
            System.setProperty(CONNECTION_POOL_PREFSIZE, getPoolingPrefsize());
         }
         if (getPoolingProtocol() != null)
         {
            System.setProperty(CONNECTION_POOL_PROTOCOL, getPoolingProtocol());
         }
         else
         {
            System.setProperty(CONNECTION_POOL_PROTOCOL, "plain ssl");
         }
         if (getPoolingTimeout() != null)
         {
            System.setProperty(CONNECTION_POOL_TIMEOUT, getPoolingTimeout());
         }
     }

      return env;
   }

   public LdapContext createInitialContext() throws IdentityException
   {
      try
      {
         //try to use external context
         if (getExternalContextJndiName() != null)
         {
            InitialContext iniCtx = new InitialContext();
            return (LdapContext)iniCtx.lookup(getExternalContextJndiName());
         }
         //if not construct our own one using provided options
         else
         {
            Hashtable env = getEnvironment();
            return new InitialLdapContext(env, null);
         }
      }
      catch (NamingException e)
      {
         throw new IdentityException("Unable to connect to LDAP: " + this, e);
         //return null;
      }

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

      if (identityContext != null)
      {
         identityContext.register(this, IdentityContext.TYPE_CONNECTION_CONTEXT);         
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

   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      str.append("Name: ").append(getName())
         .append(", Host: ").append(getHost())
         .append(", Port: ").append(getPort())
         .append(", Context factory: ").append(getContextFactory())
         .append(", Admin user: ").append(getAdminDN())

         // Protect admin credentials to not go in logs
         .append(", Admin password: ").append("***")
         .append(", Authentication: ").append(getAuthentication())
         .append(", Protocol: ").append(getProtocol());
      return str.toString();
   }

   //************************************
   //******* Getters and Setters ********
   //************************************
   public String getAuthentication()
   {
      if (authentication == null)
      {
         return "simple";
      }
      return authentication;
   }

   public void setAuthentication(String authentication)
   {
      this.authentication = authentication;
   }


   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getContextFactory()
   {
      return contextFactory;
   }

   public void setContextFactory(String contextFactory)
   {
      this.contextFactory = contextFactory;
   }

   public String getAdminDN()
   {
      return adminDN;
   }

   public void setAdminDN(String adminDN)
   {
      this.adminDN = adminDN;
   }

   public String getAdminPassword()
   {
      return adminPassword;
   }

   public void setAdminPassword(String adminPassword)
   {
      this.adminPassword = adminPassword;
   }

   public String getProtocol()
   {
      return protocol;
   }

   public void setProtocol(String protocol)
   {
      this.protocol = protocol;
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }


   public String getPort()
   {
      return port;
   }

   public void setPort(String port)
   {
      this.port = port;
   }

   public String getExternalContextJndiName()
   {
      return externalContextJndiName;
   }

   public void setExternalContextJndiName(String externalContextJndiName)
   {
      this.externalContextJndiName = externalContextJndiName;
   }

   public ServiceJNDIBinder getJndiBinder()
   {
      return jndiBinder;
   }

   public void setJndiBinder(ServiceJNDIBinder jndiBinder)
   {
      this.jndiBinder = jndiBinder;
   }


   public IdentityContext getIdentityContext()
   {
      return identityContext;
   }

   public void setIdentityContext(IdentityContext identityContext)
   {
      this.identityContext = identityContext;
   }

   public boolean isPooling()
   {
      return pooling;
   }

   public void setPooling(boolean pooling)
   {
      this.pooling = pooling;
   }

   public String getPoolingDebug()
   {
      return poolingDebug;
   }

   public void setPoolingDebug(String poolingDebug)
   {
      this.poolingDebug = poolingDebug;
   }

   public String getPoolingInitsize()
   {
      return poolingInitsize;
   }

   public void setPoolingInitsize(String poolingInitsize)
   {
      this.poolingInitsize = poolingInitsize;
   }

   public String getPoolingMaxsize()
   {
      return poolingMaxsize;
   }

   public void setPoolingMaxsize(String poolingMaxsize)
   {
      this.poolingMaxsize = poolingMaxsize;
   }

   public String getPoolingPrefsize()
   {
      return poolingPrefsize;
   }

   public void setPoolingPrefsize(String poolingPrefsize)
   {
      this.poolingPrefsize = poolingPrefsize;
   }

   public String getPoolingProtocol()
   {
      return poolingProtocol;
   }

   public void setPoolingProtocol(String poolingProtocol)
   {
      this.poolingProtocol = poolingProtocol;
   }

   public String getPoolingTimeout()
   {
      return poolingTimeout;
   }

   public void setPoolingTimeout(String poolingTimeout)
   {
      this.poolingTimeout = poolingTimeout;
   }
}
