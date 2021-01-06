package org.osivia.portal.api.cms.service;

import java.util.List;

import org.osivia.portal.api.cms.model.Document;

/**
 * The Interface CMSEvent.
 */
public interface CMSEvent {
    
    /**
     * Gets the source document.
     *
     * @return the source document
     */
    public Document getSourceDocument();
    
    /**
     * Gets the dirty request.
     *
     * @return the dirty request
     */
    public List<Request> getDirtyRequests();

}