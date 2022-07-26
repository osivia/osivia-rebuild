package org.osivia.portal.api.cms.repository;

import java.util.List;
import java.util.Locale;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;


/**
 * The Interface UserRepository.
 */
public interface UserRepository {
    
    /**
     * Adds the listener.
     *
     * @param listener the listener
     */
    public void addListener(RepositoryListener listener) ;
    
    /**
     * Execute request.
     *
     * @param request the request
     */
    public Result executeRequest(Request request) throws CMSException;    
    
 
    /**
     * Gets the document.
     *
     * @param id the id
     * @return the document
     * @throws CMSException the CMS exception
     */
    public Document getDocument(String id) throws CMSException;
    
 
    /**
     * Notify update of e document.
     *
     * @param id the id
     * @param infos the infos
     * @throws CMSException the CMS exception
     */
    public void notifyUpdate( UpdateInformations infos) throws CMSException ;
    
    /**
     * Gets the navigation item.
     *
     * @param internalId the internal id
     * @return the navigation item
     * @throws CMSException the CMS exception
     */
    public NavigationItem getNavigationItem(String internalId) throws CMSException;
    
    /**
     * Support preview.
     *
     * @return true, if successful
     */
    public boolean supportPreview();
    
    /**
     * Gets the locales.
     *
     * @return the locales
     */
    public List<Locale> getLocales();        
    
    /**
     * Publish.
     *
     * @param id the id
     * @throws CMSException the CMS exception
     */
    void publish(String id) throws CMSException;
    
    /**
     * Gets the personnalization.
     *
     * @param internalId the internal id
     * @return the personnalization
     * @throws CMSException the CMS exception
     */
    public Personnalization getPersonnalization(String internalId) throws CMSException ;
    
    /**
     * Gets the timestamp of space relative requests.
     *
     * @param id the id
     * @throws CMSException the CMS exception
     */
    SpaceCacheBean getSpaceCacheInformations(String id) throws CMSException;
 
    
    /**
     * Sets the portal context.
     *
     * @param portalContext the new portal context
     */
    void setPortalContext(PortalControllerContext portalContext) ;
    
    
    /**
     * Reloads the document
     *
     * @param internalId the internal id
     * @throws CMSException the CMS exception
     */
    public void reload(String internalId) throws CMSException;

    /**
     * @param infos
     * @throws CMSException
     */
    void handleUpdate(UpdateInformations infos) throws CMSException; 
}
