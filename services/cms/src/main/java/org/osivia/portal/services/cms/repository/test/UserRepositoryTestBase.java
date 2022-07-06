package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.CMSNotImplementedRequestException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.ModulesContainer;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.service.Documents;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.cms.service.UpdateInformations;

import fr.toutatice.portail.cms.producers.test.TestRepository;

/**
 * The Class NativeMemoryRepository contains additionnal test functions
 * (publishing, add content, ...)
 */

public abstract class UserRepositoryTestBase extends BaseUserRepository implements TestRepository

{
 

    public UserRepositoryTestBase(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName, new TestUserStorage());
    }
    

    @Override
    public void publish( String id) throws CMSException {
        try {
        
        RepositoryDocument doc = getSharedDocument(id);
        RepositoryDocument existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getSharedDocument(id);
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        RepositoryDocument publishedParent = null;
        String parentId = doc.getParentInternalId();
        
        while(  publishedParent == null && parentId != null)   {
            try {
                publishedParent = publishRepository.getSharedDocument(parentId);
            } catch(CMSException e)    {
                // not found
                RepositoryDocument parent = getSharedDocument(parentId);
                parentId = parent.getParentInternalId();                
            }
        }
        
        
        List<String> childToRemoveFromParent = new ArrayList<>();
        
        // Get parent
        String publishedParentId;
        if( publishedParent != null) {
            publishedParentId = publishedParent.getInternalID();
        }   else
            publishedParentId = null;
        
        
        
        // Update hierarchy
        List<String> childrenId;
        if( existingPublishedDoc != null)   {
            // replacement
            childrenId = existingPublishedDoc.getChildrenId();
        }
        else    {
            // new object : look for published children
            childrenId = new ArrayList<>();
            for(String childId: doc.getChildrenId())    {
                RepositoryDocument publishedChild = null;
                try {
                    publishedChild = publishRepository.getSharedDocument(childId);
                } catch(CMSException e)    {
                    // not found
                }    
                if( publishedChild != null) {
                    // Add to current node
                    childrenId.add(childId);
                    // Remove child from parent
                    childToRemoveFromParent.add(childId);
                    // reset child parent
                    publishedChild.setParentInternalId( id);
                    
                    publishRepository.getUserStorage().updateDocument(publishedChild.getInternalID(), publishedChild, true);
                }
            }            
        }
        
        
        // Set parent children
        if( publishedParent != null) {
            if( ! publishedParent.getChildrenId().contains(id))
                publishedParent.getChildrenId().add( id);
            
            for (String childId:childToRemoveFromParent)    {
                publishedParent.getChildrenId().remove(childId);
            }
            
            publishRepository.getUserStorage().updateDocument(publishedParent.getInternalID(), publishedParent, true);
        }     
        
        
        // create published object
        RepositoryDocument publishedDoc = (RepositoryDocument) doc.duplicateForPublication(publishedParentId, childrenId, publishRepository);
        publishRepository.getUserStorage().addDocument(publishedDoc.getInternalID(), publishedDoc, false);
        
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
     }
    
    
    @Override
    public void reloadDatas() {
        ((TestUserStorage) getUserStorage()).clearDocuments();
        startInitBatch();
      }



    @Override
    public void addDocument(String internalID, RepositoryDocument document) throws CMSException {
        getUserStorage().addDocument(internalID, document, batchMode);
     }

    @Override
    public void updateDocument(String internalID, RepositoryDocument document) throws CMSException {
        getUserStorage().updateDocument(internalID, document, batchMode);
    }



    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
    }


    @Override  
    public void addWindow(String id, String name, String portletName, String region, int position, String pageId,  Map<String,String> properties) throws CMSException {
        ModulesContainer page = (ModulesContainer) getSharedDocument(pageId);
        ModuleRef module = new ModuleRef("winD-" + System.currentTimeMillis(), region,  portletName, properties);
        
        if( position == POSITION_END)
            page.getModuleRefs().add(module);
        else
            page.getModuleRefs().add(position, module);
        
        updateDocument(pageId, (RepositoryDocument) page);
    }
    
    
    @Override
    public void addFolder(String id, String name, String parentId) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);
        
        RepositoryDocument parent = getSharedDocument(parentId);

        MemoryRepositoryFolder folder = new MemoryRepositoryFolder(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);        
        
        addDocument(id, folder);
  
    }


    @Override
    public void addDocument(String id, String name, String parentId) throws CMSException {
        
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);
        
        RepositoryDocument parent = getSharedDocument(parentId);
        RepositoryDocument doc = new MemoryRepositoryDocument(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);
        
        addDocument(id, doc);
     }
    
    @Override
    public void renameDocument(String id,String title) throws CMSException {
        
        
        RepositoryDocument doc = getSharedDocument(id);
        doc.setTitle(title);
        
        updateDocument(id, doc);
     }


    @Override
    public void setProfiles(String id, List<Profile> profiles) throws CMSException {
        
        MemoryRepositorySpace space = (MemoryRepositorySpace) getSharedDocument(id);
        
        space.setProfiles(profiles);
        
        updateDocument(id, space);
    }
    
    @Override
    public void setStyles(String id, List<String> styles) throws CMSException {
        
        MemoryRepositorySpace space = (MemoryRepositorySpace) getSharedDocument(id);
        
        space.setStyles(styles);
        
        updateDocument(id, space);
    }


    @Override
    protected void initDocuments() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public boolean supportPreview() {
        return false;
     }


    @Override
    public boolean supportPageEdition() {
        return false;
    }
 
     
    
    @Override
    public Result executeRequest(Request request) throws CMSException {
        if( request instanceof GetChildrenRequest)
            return new Documents(getChildren(((GetChildrenRequest) request).getParentId().getInternalID()));
        throw new CMSNotImplementedRequestException();
    }


    public List<Document> getChildren(String id) throws CMSException {
        List<Document> childrens = new ArrayList<>();
        for (String childId:getSharedDocument(id).getChildrenId()) {
            try {
                childrens.add(getDocument(childId));
            } catch(DocumentForbiddenException e)   {
                
            }
        }
        
        return childrens;
    }
    
    

    
    @Override
    public List<String> getACL(String id) throws CMSException {
        MemoryRepositoryDocument memDoc = (MemoryRepositoryDocument) getSharedDocument(id);
        return memDoc.getACL();
        
     }

    @Override
    public void setACL(String id, List<String> acls) throws CMSException {
        MemoryRepositoryDocument memDoc = (MemoryRepositoryDocument) getSharedDocument(id);
        memDoc.setACL(acls);
        updateDocument(id, memDoc);
     }
    
}
