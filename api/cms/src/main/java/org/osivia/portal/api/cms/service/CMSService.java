package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.UniversalID;

/**
 * CMS service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface CMSService {


    /**
     * Gets the document.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException;



}
