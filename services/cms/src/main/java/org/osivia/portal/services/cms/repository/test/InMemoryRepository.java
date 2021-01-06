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
 * The shared cache
 * 
 */
public class InMemoryRepository  {
    
    /** The repository name. */
    private final String repositoryName;
    

    private Map<String, DocumentImpl> documents;
    
    public InMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
        this.documents= new Hashtable<String, DocumentImpl>();
   }
    
    
    
    public String getRepositoryName() {
        return repositoryName;
    }


    
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
    }
    
    public void updateDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        documents.put(internalID, document);
        
        if(!batchMode)  {
            updatePaths();
        }
    } 
    
    
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

                path = "/" + getRepositoryName() + path;

                // add path to document
                doc.setPath(path);

                // add path entry
                documents.put(path, doc);

            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }
    
    public void endBatch()  {
        updatePaths();
    }
    

    
}
