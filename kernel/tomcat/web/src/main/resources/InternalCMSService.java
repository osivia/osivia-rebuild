package fr.toutatice.portail.cms.producers.api;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;

/**
 * CMS SPI service interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface InternalCMSService {

    
    /**
     * Gets the user repository.
     *
     * @param cmsContext the cms context
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    Object getUserRepository(CMSContext cmsContext, String repositoryName) throws CMSException;
    
    
    /**
     * Adds the listener.
     *
     * @param listener the listener
     */

    void addListener(CMSContext cmsContext, String repositoryName, RepositoryListener listener);
     
    
   
}
