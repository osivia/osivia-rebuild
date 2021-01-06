package org.osivia.portal.services.cms.repository.test;

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
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.service.CMSEventImpl;
import org.springframework.util.CollectionUtils;

/**
 * In memory sample storage
 * 
 */
public class InMemoryRepository implements StorageRepository  {
    

    private Map<String, DocumentImpl> documents;
    private SharedRepository sharedRepository;
    
 
    public InMemoryRepository() {
        this.documents= new Hashtable<String, DocumentImpl>();
   }
  
    
    public void setSharedRepository(SharedRepository sharedRepository) {
        this.sharedRepository = sharedRepository;
    }

    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#addDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void addDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        documents.put(internalID, document);
        
        // update parent
        String parentID = document.getParentInternalId();
        if( parentID != null)   {
            DocumentImpl parent = documents.get(parentID);
            if( parent != null) {
                if( !parent.getChildrenId().contains(internalID))  {
                    parent.getChildrenId().add(internalID);
                }
            }
        }
        
        if(!batchMode)  {
             updatePaths();
        }
        
        sharedRepository.addDocumentToCache(internalID, document, batchMode);
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#updateDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void updateDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        documents.put(internalID, document);
        
        if(!batchMode)  {
            updatePaths();
        }
        
        sharedRepository.updateDocumentToCache(internalID, document, batchMode);
    } 
    
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#getDocument(java.lang.String)
     */
    @Override
    public DocumentImpl getDocument(String internalID) throws CMSException {
        try {
        DocumentImpl doc = documents.get(internalID);
        if( doc == null)
            throw new CMSException();
        return doc.duplicate();
        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }


    private void updatePaths() {
        // Set paths
        for (DocumentImpl doc : new ArrayList<DocumentImpl>(documents.values())) {
            try {
                String path = "";
                DocumentImpl hDoc = doc;

                path = "/" + hDoc.getName() + path;

                while (!(hDoc instanceof SpaceImpl)) {
                    hDoc = getDocument(hDoc.getParentInternalId());
                    path = "/" + hDoc.getName() + path;
                }

                path = "/" + sharedRepository.getRepositoryName() + path;

                // add path to document
                doc.setPath(path);

                // add path entry
                documents.put(path, doc);

            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#endBatch()
     */
    @Override
    public void endBatch()  {
        updatePaths();
    }
    

    
}
