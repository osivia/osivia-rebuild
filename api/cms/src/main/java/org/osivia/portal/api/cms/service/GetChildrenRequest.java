package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.UniversalID;

/**
 * The Class ChildrenRequest.
 */
public class GetChildrenRequest implements Request{


    private final UniversalID parentId;

    
    /**
     * Instantiates a new parent request.
     *
     * @param parentId the parent id
     */
    public GetChildrenRequest(UniversalID parentId) {
        super();
        this.parentId = parentId;
    }


    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    public UniversalID getParentId() {
        return parentId;
    }
    
    
    
    @Override
    public String getRepositoryName() {
        
        return parentId.getRepositoryName();
    }


}
