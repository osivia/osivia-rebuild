package org.osivia.portal.core.theming.attributesbundle;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

public interface IHeaderMetadatasAttributesBundle {
    
    public String getTitle(PortalControllerContext portalControllerContext, Page page) throws PortalException ;

}
