package org.osivia.portal.api.cms.repository.model;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.Request;


public class RepositoryEvent implements CMSEvent {

    /** The src. */
    private final Document src;
    
    private final List<Request> dirtyRequests;
    
    /**
     * Instantiates a new CMS event impl.
     *
     * @param src the src
     */
    public RepositoryEvent(Document src, List<Request> dirtyRequests) {
        super();
        this.src = src;
        this.dirtyRequests = dirtyRequests;
    }
    


    @Override
    public Document getSourceDocument() {
        return src;
    }

    @Override
    public List<Request> getDirtyRequests() {
        return this.dirtyRequests;
    }

}
