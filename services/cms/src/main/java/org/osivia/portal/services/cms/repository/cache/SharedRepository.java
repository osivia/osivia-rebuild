package org.osivia.portal.services.cms.repository.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.services.cms.model.test.DocumentImpl;
import org.osivia.portal.services.cms.model.test.SpaceImpl;
import org.osivia.portal.services.cms.repository.test.InMemoryRepository;
import org.osivia.portal.services.cms.service.CMSEventImpl;
import org.springframework.util.CollectionUtils;

/**
 * The shared cache
 * 
 */
public class SharedRepository {
    
    /** The repository name. */
    private final String repositoryName;
    
    private final List<RepositoryListener> listeners;
    
    private Map<String, DocumentImpl> cachedDocument;
    
    private InMemoryRepository internalRepository;
    
    public SharedRepository(String repositoryName) {
        super();
        this.repositoryName = repositoryName;
        this.listeners = new ArrayList<>();
        this.cachedDocument= new Hashtable<String, DocumentImpl>();
        internalRepository = new InMemoryRepository(repositoryName);
   }


    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    
    
    public void endBatch()  {
        internalRepository.endBatch();
    }
    
    public void addDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        internalRepository.addDocument(internalID, document, batchMode);
        
        cachedDocument.put(internalID, document);
       
        if(!batchMode)  {
            
            if(CollectionUtils.isEmpty(document.getSubTypes()))    {
                List<Request> dirtyRequests = new ArrayList<>();
                dirtyRequests.add(new GetChildrenRequest(new UniversalID(repositoryName,document.getParentInternalId())));
                notifyChanges( new CMSEventImpl( document, dirtyRequests));    
            }   else
                notifyChanges( new CMSEventImpl());
            
        }
    }
    
    public void updateDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        internalRepository.addDocument(internalID, document, batchMode);
        
        cachedDocument.put(internalID, document);
        
        if(!batchMode)  {
            notifyChanges( new CMSEventImpl());    
        }
    } 
    
    
    public DocumentImpl getDocument(String internalID) throws CMSException {
        try {
        DocumentImpl doc = cachedDocument.get(internalID);
        if( doc == null) {
            doc = internalRepository.getDocument(internalID);
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

   
    public String getRepositoryName() {
        return repositoryName;
    }
    
    
 
    public void notifyChanges( CMSEvent e) {
        for (RepositoryListener listener : listeners) {
            listener.contentModified(e);
        }
    }
    
    
}
