package org.osivia.portal.api.cms.service;

import java.util.List;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
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
    
    /**
     * Gets the default portal.
     *
     * @param cmsContext the cms context
     * @return the CMS session
     * @throws CMSException the CMS exception
     */
    UniversalID getDefaultPortal(CMSContext cmsContext) throws CMSException;



    /**
     * Get the user repositories
     * 
     * @param cmsContext
     * @param repositoryName
     * @return
     * @throws CMSException
     */
    List<NativeRepository> getUserRepositories(CMSContext cmsContext) throws CMSException;



    /**
     * Remove the listener
     * @param cmsContext
     * @param repositoryName
     * @param listener
     */
    void removeListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener); 
   
}
