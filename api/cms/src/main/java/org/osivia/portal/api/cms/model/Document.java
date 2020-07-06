package org.osivia.portal.api.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
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
     * Gets the native item.
     *
     * @return the native item
     */
    public EcmDocument getNativeItem();
    
     

  
    
}
