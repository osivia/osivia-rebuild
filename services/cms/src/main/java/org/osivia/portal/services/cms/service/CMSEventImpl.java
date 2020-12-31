package org.osivia.portal.services.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.Request;


public class CMSEventImpl implements CMSEvent {

    /** The src. */
    private final Document src;
    
    private final List<Request> dirtyRequests;
    
    /**
     * Instantiates a new CMS event impl.
     *
     * @param src the src
     */
    public CMSEventImpl(Document src, List<Request> dirtyRequests) {
        super();
        this.src = src;
        this.dirtyRequests = dirtyRequests;
    }
    
    /**
     * Instantiates a new CMS event impl.
     *
     * @param src the src
     */
    public CMSEventImpl() {
        super();
        this.src = null;
        this.dirtyRequests = new ArrayList<>();
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
