package org.osivia.portal.api.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;

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
    
       
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Document getParent()  throws CMSException;
    
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public List<Document> getChildren()  throws CMSException;
    
}
