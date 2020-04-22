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
    UniversalID getId();
    
    
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
    public UniversalID getSpaceId();
    
     
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Document getNavigationParent()  throws CMSException;
    
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public List<Document> getNavigationChildren()  throws CMSException;
  
    
}
