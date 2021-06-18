package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.UserData;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.user.UserDatasImpl;

public class TestUserStorage extends BaseUserStorage {
    
     

    private static boolean batchMode = false;
    
    private static Map<SharedRepositoryKey, Map<String,RepositoryDocument>> allDocuments = new Hashtable<SharedRepositoryKey, Map<String,RepositoryDocument>>();

    
    protected Map<String,RepositoryDocument> getDocuments()   {
        Map<String,RepositoryDocument> documents = allDocuments.get(getUserRepository().getRepositoryKey());
        if( documents == null) {
            documents= new Hashtable<String,RepositoryDocument>();
            allDocuments.put(getUserRepository().getRepositoryKey(), documents);
            
            if( batchMode == false) {
                // We are not in batch mode 
                // the repository meust bootstrap
                // (redeployment use case)
                getUserRepository().startInitBatch();
            }

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
    public void updateDocument(String internalID, RepositoryDocument document, boolean batchMode)  throws CMSException {
        
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
        batchMode = false;
    }

    
    public void clearDocuments()  {
        Map<String,RepositoryDocument> documents = new Hashtable<String,RepositoryDocument>();
        allDocuments.put(getUserRepository().getRepositoryKey(), documents);
    }

    @Override
    public UserData getUserData(String internalID) throws CMSException {
        try {
            RepositoryDocument doc = getDocuments().get(internalID);
            if (doc == null)
                throw new CMSException();


            BaseUserRepository userRepository = getUserRepository();

            // ACL
            
            boolean aclControl = false;

            if (doc instanceof MemoryRepositoryDocument) {

                MemoryRepositoryDocument mDoc = (MemoryRepositoryDocument) doc;
                List<String> acls = mDoc.getACL();
                if (acls.size() > 0) {

                    if (userRepository.getUserName() != null && acls.contains("group:members"))
                        aclControl = true;
                } else
                    aclControl = true;
            }
            
            if (aclControl == false)
                throw new DocumentForbiddenException();


            List<String> subtypes = new ArrayList<String>(doc.getSupportedSubTypes());

            if (!getUserRepository().isAdministrator())
                subtypes.clear();

            return new UserDatasImpl(subtypes, userRepository.isAdministrator(), userRepository.isAdministrator());
        } catch (Exception e) {
            throw new CMSException(e);
        }

    }



    @Override
    public void beginBatch() {
        batchMode = true;
        
    }

  


}
