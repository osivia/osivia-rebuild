package org.osivia.portal.core.instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.portal.core.impl.model.instance.AbstractInstanceDefinition;
import org.jboss.portal.core.impl.model.instance.InstanceContainerContext;
import org.jboss.portal.core.model.instance.DuplicateInstanceException;
import org.jboss.portal.core.model.instance.Instance;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.InstancePermission;
import org.jboss.portal.core.model.instance.NoSuchInstanceException;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.impl.invocation.PortletInterceptorStack;
import org.jboss.portal.portlet.impl.invocation.PortletInterceptorStackFactory;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.security.PortalPermission;
import org.jboss.portal.security.PortalPermissionCollection;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.security.spi.provider.PermissionFactory;
import org.jboss.portal.security.spi.provider.PermissionRepository;
import org.jboss.portal.security.spi.provider.SecurityConfigurationException;

/**
 * This container manages instances definitions in a memory space
 */

public class InstanceContainerImpl extends AbstractJBossService
        implements InstanceContainer, AuthorizationDomain, DomainConfigurator, PermissionRepository, PermissionFactory {


    /** The container context. */
    protected InstanceContainerContextImpl containerContext;
    protected PortletInterceptorStackFactory stackFactory;
    protected PortletInvoker portletInvoker;




    public InstanceContainerImpl() {

    }

  

    protected void startService() throws Exception {

        //
        containerContext.setContainer(this);




    }

    protected void stopService() throws Exception {
        //
        containerContext.setContainer(null);


    }

    public PortalPermission createPermissionContainer(PortalPermissionCollection collection)
            throws PortalSecurityException {
        return new InstancePermission(collection);
    }

    public PortalPermission createPermission(String uri, String action) throws PortalSecurityException {
        return new InstancePermission(uri, action);
    }

    public PortalPermission createPermission(String uri, Collection actions) throws PortalSecurityException {
        return new InstancePermission(uri, actions);
    }

    public InstanceContainerContext getContainerContext() {
        return containerContext;
    }

    public void setContainerContext(InstanceContainerContext containerContext) {
        this.containerContext = (InstanceContainerContextImpl) containerContext;
    }

    public PortletInterceptorStackFactory getStackFactory() {
        return stackFactory;
    }

    public void setStackFactory(PortletInterceptorStackFactory stackFactory) {
        this.stackFactory = stackFactory;
    }

    public PortletInvoker getPortletInvoker() {
        return portletInvoker;
    }

    public void setPortletInvoker(PortletInvoker portletInvoker) {
        this.portletInvoker = portletInvoker;
    }

    @Override
    public InstanceDefinition getDefinition(String id) throws IllegalArgumentException {
          if (id == null)
          {
             throw new IllegalArgumentException("id cannot be null");
          }
          return containerContext.getInstanceDefinition(id);
    }

    @Override
    public InstanceDefinition createDefinition(String id, String portletId)
            throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
        return null;
    }

    @Override
    public InstanceDefinition createDefinition(InstanceMetaData instanceMetaData) throws DuplicateInstanceException {
        InstanceDefinitionImpl instance = new InstanceDefinitionImpl(containerContext, instanceMetaData);
        containerContext.createInstanceDefinition(instance);
        return instance;
    }
    



    @Override
    public InstanceDefinition createDefinition(String id, String portletId, boolean clone)
            throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
        return null;
    }

    @Override
    public void destroyDefinition(String id)
            throws NoSuchInstanceException, PortletInvokerException, IllegalArgumentException {

          if (id == null)
          {
             throw new IllegalArgumentException("id cannot be null");
          }
          AbstractInstanceDefinition instance = containerContext.getInstanceDefinition(id);
          
          containerContext.destroyInstanceDefinition(instance);
    }

    @Override
    public Collection<InstanceDefinition> getDefinitions() {

        Collection<InstanceDefinition> list = containerContext.getInstanceDefinitions();

        /*
        // Filter the list
        Collection<InstanceDefinition> list = new ArrayList<>();
        list.addAll(impls);
        
        //
        for (Iterator i = list.iterator(); i.hasNext();) {
            Instance instance = (Instance) i.next();
            InstancePermission perm = new InstancePermission(instance.getId(), InstancePermission.VIEW_ACTION);
            if (!mgr.checkPermission(perm)) {
                i.remove();
            }
        }
*/
        //
        return list;
    }

    public AuthorizationDomain getAuthorizationDomain() {
        return this;
    }

    public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException {
        PortletInterceptorStack stack = stackFactory.getInterceptorStack();
        if (stack.getLength() != 0) {
            try {
                return stack.getInterceptor(0).invoke(invocation);
            } catch (Exception e) {
                if (e instanceof PortletInvokerException) {
                    throw (PortletInvokerException) e;
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new PortletInvokerException(e);
                }
            }
        }

        return portletInvoker.invoke(invocation);
    }

    public String getType() {
        return InstancePermission.PERMISSION_TYPE;
    }

    public DomainConfigurator getConfigurator() {
        return this;
    }

    public PermissionRepository getPermissionRepository() {
        return this;
    }

    public PermissionFactory getPermissionFactory() {
        return this;
    }

    public Set getSecurityBindings(String uri) {

        InstanceDefinitionImpl instanceDef = (InstanceDefinitionImpl) containerContext.getInstanceDefinition(uri);

        if (instanceDef != null) {
            return instanceDef.getSecurityBindings();
        }

        return null;

    }

    public void setSecurityBindings(String uri, Set securityBindings) throws SecurityConfigurationException {

        InstanceDefinitionImpl instanceDef = (InstanceDefinitionImpl) containerContext.getInstanceDefinition(uri);

        //
        if (instanceDef == null) {
            throw new SecurityConfigurationException(
                    "The object should exist prior its security is configured : fixme");
        }

        //
        Set tmp = new HashSet(securityBindings.size());
        for (Iterator i = securityBindings.iterator(); i.hasNext();) {
            RoleSecurityBinding sc = (RoleSecurityBinding) i.next();

            // Optimize
            if (sc.getActions().size() > 0) {
                tmp.add(sc);
            }
        }

        instanceDef.getRelatedSecurityBindings().clear();

        for (Iterator i = securityBindings.iterator(); i.hasNext();) {
            RoleSecurityBinding sc = (RoleSecurityBinding) i.next();

            //
            MemoryInstanceSecurityBinding isc = new MemoryInstanceSecurityBinding(sc.getActions(), sc.getRoleName());

            // Create association
            instanceDef.getRelatedSecurityBindings().put(sc.getRoleName(), isc);
        }
    }

    public void removeSecurityBindings(String uri) throws SecurityConfigurationException {
        InstanceDefinitionImpl instanceDef = (InstanceDefinitionImpl) containerContext.getInstanceDefinition(uri);

        if (instanceDef == null) {
            throw new SecurityConfigurationException("The object should exist prior its security is removed : fixme");
        }
        instanceDef.getRelatedSecurityBindings().clear();
    }

    public PortalPermission getPermission(String roleName, String uri) throws PortalSecurityException {

        Set set = getSecurityBindings(uri);
        if (set != null && !set.isEmpty()) {
            for (Iterator i = set.iterator(); i.hasNext();) {
                RoleSecurityBinding sc = (RoleSecurityBinding) i.next();
                String constraintRoleName = sc.getRoleName();
                if (constraintRoleName.equals(roleName)) {
                    return createPermission(uri, sc.getActions());
                }
            }
        }
        return null;

    }
}
