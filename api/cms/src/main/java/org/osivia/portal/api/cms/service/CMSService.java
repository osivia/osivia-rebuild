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
     * @param repositoryName the repository name
     * @return the user repository
     * @throws CMSException the CMS exception
     */
    NativeRepository getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException;
    
    

    /**
     * Adds the listener.
     *
     * @param cmsContext the cms context
     * @param repositoryName the repository name
     * @param listener the listener
     */
    void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener);
     
    

    /**
     * Gets the CMS session.
     *
     * @param cmsContext the cms context
     * @return the CMS session
     * @throws CMSException the CMS exception
     */
    CMSSession getCMSSession(CMSContext cmsContext) throws CMSException;
   

   
}
