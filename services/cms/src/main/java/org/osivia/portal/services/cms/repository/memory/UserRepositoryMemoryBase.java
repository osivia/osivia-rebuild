package org.osivia.portal.services.cms.repository.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.UpdateScope;
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
import org.osivia.portal.api.cms.repository.RepositoryFactory;
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
import org.osivia.portal.api.cms.service.StreamableRepository;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;

/**
 * The Class NativeMemoryRepository contains additionnal test functions
 * (publishing, add content, ...)
 */

public abstract class UserRepositoryMemoryBase extends BaseUserRepository implements AdvancedRepository

{
 



    public UserRepositoryMemoryBase(RepositoryFactory repositoryFactory, SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryFactory, repositoryKey, publishRepository, userName, new MemoryUserStorage());
    }
    
    
    public Map<String, RepositoryDocument> getDocuments() {
        return ((MemoryUserStorage) getUserStorage()).getDocuments();
    }

    @Override
    public void publish( String id) throws CMSException {
        publish(id, false);
    }
    
    
    @Override
    public void unpublish(String id) throws CMSException {
        
        try {
            
            ((MemoryUserStorage) publishRepository.getUserStorage()).deleteDocumentNonRecurse(id, batchMode);
        
              
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
        
    }
    
    
    
    public void publish( String id, boolean update) throws CMSException {
        try {
        
            
        RepositoryDocument liveDoc = getSharedDocument(id);
        
        
        RepositoryDocument existingPublishedParent = null;
        RepositoryDocument existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getSharedDocument(id);
            existingPublishedParent = publishRepository.getSharedDocument(existingPublishedDoc.getParentInternalId());
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        RepositoryDocument publishedParent = null;
        String parentId = liveDoc.getParentInternalId();
        
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
        
        
        
        // Update old hierarchy
        List<String> childrenId;
        if( existingPublishedParent != null)   {
            existingPublishedParent.getChildrenId().remove(id);
            publishRepository.getUserStorage().updateDocument(existingPublishedParent.getInternalID(), existingPublishedParent, true);
        }
        
        
        // look for already published children
         
        childrenId = new ArrayList<>();
        for(String childId: liveDoc.getChildrenId())    {
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
        
        
        
        
        // Set parent children
        
        if( publishedParent != null) {
            if( ! publishedParent.getChildrenId().contains(id))
                publishedParent.getChildrenId().add( id);
            
            for (String childId:childToRemoveFromParent)    {
                publishedParent.getChildrenId().remove(childId);
            }
        
            // Reorder parent children (move effects)
           
            List<String> liveParentChildrenId = getSharedDocument(liveDoc.getParentInternalId()).getChildrenId();

            List<String> parentChildren = new ArrayList<>();
            
            for(String parentChildId : liveParentChildrenId)    {
               if( publishedParent.getChildrenId().contains(parentChildId))
                   parentChildren.add(parentChildId);
            }
            
            for(String parentChildId : publishedParent.getChildrenId())    {
                if( !parentChildren.contains(parentChildId))
                    parentChildren.add(parentChildId);
             }
            
            publishedParent.getChildrenId().clear();
            publishedParent.getChildrenId().addAll(parentChildren);
            
            
            publishRepository.getUserStorage().updateDocument(publishedParent.getInternalID(), publishedParent, true);
        }     
        
        
        // create published object
        RepositoryDocument publishedDoc;
        if( update == false || existingPublishedDoc == null)
            publishedDoc = (RepositoryDocument) liveDoc.duplicateForPublication(publishedParentId, childrenId, publishRepository);
        else    {
            publishedDoc = (RepositoryDocument) existingPublishedDoc.duplicateForPublication(publishedParentId, childrenId, publishRepository);
        }
        
        
        publishRepository.getUserStorage().addDocument(publishedDoc.getInternalID(), publishedDoc, false);
        
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
     }
    
    @Override
    public void reloadDatas() {
        ((MemoryUserStorage) getUserStorage()).clearDocuments();
        startInitBatch();
      }



    @Override
    public void addDocument(String internalID, RepositoryDocument document) throws CMSException {
        getUserStorage().addDocument(internalID, document, batchMode);
        
        if(!batchMode)  {   
            if(this instanceof StreamableRepository)    {
                UpdateInformations infos = new UpdateInformations(new UniversalID(getRepositoryName(), internalID), document.getSpaceId(), UpdateScope.SCOPE_SPACE, false);
                getSharedRepository().notifyUpdate( getUserStorage(), infos);
            }
        }
     }

    @Override
    public void updateDocument(String internalID, RepositoryDocument document) throws CMSException {
        getUserStorage().updateDocument(internalID, document, batchMode);
        
        if(!batchMode)  {        
            UpdateInformations infos = new UpdateInformations(new UniversalID(getRepositoryName(), internalID), document.getSpaceId(), UpdateScope.SCOPE_SPACE, false);
            getSharedRepository().notifyUpdate( getUserStorage(), infos);
        }        
    }

    @Override    
    public void deleteDocument(String id) throws CMSException {
        
        Document document = getDocument(id);
        
      
        if( publishRepository != null)  {
            try {
                publishRepository.getSharedDocument(id);
                publishRepository.getUserStorage().deleteDocument(id, batchMode);
                
                if(!batchMode)  {        
                    UpdateInformations infos = new UpdateInformations(new UniversalID(getRepositoryName(), id), document.getSpaceId(), UpdateScope.SCOPE_SPACE, true);
                    publishRepository.getSharedRepository().notifyUpdate( getUserStorage(), infos);
               }

                
                
            } catch(CMSException e)    {
                // not found
            }
         }
        
          
        getUserStorage().deleteDocument(id, batchMode);
          
         if(!batchMode)  {        
              UpdateInformations infos = new UpdateInformations(new UniversalID(getRepositoryName(), id), document.getSpaceId(), UpdateScope.SCOPE_SPACE, false);
              getSharedRepository().notifyUpdate( getUserStorage(), infos);
              
              
          }
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
        RepositoryDocument doc = new MemoryRepositoryDocument(this, null, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);
        
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
    
    
    @Override
    public void moveDocument(String srcId, String beforedestId, boolean endOfList) throws CMSException {
        
        MemoryRepositoryDocument targetParent; 
        MemoryRepositoryDocument srcDoc; 
        MemoryRepositoryDocument srcParent; 
        
        targetParent = (MemoryRepositoryDocument) getSharedDocument(beforedestId);
        if( !endOfList)
            targetParent = (MemoryRepositoryDocument) getSharedDocument(targetParent.getParentInternalId());
        
        
        srcDoc = (MemoryRepositoryDocument) getSharedDocument(srcId);
        srcParent = (MemoryRepositoryDocument) getSharedDocument(srcDoc.getParentInternalId());
        
        
        srcParent.getChildrenId().remove(srcId);
        
        // Search for place to insert
        int i=0;
        for(String childId : targetParent.getChildrenId()) {
            if( childId.equals(beforedestId))
                break;
            else
                i++;
                
        }
        
        targetParent.getChildrenId().add(i,srcId);
        
        
        srcDoc.setParentInternalId(targetParent.getId().getInternalID());
        
        
        getUserStorage().updateDocument(srcDoc.getInternalID(), srcDoc, true);
        getUserStorage().updateDocument(srcParent.getInternalID(), srcParent, true);
        getUserStorage().updateDocument(targetParent.getInternalID(), targetParent, false);
        
        if( supportPreview())   {
            publish(srcDoc.getInternalID(), true);
        }
        
        
    }


    @Override
    public void setNewId(String internalID, String newId) throws CMSException {
        
        try {
            MemoryRepositoryDocument doc = (MemoryRepositoryDocument) getSharedDocument(internalID);
            
            MemoryRepositoryDocument targetParent = (MemoryRepositoryDocument) getSharedDocument(doc.getParentInternalId());
            

            MemoryRepositoryDocument newDoc = (MemoryRepositoryDocument) doc.duplicateForNewId(newId);
            
            // Add at the same ordrer than old one
            int index = targetParent.getChildrenId().indexOf(internalID);
            targetParent.getChildrenId().add(index, newId);
            
             
            // Delete old doc
            ((MemoryUserStorage) getUserStorage()).deleteDocumentNonRecurse(internalID, true);

            // Update parent
             getUserStorage().updateDocument(targetParent.getInternalID(), targetParent, true);            

             // Add coument
            addDocument(newId, newDoc);
        } catch (Exception e)   {
            throw new CMSException(e);
        }
    }

}
