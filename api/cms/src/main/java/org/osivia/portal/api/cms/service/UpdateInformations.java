package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.UniversalID;

/**
 * The Class UpdateInformations.
 */
public class UpdateInformations {
    
    /** The document. */
    private final UniversalID documentID;
    


    /** Is the update action asynchronous */
    private boolean async = false;
    

    /**
     * Instantiates a new update informations.
     */
    public UpdateInformations( UniversalID documentID) {

        super();
        this.documentID = documentID;       
    }
    
    /**
     * Instantiates a new update informations.
     *
     * @param documentID the document ID
     * @param async the async
     */
    public UpdateInformations(UniversalID documentID, boolean async) {
        super();
        this.documentID = documentID;
        this.async = async;
    }

    /**
     * Checks if is async.
     *
     * @return true, if is async
     */
    public boolean isAsync() {
        return async;
    }

    
    /**
     * Gets the document ID.
     *
     * @return the document ID
     */
    public UniversalID getDocumentID() {
        return documentID;
    }
    
    /**
     * Sets the async.
     *
     * @param async the new async
     */
    public void setAsync(boolean async) {
        this.async = async;
    }





 

}
