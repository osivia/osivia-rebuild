package org.osivia.portal.api.cms.service;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;

/**
 * CMS service interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface CMSService {


   
    
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
     
    
    /**
     * Adds the listener.
     *
     * @param listener the listener
     */

    CMSSession getCMSSession(CMSContext cmsContext) throws CMSException;
   

   
}
