package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;

/**
 * CMS service interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface CMSService {


    
    /**
     * Gets the document.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Document getDocument(CMSContext cmsContext, UniversalID id) throws CMSException;
    
    
    /**
     * Execute the request.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Result executeRequest(CMSContext cmsContext, Request request) throws CMSException;


    /**
     * Gets the navigation item for the current document.
     *
     * @param cmsContext the cms context
     * @param documentId the document id
     * @param navigationTreeName the navigation tree name
     * @return the navigation item
     * @throws CMSException the CMS exception
     */
    NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id) throws CMSException;
    
    
    
    
    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException;
    
    
    /**
     * Adds the listener.
     *
     * @param listener the listener
     */

    void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener);
     
    
    

   
}
