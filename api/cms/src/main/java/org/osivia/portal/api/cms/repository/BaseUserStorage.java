package org.osivia.portal.api.cms.repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.UserData;
import org.osivia.portal.api.cms.repository.UserStorage;
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.user.UserDatasImpl;

/**
 * In memory sample storage
 * 
 */
public abstract class BaseUserStorage implements UserStorage  {
    

    private static Map<SharedRepositoryKey, Map<String,RepositoryDocument>> allDocuments = new Hashtable<SharedRepositoryKey, Map<String,RepositoryDocument>>();
    private BaseUserRepository userRepository;
    
    public void setUserRepository(BaseUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    protected SharedRepository getSharedRepository()    {
        return userRepository.getSharedRepository();
    }
    
    protected BaseUserRepository getUserRepository() {
        return userRepository;
    }
    
    protected Map<String,RepositoryDocument> getDocuments()   {
        
        Map<String,RepositoryDocument> documents = allDocuments.get(userRepository.getRepositoryKey());
        if( documents == null) {
            documents= new Hashtable<String,RepositoryDocument>();
            allDocuments.put(userRepository.getRepositoryKey(), documents);
        }
        
        return documents;
        
    }
    
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#addDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void addDocument(String internalID, RepositoryDocument document, boolean batchMode)  {
        
        getDocuments().put(internalID, document);
        
        // update parent
        String parentID = document.getParentInternalId();
        if( parentID != null)   {
            RepositoryDocument parent = getDocuments().get(parentID);
            if( parent != null) {
                if( !parent.getChildrenId().contains(internalID))  {
                    parent.getChildrenId().add(internalID);
                    
                }
                getSharedRepository().addDocumentToCache(parentID, parent, true);
            }
        }
        
        if(!batchMode)  {
             updatePaths();
        }
        
        getSharedRepository().addDocumentToCache(internalID, document, batchMode);
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#updateDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void updateDocument(String internalID, RepositoryDocument document, boolean batchMode)  {
        
        getDocuments().put(internalID, document);
        
        if(!batchMode)  {
            updatePaths();
        }
        
        getSharedRepository().updateDocumentToCache(internalID, document, batchMode);
    } 
    
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#getDocument(java.lang.String)
     */
    @Override
    public RepositoryDocument reloadDocument(String internalID) throws CMSException {
        try {
        RepositoryDocument doc = getDocuments().get(internalID);
        if( doc == null)
            throw new CMSException();
        return doc.duplicate();

        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }


    @Override
    public abstract UserData getUserData(String internalID) throws CMSException ;


    protected void updatePaths() {
        // Set paths
        for (RepositoryDocument doc : new ArrayList<RepositoryDocument>(getDocuments().values())) {
            try {
                String path = "";
                RepositoryDocument hDoc = doc;

                path = "/" + hDoc.getName() + path;

                while (!(hDoc instanceof MemoryRepositorySpace)) {
                    hDoc = reloadDocument(hDoc.getParentInternalId());
                    path = "/" + hDoc.getName() + path;
                }

                path = "/" + getSharedRepository().getRepositoryName() + path;

                // add path to document
                doc.setPath(path);

                // add path entry
                getDocuments().put(path, doc);

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
