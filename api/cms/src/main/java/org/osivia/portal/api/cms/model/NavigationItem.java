package org.osivia.portal.api.cms.model;

import java.util.List;

import org.osivia.portal.api.cms.exception.CMSException;

/**
 * The Class NavigationRoot.
 */

public interface NavigationItem {
    
    /**
     * Checks if is root.
     *
     * @return true, if is root
     */
    boolean isRoot();
    
    
    /**
     * Get document identifier.
     *
     * @return identifier
     */
    UniversalID getDocumentId();
    
    
    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();    

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public NavigationItem getParent()  throws CMSException;
    
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public List<NavigationItem> getChildren()  throws CMSException;
}
