package org.osivia.portal.api.cms.model;

import java.util.Map;

/**
 * CMS document interface.
 *
 * @author Jean-Sbastien Steux
 */
public interface Document {

    /**
     * Get document identifier.
     *
     * @return identifier
     */
    String getId();

    
    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();
    
    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Map<String,Object> getProperties();
    
    /**
     * Gets the space.
     *
     * @return the space
     */
    public String getSpaceId();
}
