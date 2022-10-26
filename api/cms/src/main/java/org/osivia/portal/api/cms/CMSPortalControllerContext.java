package org.osivia.portal.api.cms;

import org.osivia.portal.api.context.PortalControllerContext;

public class CMSPortalControllerContext {
	
	private final PortalControllerContext portalControllerContext;
	
	private final boolean isUserRefreshingPage;

	public CMSPortalControllerContext(PortalControllerContext portalControllerContext, boolean isUserRefreshingPage) {
		super();
		this.portalControllerContext = portalControllerContext;
		this.isUserRefreshingPage = isUserRefreshingPage;
	}

	public PortalControllerContext getPortalControllerContext() {
		return portalControllerContext;
	}

	public boolean isUserRefreshingPage() {
		return isUserRefreshingPage;
	}

}
