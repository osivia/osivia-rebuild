package org.osivia.portal.jpb.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

import org.jboss.portal.security.spi.provider.AuthorizationDomain;

public class InstanceContainerImpl implements InstanceContainer {


    private Map<String, InstanceDefinitionImpl> instances;
	/** The container context. */
	protected InstanceContainerContextImpl containerContext;
	protected PortletInterceptorStackFactory stackFactory;
	protected PortletInvoker portletInvoker;



	@PostConstruct
	protected void startService() throws Exception {

		//
		containerContext.setContainer(this);
		instances = Collections.synchronizedMap(new HashMap());
		
//		InstanceDefinitionImpl sampleDef = new InstanceDefinitionImpl(containerContext, "SampleInstance",
//				"local./osivia-portal-portlets-sample-5.0.0-SNAPSHOT.Sample");
//		
//		instances.put( sampleDef.getInstanceId(), sampleDef);
		
		InstanceDefinitionImpl sampleRemote = new InstanceDefinitionImpl(containerContext, "SampleRemote",
				"local./samples-remotecontroller-portlet.RemoteControl");		
		
		instances.put( sampleRemote.getInstanceId(), sampleRemote);		
		
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

		return instances.get(id);
	}

	@Override
	public InstanceDefinition createDefinition(String id, String portletId)
			throws DuplicateInstanceException, IllegalArgumentException, PortletInvokerException {
		return null;
	}

	@Override
	public InstanceDefinition createDefinition(InstanceMetaData instanceMetaData)	{
		
		InstanceDefinitionImpl instance = new InstanceDefinitionImpl(containerContext, instanceMetaData.getId(),
				instanceMetaData.getPortletRef());
		
		instances.put( instance.getInstanceId(), instance);
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
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<InstanceDefinition> getDefinitions() {
		return null;
	}

	@Override
	public AuthorizationDomain getAuthorizationDomain() {
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
