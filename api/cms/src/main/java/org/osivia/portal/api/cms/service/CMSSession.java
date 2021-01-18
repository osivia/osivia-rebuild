package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;

/**
 * A CMS Session is a cross-repository session with an abstraction of the execution context
 * (language, publication state, ..)
 * Each document as one instance
 *
 * @author Jean-Sébastien Steux
 */
public interface CMSSession {


    
    /**
     * Gets the document.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Document getDocument(UniversalID id) throws CMSException;
 
    /**
     * Gets the personnalization.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the personnalization
     * @throws CMSException the CMS exception
     */
    Personnalization getPersonnalization(UniversalID id) throws CMSException;

    
    /**
     * Execute the request.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Result executeRequest(Request request) throws CMSException;


    /**
     * Gets the navigation item for the current document.
     *
     * @param cmsContext the cms context
     * @param documentId the document id
     * @param navigationTreeName the navigation tree name
     * @return the navigation item
     * @throws CMSException the CMS exception
     */
    NavigationItem getNavigationItem( UniversalID id) throws CMSException;
    
     
 
   
}
