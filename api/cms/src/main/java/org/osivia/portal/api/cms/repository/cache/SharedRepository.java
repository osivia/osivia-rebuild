package org.osivia.portal.api.cms.repository.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.UserStorage;
import org.osivia.portal.api.cms.repository.model.RepositoryEvent;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;

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
    
    
    



    public SharedRepository(String repositoryName, UserStorage storageRepository) {
        super();
        this.repositoryName = repositoryName;
        this.listeners = new ArrayList<>();
        this.cachedDocument= new Hashtable<String, RepositoryDocument>();
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
        if( doc == null) {
            doc = storageRepository.reloadDocument(internalID);
            if( doc != null)
                cachedDocument.put(internalID, doc);
        }
        if( doc == null)
            throw new CMSException();
        return doc.duplicate();
        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }
    
    
    public void updateDocument(UserStorage storageRepository,String internalID) throws CMSException {

         cachedDocument.remove(internalID);
       
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
