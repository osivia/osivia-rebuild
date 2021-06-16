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

import org.osivia.portal.api.cms.service.UpdateInformations;
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
    
    private Map<String, Long>  spaceTsMap ; 
    



    public SharedRepository(String repositoryName, UserStorage storageRepository) {
        super();
        this.repositoryName = repositoryName;
        this.listeners = new ArrayList<>();
        this.cachedDocument= new Hashtable<String, RepositoryDocument>();
        this.spaceTsMap  = new Hashtable<String, Long>();
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
    
    public void addDocumentToCache(String internalID, RepositoryDocument document, boolean batchMode)  {
        
        cachedDocument.put(internalID, document);
       
        if(!batchMode)  {
            
            if(CollectionUtils.isEmpty(document.getSupportedSubTypes()))    {
                List<Request> dirtyRequests = new ArrayList<>();
                dirtyRequests.add(new GetChildrenRequest(new UniversalID(repositoryName,document.getParentInternalId())));
                notifyChanges( new RepositoryEvent( document, dirtyRequests));    
            }   else
                notifyChanges( new RepositoryEvent());
            
        }
    }
    
    public void updateDocumentToCache(String internalID, RepositoryDocument document, boolean batchMode)  {
        
        cachedDocument.put(internalID, document);
        
        if(!batchMode)  {
            notifyChanges( new RepositoryEvent());    
        }
    } 
    
    
    public RepositoryDocument getDocument(UserStorage storageRepository,String internalID) throws CMSException {
        try {
        RepositoryDocument doc = cachedDocument.get(internalID);
        
        boolean reload = false;
        
        if( doc != null)    {
            Long spaceTs = getSpaceTs(doc.getSpaceId().getInternalID());
            if( spaceTs != null)    {
                if( doc.getTimestamp() < spaceTs)
                    // TODO : multiple calls for one item during asynchronous delay 
                    reload = true;
            }
        }   else
            reload = true;
        
        if( reload) {
             reload( storageRepository, internalID);
             doc = cachedDocument.get(internalID);
        }
        
        if( doc == null)
            throw new CMSException();
        
        return doc.duplicate();
        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }
    
    public void reload(UserStorage storageRepository,String internalID) throws CMSException  {
        RepositoryDocument doc = storageRepository.reloadDocument(internalID);
        if( doc != null)
            cachedDocument.put(internalID, doc);
    }
    
    
    public void notifyUpdate(UserStorage storageRepository, UpdateInformations updateInformation) throws CMSException {
        
         Document document;
         
         try    {
             document = getDocument(storageRepository,updateInformation.getDocumentID().getInternalID());
         } catch(CMSException e)   {
             // Document may have been deleted
             document = null;
         }
        
         cachedDocument.remove(storageRepository,updateInformation.getDocumentID().getInternalID());
         
         if( document != null)  {
             Long updateTs =  System.currentTimeMillis();
             if( updateInformation.isAsync())
                 updateTs += 10000L;
             
             spaceTsMap.put(document.getSpaceId().getInternalID(), updateTs);
             notifyChanges( new RepositoryEvent( ));    
         }
    }

   
    public Long getSpaceTs( String spaceId) {
        return spaceTsMap.get(spaceId);
    }
    
    public String getRepositoryName() {
        return repositoryName;
    }
    
    
 
    public void notifyChanges( CMSEvent e) {
        for (RepositoryListener listener : listeners) {
            listener.contentModified(e);
        }
    }
    
    public void clear() {
        cachedDocument.clear();
    }
    
    
}
