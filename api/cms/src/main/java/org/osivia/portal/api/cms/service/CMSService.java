package org.osivia.portal.api.cms.service;

import javax.servlet.ServletRequest;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;

/**
 * CMS service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface CMSService {

    /**
     * Get document from servlet request.
     *
     * @param servletRequest servlet request
     * @return document
     * @throws CMSException
     */
    Document getDocument(ServletRequest servletRequest) throws CMSException;

}
