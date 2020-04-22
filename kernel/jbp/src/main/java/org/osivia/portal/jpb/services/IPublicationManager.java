package org.osivia.portal.jpb.services;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Interface PublicationManager.
 * 
 * Handle publication logic
 */
public interface IPublicationManager {
    public PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID docId) throws ControllerException;
}
