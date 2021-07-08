package org.osivia.portal.api.cms.service;


/**
 * The  SpaceCacheBean.
 * Contains informations about last modifications
 */
public class SpaceCacheBean {
    
    /** The last space modification. */
    private Long lastSpaceModification;
    
     /** The last content modification. */
     private Long lastContentModification;

    


    /**
     * Instantiates a new space cache bean.
     */
    public SpaceCacheBean() {
        super();
        
    }
    
    /**
     * Gets the last space modification.
     *
     * @return the last space modification
     */
    public Long getLastSpaceModification() {
        return lastSpaceModification;
    }

    
    /**
     * Sets the last space modification.
     *
     * @param lastSpaceModification the new last space modification
     */
    public void setLastSpaceModification(Long lastSpaceModification) {
        this.lastSpaceModification = lastSpaceModification;
    }
    
    /**
     * Gets the last content modification.
     *
     * @return the last content modification
     */
    public Long getLastContentModification() {
        return lastContentModification;
    }

    
    /**
     * Sets the last content modification.
     *
     * @param lastContentModification the new last content modification
     */
    public void setLastContentModification(Long lastContentModification) {
        this.lastContentModification = lastContentModification;
    }
}
