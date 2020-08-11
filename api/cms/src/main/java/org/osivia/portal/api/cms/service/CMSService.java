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
     * Gets the navigation item for the current document.
     *
     * @param cmsContext the cms context
     * @param documentId the document id
     * @param navigationTreeName the navigation tree name
     * @return the navigation item
     * @throws CMSException the CMS exception
     */
    NavigationItem getNavigationItem(CMSContext cmsContext, UniversalID id) throws CMSException;
    
    

   
}
