package org.osivia.portal.api.cms.model;

import java.util.List;
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
     * Get document name.
     *
     * @return identifier
     */
    String getName();

    
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
    
       
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public String getParentId();
    
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public List<String> getChildrenId();
    
}
