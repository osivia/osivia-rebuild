package org.osivia.portal.jpb.services;

import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;

/**
 * A factory for creating CMSPage objects associated to CMS document.
 */
public interface CMSPageFactory {

    void createCMSWindows(CMSPage page, Map windows);

    public List<PortalObjectId> getTemplatesID(CMSPage page);

}
