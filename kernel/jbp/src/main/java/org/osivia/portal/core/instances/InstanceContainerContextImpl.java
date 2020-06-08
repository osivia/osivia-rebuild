package org.osivia.portal.core.instances;

import java.util.Collection;
import java.util.Set;

import org.jboss.portal.core.impl.model.instance.AbstractInstance;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceCustomization;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceDefinition;

import org.jboss.portal.core.impl.model.instance.JBossInstanceContainerContext;
import org.jboss.portal.core.model.instance.DuplicateInstanceException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.InstancePermission;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.portlet.PortletContext;

public class InstanceContainerContextImpl implements JBossInstanceContainerContext {
	
	private InstanceContainerImpl container;

	@Override
	public Collection<InstanceDefinition> getInstanceDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInstanceDefinition getInstanceDefinition(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInstanceDefinition newInstanceDefinition(String id, String portletRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInstanceDefinition newInstanceDefinition(InstanceMetaData instanceMetaData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createInstanceDefinition(AbstractInstanceDefinition instanceDef) throws DuplicateInstanceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyInstanceDefinition(AbstractInstanceDefinition instanceDef) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyInstanceCustomization(AbstractInstanceCustomization customization) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractInstanceCustomization getCustomization(AbstractInstanceDefinition instanceDef,
			String customizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInstanceCustomization newInstanceCustomization(AbstractInstanceDefinition def, String id,
			PortletContext portletContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createInstanceCustomizaton(AbstractInstanceCustomization customization) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateInstance(AbstractInstance instance, PortletContext portletContext, boolean mutable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateInstance(AbstractInstance instance, PortletContext portletContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateInstanceDefinition(AbstractInstanceDefinition def, Set securityBindings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkPermission(InstancePermission perm) {
		// TODO Auto-generated method stub
		return false;
	}





	   public InstanceContainerImpl getContainer()
	   {
	      return container;
	   }

	   public void setContainer(InstanceContainerImpl container)
	   {
	      this.container = container;
	   }

	@Override
	public void setContainer(InstanceContainer container) {
		this.container = (InstanceContainerImpl) container;
		
	}

}
