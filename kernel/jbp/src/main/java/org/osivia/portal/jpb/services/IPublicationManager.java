package org.osivia.portal.jpb.services;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.model.Document;

/**
 * The Interface PublicationManager.
 * 
 * Handle publication logic
 */
public interface IPublicationManager {
    PortalObjectId getPageTemplate( CMSContext ctx, Document doc) throws ControllerException;
}
