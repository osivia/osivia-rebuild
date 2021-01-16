package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Personnalization {

    /**
     * Checks if is administrable.
     *
     * @return true, if is administrable
     */
    public boolean isManageable();
    
    
    /**
     * Checks if is modifiable by current user.
     *
     * @return true, if is administrable
     */
    public boolean isModifiable();
    
    /**
     * Gets the sub types.
     *
     * @return the sub types
     */
    public List<String> getSubTypes();

}
