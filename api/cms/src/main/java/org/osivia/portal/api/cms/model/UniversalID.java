package org.osivia.portal.api.cms.model;


/**
 * @author Jean-Sébastien
 */
public class UniversalID {
    

    /** The store name. */
    private final String repositoryName;
    
    /** The internal ID. */
    private final String internalID;
    
    /**
     * Gets the store name.
     *
     * @return the store name
     */
    public String getRepositoryName() {
        return repositoryName;
    }
    

    
    /**
     * Gets the internal ID.
     *
     * @return the internal ID
     */
    public String getInternalID() {
        return internalID;
    }


    /**
     * Instantiates a new universal ID.
     *
     * @param storeName the store name
     * @param internalID the internal ID
     */
    public UniversalID(String repositoryName, String internalID) {
        super();
        this.repositoryName = repositoryName;
        this.internalID = internalID;
    }
    
    /**
     * Instantiates a new universal ID.
     *
     * @param id the id (REPO_NAME/INTERNAL_ID)
     */
    public UniversalID(String id)   {
        int separatorIndex = id.indexOf(":");
        if( separatorIndex == -1)
            throw new IllegalArgumentException("incorrect ID " + id);
        this.repositoryName = id.substring(0, separatorIndex);
        this.internalID = id.substring(separatorIndex +1);
    }
    
    public String toString()  {
        return repositoryName + ":" + internalID;
    }
    

}
