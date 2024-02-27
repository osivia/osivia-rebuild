package org.osivia.portal.api.components;

import java.util.List;


import org.osivia.portal.api.context.PortalControllerContext;

public interface IComponentService {

    String MBEAN_NAME = "osivia:service=ComponentService";
    
    public <T> List<T> filterByComponentList(PortalControllerContext portalCtx, String componentPrefix, List<T> components, IComponentElement<T> idRetrieve) ;

}
