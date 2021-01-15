package org.osivia.portal.services.cms.repository.spi;

import java.util.List;

public interface UserData {
    
    /**
     * Gets the sub types.
     *
     * @return the sub types
     */
    List<String> getSubTypes();
    
    /**
     * Checks if is administrable.
     *
     * @return true, if is administrable
     */
    boolean isManageable();
    
    
    /**
     * Checks if is modifiable.
     *
     * @return true, if is modifiable
     */
    public boolean isModifiable();
    


}
