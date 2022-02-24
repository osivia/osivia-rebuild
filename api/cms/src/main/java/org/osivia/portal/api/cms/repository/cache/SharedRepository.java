package org.osivia.portal.api.cms.repository.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.CMSNotImplementedRequestException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.UserStorage;
import org.osivia.portal.api.cms.repository.model.RepositoryEvent;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.cms.service.UpdateInformations;
import org.osivia.portal.api.cms.service.UpdateScope;
import org.springframework.util.CollectionUtils;

/**
 * The shared cache
 * 
 */
public class SharedRepository {
    
    /** The repository name. */
    private final String repositoryName;
    
    private final List<RepositoryListener> listeners;
    
    private Map<String, RepositoryDocument> cachedDocument;
    
    private Map<String, SpaceCacheBean>  spaceTsMap ; 
    
    private final UserStorage defaultStorage;


    public SharedRepository(String repositoryName, UserStorage storageRepository) {
        super();
        this.repositoryName = repositoryName;
        this.listeners = new ArrayList<>();
        this.cachedDocument= new Hashtable<String, RepositoryDocument>();
        this.spaceTsMap  = new Hashtable<String, SpaceCacheBean>();
        defaultStorage = storageRepository;
   }


    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }


    
    public void endBatch(UserStorage storageRepository)  {
        storageRepository.endBatch();
    }
    
    public void beginBatch(UserStorage storageRepository)  {
        storageRepository.beginBatch();
    }
    
    
    private CMSEvent createCMSEvent(String spaceInternalId, List<Request> dirtyRequests)  throws CMSException  {
         
        Document space = getDocument(defaultStorage, spaceInternalId);
        
        return new RepositoryEvent( space, dirtyRequests);
    }
    
    public void addDocumentToCache(String internalID, RepositoryDocument document, boolean batchMode) throws CMSException  {
        
        cachedDocument.put(internalID, document);
       
        if(!batchMode)  {
            
            List<Request> dirtyRequests = new ArrayList<>();
            if(CollectionUtils.isEmpty(document.getSupportedSubTypes()))    {

                dirtyRequests.add(new GetChildrenRequest(new UniversalID(repositoryName,document.getParentInternalId())));
            }
            
            notifyChanges( createCMSEvent( document.getSpaceId().getInternalID(), dirtyRequests));    
         }
    }
    
    public void updateDocumentToCache(String internalID, RepositoryDocument document, boolean batchMode) throws CMSException {
        
        cachedDocument.put(internalID, document);
        
        if(!batchMode)  {
            notifyChanges( createCMSEvent( document.getSpaceId().getInternalID(), new ArrayList<>()));    
        }
    } 
    
    
    public RepositoryDocument getDocument(UserStorage storageRepository,String internalID) throws CMSException {
        try {
            RepositoryDocument doc = cachedDocument.get(internalID);

            boolean reload = false;

            if (doc != null) {
                if (doc.getSpaceId() != null) {
                    SpaceCacheBean spaceTs = getSpaceCacheInformations(doc.getSpaceId().getInternalID());
                    if (spaceTs.getLastSpaceModification() != null) {
                        if (doc.getTimestamp() < spaceTs.getLastSpaceModification())
                            // TODO : multiple calls for one item during asynchronous delay
                            reload = true;
                    }
                }
            } else
                reload = true;

            if (reload) {
                reload(storageRepository, internalID);
                doc = cachedDocument.get(internalID);
            }

            if (doc == null)
                throw new CMSException();

            return doc.duplicate();
        } catch (Exception e) {
            if( e instanceof CMSException)
                throw (CMSException) e;
            else
                throw new CMSException(e);
        }
    }
    
    public void reload(UserStorage storageRepository,String internalID) throws CMSException  {
        RepositoryDocument doc = storageRepository.reloadDocument(internalID);
        if( doc != null)
            cachedDocument.put(internalID, doc);
    }
    
    
    public void notifyUpdate(UserStorage storageRepository, UpdateInformations updateInformation) throws CMSException {
        
         Document space = null;
         
         try    {
             if( updateInformation.getSpaceID() != null)
                 space = getDocument(storageRepository,updateInformation.getSpaceID().getInternalID());
         } catch(CMSException e)   {
             // Document may have been deleted

         }
         
         RepositoryDocument document = null;
         
         try    {
             if( updateInformation.getDocumentID() != null)
                 document = getDocument(storageRepository,updateInformation.getDocumentID().getInternalID());
         } catch(CMSException e)   {
             // Document may have been deleted
         }         
        
         if( updateInformation.getDocumentID() != null)
             cachedDocument.remove(updateInformation.getDocumentID().getInternalID());
         
         if( updateInformation.getScope().equals(UpdateScope.SCOPE_SPACE) && space != null)  {
             Long updateTs =  System.currentTimeMillis();
             if( updateInformation.isAsync())
                 updateTs += 10000L;
             
             // Update space scope timestamp
             SpaceCacheBean cacheBean = getSpaceCacheInformations(space.getId().getInternalID());
             cacheBean.setLastSpaceModification(updateTs);
             cacheBean.setLastContentModification(updateTs);
             
             notifyChanges(  createCMSEvent(space.getId().getInternalID(), new ArrayList<>()));    
         }
         
         if( updateInformation.getScope().equals(UpdateScope.SCOPE_CONTENT) && space!= null && document != null)  {
             
             List<Request> dirtyRequests = new ArrayList<>();
             dirtyRequests.add(new GetChildrenRequest(new UniversalID(repositoryName,document.getParentInternalId())));
 
             // Update content scope timestamp
             SpaceCacheBean cacheBean = getSpaceCacheInformations(space.getId().getInternalID());
             cacheBean.setLastContentModification(System.currentTimeMillis());
             
             notifyChanges(  createCMSEvent(space.getId().getInternalID(), dirtyRequests));    
         }
    }

   
    public SpaceCacheBean getSpaceCacheInformations( String spaceId) {
        SpaceCacheBean bean = spaceTsMap.get(spaceId);
        if( bean == null) {
            bean = new SpaceCacheBean();
            spaceTsMap.put(spaceId, bean);
        }
        return bean;
    }
    
    public String getRepositoryName() {
        return repositoryName;
    }
    
    
 
    public void notifyChanges( CMSEvent e) {
        
        List<RepositoryListener> obsoleteListeners = new ArrayList<>();
        
        for (RepositoryListener listener : listeners) {
            try {
                listener.contentModified(e);
            } catch( NoClassDefFoundError err) {
                obsoleteListeners.add(listener);
            }
        }
        
        // Remove obsolete listeners 
        // (hot deploy of custom-services)
        if( obsoleteListeners.size() > 0)   {
            List<RepositoryListener> newListeners = new ArrayList<>();
            for( RepositoryListener listener : listeners)   {
                boolean obsolete = false;
                for (RepositoryListener obsoleteListener : obsoleteListeners) {
                    if( obsoleteListener == listener)   
                        obsolete = true;
                }
                if( obsolete == false)
                    newListeners.add(listener);
            }
            
            listeners.clear();
            listeners.addAll(newListeners);
        }
        
        
    }
    
    public void clear() {
        cachedDocument.clear();
    }
    
    
}
