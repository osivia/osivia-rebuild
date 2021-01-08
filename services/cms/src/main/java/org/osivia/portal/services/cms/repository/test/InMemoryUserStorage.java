package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.services.cms.model.share.DocumentImpl;
import org.osivia.portal.services.cms.model.share.SpaceImpl;
import org.osivia.portal.services.cms.model.test.UserDocumentImpl;
import org.osivia.portal.services.cms.model.test.UserDatasImpl;
import org.osivia.portal.services.cms.repository.BaseUserRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserData;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.repository.spi.UserStorage;
import org.osivia.portal.services.cms.service.CMSEventImpl;
import org.springframework.util.CollectionUtils;

/**
 * In memory sample storage
 * 
 */
public class InMemoryUserStorage implements UserStorage  {
    

    private static Map<SharedRepositoryKey, Map<String,DocumentImpl>> allDocuments = new Hashtable<SharedRepositoryKey, Map<String,DocumentImpl>>();
    private BaseUserRepository userRepository;
    
    public void setUserRepository(BaseUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    private SharedRepository getSharedRepository()    {
        return userRepository.getSharedRepository();
        
    }
    
    private Map<String,DocumentImpl> getDocuments()   {
        
        Map<String,DocumentImpl> documents = allDocuments.get(userRepository.getRepositoryKey());
        if( documents == null) {
            documents= new Hashtable<String,DocumentImpl>();
            allDocuments.put(userRepository.getRepositoryKey(), documents);
        }
        
        return documents;
        
    }
    
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#addDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void addDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
        getDocuments().put(internalID, document);
        
        // update parent
        String parentID = document.getParentInternalId();
        if( parentID != null)   {
            DocumentImpl parent = getDocuments().get(parentID);
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
    public void updateDocument(String internalID, DocumentImpl document, boolean batchMode)  {
        
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
    public DocumentImpl getSharedDocument(String internalID) throws CMSException {
        try {
        DocumentImpl doc = getDocuments().get(internalID);
        if( doc == null)
            throw new CMSException();
        return doc.duplicate();

        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }

    
    @Override
    public UserData getUserData(String internalID) throws CMSException {
        try {
            DocumentImpl doc = getDocuments().get(internalID);
            if( doc == null)
                throw new CMSException();
            List<String> subtypes = doc.getSupportedSubTypes();
            if(userRepository.getUserName() != null) {
                subtypes.clear();
            }
            return new UserDatasImpl( subtypes);
            } catch(Exception e)    {
                throw new CMSException(e);
            }

    }


    private void updatePaths() {
        // Set paths
        for (DocumentImpl doc : new ArrayList<DocumentImpl>(getDocuments().values())) {
            try {
                String path = "";
                DocumentImpl hDoc = doc;

                path = "/" + hDoc.getName() + path;

                while (!(hDoc instanceof SpaceImpl)) {
                    hDoc = getSharedDocument(hDoc.getParentInternalId());
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
