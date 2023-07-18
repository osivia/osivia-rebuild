/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.jboss.portal.core.model.instance;


import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.portal.Mode;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.impl.invocation.PortletInterceptorStackFactory;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.InsufficientPrivilegesResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.security.spi.provider.PermissionRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;


public class InstanceSecurityInterceptor extends PortletInvokerInterceptor {

    /** . */
    private Logger log = Logger.getLogger(InstanceSecurityInterceptor.class);

    /** . */
    private boolean trace = log.isTraceEnabled();

    protected DomainConfigurator domainConfigurator;


    public DomainConfigurator getDomainConfigurator() {
        return domainConfigurator;
    }


    public void setDomainConfigurator(DomainConfigurator domainConfigurator) {
        this.domainConfigurator = domainConfigurator;
    }


public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      try
      {
         // Compute the security mask
         int mask = InstancePermission.VIEW_MASK;
         Mode mode = invocation.getMode();
         if (Mode.ADMIN.equals(mode))
         {
            mask |= InstancePermission.ADMIN_MASK;
         }

         //
         String instanceid = (String)invocation.getAttribute(Instance.INSTANCE_ID_ATTRIBUTE);
         InstancePermission wantedPerm = new InstancePermission(instanceid, mask);
         
         
         //Authorizations
         
         boolean authorized = false;
         ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");
         HttpServletRequest httpServletRequest = ctx.getServerInvocation().getServerContext().getClientRequest();
         
         
        Set securityBindings = getDomainConfigurator().getSecurityBindings(instanceid);
        if (securityBindings != null) {
            for (Iterator i = securityBindings.iterator(); i.hasNext();) {
                RoleSecurityBinding sc = (RoleSecurityBinding) i.next();
                if (SecurityConstants.UNCHECKED_ROLE_NAME.equals(sc.getRoleName()) || httpServletRequest.isUserInRole(sc.getRoleName())) {
                    InstancePermission permission = new InstancePermission(instanceid, sc.getActions());
                    if (permission.implies(wantedPerm)) {
                        authorized = true;
                        break;
                    }
                }
            }
        }
        
        if(authorized == false)	{
        	PortalControllerContext portalCtx = new PortalControllerContext(httpServletRequest);
        	if( PortalObjectUtils.isPageRepositoryManager(portalCtx))	{
        		authorized = true;
        	}
        }
        
        
        

         //
         //
         if (trace)
         {
            log.trace("Access granted=" + authorized + " for instance " + instanceid);
         }
         if (!authorized)
         {
            return new InsufficientPrivilegesResponse();
         }
         else
         {
            return super.invoke(invocation);
         }
      }
      catch (PortalSecurityException e)
      {
         throw new InvocationException(e);
      }
   }
}
