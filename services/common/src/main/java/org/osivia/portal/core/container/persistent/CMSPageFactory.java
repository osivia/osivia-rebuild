package org.osivia.portal.core.container.persistent;

import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.cms.exception.CMSException;

/**
 * A factory for creating CMSPage objects associated to CMS document.
 */
public interface CMSPageFactory {

    void createCMSWindows(CMSPage page, Map windows) throws CMSException;

    public List<PortalObjectId> getTemplatesID(CMSPage page) throws CMSException;

}
