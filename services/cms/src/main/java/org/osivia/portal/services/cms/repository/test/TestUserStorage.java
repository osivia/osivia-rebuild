package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.UpdateScope;
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
    private static boolean error = false;
    private static Map<String,RepositoryDocument> savedDocuments;
    
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
    
    
    protected void initDocuments()   {
        getDocuments().clear();
       
    }
    
  
    
    /* (non-Javadoc)
     * @see org.osivia.portal.services.cms.repository.test.StorageRepository#addDocument(java.lang.String, org.osivia.portal.services.cms.model.test.DocumentImpl, boolean)
     */
    @Override
    public void addDocument(String internalID, RepositoryDocument document, boolean batchMode) throws CMSException {
        
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

        // remove paths
         ArrayList<String> keys = new ArrayList<String>(getDocuments().keySet());
         for(String key:keys) {
             if( key.startsWith("/"))
                 getDocuments().remove(key);
         }
        
        // set paths
        List<RepositoryDocument> documents = new ArrayList<RepositoryDocument>(getDocuments().values());
        for (RepositoryDocument doc : documents) {
            try {
                String path = "";
                RepositoryDocument hDoc = doc;
                
                List<String> computedACL = new ArrayList<>();
                
                if (doc instanceof MemoryRepositoryDocument) {
                    MemoryRepositoryDocument mDoc = (MemoryRepositoryDocument) doc;
                    computedACL.addAll(mDoc.getACL());
                }

                path = "/" + hDoc.getName() + path;

                while (!(hDoc instanceof MemoryRepositorySpace)) {
                    hDoc = reloadDocument(hDoc.getParentInternalId());
                    path = "/" + hDoc.getName() + path;
                    
                    if (computedACL.isEmpty() && hDoc instanceof MemoryRepositoryDocument) {
                        MemoryRepositoryDocument pmDoc = (MemoryRepositoryDocument) hDoc;
                        computedACL.addAll(pmDoc.getACL());
                    }
                   
                }

                path = "/" + getSharedRepository().getRepositoryName() + path;

                // add path to document
                doc.setPath(path);
                
                if (doc instanceof MemoryRepositoryDocument) {
                    MemoryRepositoryDocument mDoc = (MemoryRepositoryDocument) doc;
                    mDoc.setComputedAcls(computedACL);
                }               
                

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
        if( error)
            allDocuments.put(getUserRepository().getRepositoryKey(), savedDocuments);            
        
        updatePaths();
        batchMode = false;
        error = false;
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
                List<String> acls = mDoc.getComputedAcls();
                
 
                if (userRepository.getUserName() != null && acls.contains("group:members"))
                    aclControl = true;
                
                if (userRepository.isAdministrator())
                    aclControl = true;
                
                if (acls.contains("_anonymous_"))
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
        error = false;
        savedDocuments = new Hashtable<String,RepositoryDocument>();
        savedDocuments.putAll(getDocuments());
     }



    @Override
    public void deleteDocument(String internalID, boolean batchMode) throws CMSException {
      
        
        deleteDocumentInternal(internalID);
        
        if(!batchMode)  {
            updatePaths();
        }
        

          
    }

    public void deleteDocumentInternal(String internalID) throws CMSException {
        
        RepositoryDocument document = getDocuments().get(internalID);
        if (document == null)
            throw new CMSException();
        
        List<String> children = new ArrayList<>();
        children.addAll(document.getChildrenId());
        
        for(String childId: children)   {
            deleteDocumentInternal(childId);
        }
        
        getDocuments().remove(internalID);

        
        
        // update parent
        String parentID = document.getParentInternalId();
        if( parentID != null)   {
            RepositoryDocument parent = getDocuments().get(parentID);
            if( parent != null) {
                if( parent.getChildrenId().contains(internalID))  {
                    parent.getChildrenId().remove(internalID);
                    
                }
                getSharedRepository().updateDocumentToCache(parentID, parent, true);
            }
        }
        
       
        getSharedRepository().removeDocumentFromCache(internalID, true);
        
        
        
        
    }


    @Override
    public void handleError() {
        error = true;
        
    }


}
