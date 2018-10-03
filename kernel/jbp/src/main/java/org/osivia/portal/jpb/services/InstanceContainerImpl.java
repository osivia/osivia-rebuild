package org.osivia.portal.jpb.services;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.jboss.portal.core.impl.model.instance.InstanceContainerContext;
import org.jboss.portal.core.impl.model.instance.JBossInstanceContainerContext;
import org.jboss.portal.core.model.instance.DuplicateInstanceException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.NoSuchInstanceException;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.impl.invocation.PortletInterceptorStack;
import org.jboss.portal.portlet.impl.invocation.PortletInterceptorStackFactory;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;

public class InstanceContainerImpl implements InstanceContainer {

	private InstanceDefinitionImpl sampleDef;

	/** The container context. */
	protected InstanceContainerContextImpl containerContext;
	protected PortletInterceptorStackFactory stackFactory;
	protected PortletInvoker portletInvoker;

	@PostConstruct
	void build() {
		sampleDef = new InstanceDefinitionImpl(containerContext, "sample-instance",
				"local./osivia-portal-portlets-sample.Sample");
	}

	@PostConstruct
	protected void startService() throws Exception {

		//
		containerContext.setContainer(this);
	}

	public InstanceContainerContext getContainerContext() {
		return containerContext;
	}

	public void setContainerContext(InstanceContainerContextImpl containerContext) {
		this.containerContext = containerContext;
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

		return sampleDef;
	}

	@Override
	public InstanceDefinition createDefinition(String id, String portletId)
			throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstanceDefinition createDefinition(InstanceMetaData instanceMetaData)
			throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstanceDefinition createDefinition(String id, String portletId, boolean clone)
			throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroyDefinition(String id)
			throws NoSuchInstanceException, PortletInvokerException, IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<InstanceDefinition> getDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthorizationDomain getAuthorizationDomain() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 public  PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException
	   {
	      PortletInterceptorStack stack = stackFactory.getInterceptorStack();
	      if (stack.getLength() != 0)
	      {
	         try
	         {
	            return stack.getInterceptor(0).invoke(invocation);
	         }
	         catch (Exception e)
	         {
	            if (e instanceof PortletInvokerException)
	            {
	               throw (PortletInvokerException)e;
	            }
	            else if (e instanceof RuntimeException)
	            {
	               throw (RuntimeException)e;
	            }
	            else
	            {
	               throw new PortletInvokerException(e);
	            }
	         }
	      }

	      return portletInvoker.invoke(invocation);
	   }

}
