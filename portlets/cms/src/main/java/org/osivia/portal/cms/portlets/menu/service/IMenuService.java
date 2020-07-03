package org.osivia.portal.cms.portlets.menu.service;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.cms.portlets.menu.model.NavigationDisplayItem;

public interface IMenuService {

    NavigationDisplayItem getDisplayItem(PortalControllerContext portalControllerContext) throws PortletException;

}
