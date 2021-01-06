package org.osivia.portal.api.cms;

import java.io.Serializable;

/**
 * @author Jean-Sébastien
 */
public class UniversalID implements Serializable {
    

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((internalID == null) ? 0 : internalID.hashCode());
        result = prime * result + ((repositoryName == null) ? 0 : repositoryName.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UniversalID other = (UniversalID) obj;
        if (internalID == null) {
            if (other.internalID != null)
                return false;
        } else if (!internalID.equals(other.internalID))
            return false;
        if (repositoryName == null) {
            if (other.repositoryName != null)
                return false;
        } else if (!repositoryName.equals(other.repositoryName))
            return false;
        return true;
    }
    

}
