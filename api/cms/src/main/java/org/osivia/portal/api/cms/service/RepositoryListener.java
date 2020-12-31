package org.osivia.portal.api.cms.service;


/**
 * Listeners for modification to a repository
 */

public interface RepositoryListener {
    
    /**
     * Content modified.
     *
     * @param event the event
     */
    public void contentModified( CMSEvent event)  ;
}
