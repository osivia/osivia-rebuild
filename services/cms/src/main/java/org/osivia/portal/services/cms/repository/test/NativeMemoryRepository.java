package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.services.cms.model.test.FolderImpl;
import org.osivia.portal.services.cms.model.test.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;


import fr.toutatice.portail.cms.producers.test.TestRepository;

/**
 * The Class NativeMemoryRepository contains additionnal test functions
 * (publishing, add content, ...)
 */

public abstract class NativeMemoryRepository extends InMemoryUserRepository implements TestRepository

{
    public NativeMemoryRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {
        super(repositoryKey, publishRepository);
    }
    

    protected void addDocument(String internalID, NuxeoMockDocumentImpl document) {
        getSharedRepository().addDocument(internalID, document);
    }

    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
    }


    @Override  
    public void addWindow(String id, String name, String portletName, String region, int position, String pageId,  Map<String,String> properties) throws CMSException {
        Page page = (Page) getInternalDocument(pageId);
        ModuleRef module = new ModuleRef("winD-" + System.currentTimeMillis(), region,  portletName, properties);
        
        if( position == POSITION_END)
            page.getModuleRefs().add(module);
        else
            page.getModuleRefs().add(position, module);
        
        notifyChanges();
    }
    
    
    @Override
    public void addFolder(String id, String name, String parentId) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);
        
        NuxeoMockDocumentImpl parent = getInternalDocument(parentId);
        parent.getChildrenId().add(id);        
 
        FolderImpl folder = new FolderImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);        
        
        addDocument(id, folder);
        updatePaths();
        notifyChanges();         
    }


    @Override
    public void addDocument(String id, String name, String parentId) throws CMSException {
        
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);
        
        NuxeoMockDocumentImpl parent = getInternalDocument(parentId);
        parent.getChildrenId().add(id);        

        NuxeoMockDocumentImpl doc = new NuxeoMockDocumentImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);
        
        addDocument(id, doc);
        updatePaths();
        notifyChanges();           
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
    public void publish( String id) throws CMSException {
        try {
        
        NuxeoMockDocumentImpl doc = getInternalDocument(id);
        NuxeoMockDocumentImpl existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getInternalDocument(id);
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        NuxeoMockDocumentImpl publishedParent = null;
        String parentId = doc.getParentInternalId();
        
        while(  publishedParent == null && parentId != null)   {
            try {
                publishedParent = publishRepository.getInternalDocument(parentId);
            } catch(CMSException e)    {
                // not found
                NuxeoMockDocumentImpl parent = getInternalDocument(parentId);
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
                NuxeoMockDocumentImpl publishedChild = null;
                try {
                    publishedChild = publishRepository.getInternalDocument(childId);
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
        }     
        
        
        // create published object
        NuxeoMockDocumentImpl publishedDoc = (NuxeoMockDocumentImpl) doc.duplicate(publishedParentId, childrenId, publishRepository);
        publishRepository.getSharedRepository().addDocument(publishedDoc.getInternalID(), publishedDoc);
        
        
        
        publishRepository.updatePaths();
        publishRepository.notifyChanges(); 
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
        
        
        
    }
    
    public List<Document> getChildren(String id) throws CMSException {
        List<Document> childrens = new ArrayList<>();
        for (NuxeoMockDocumentImpl doc:getChildren(getInternalDocument(id))) {
            childrens.add(doc);
        }
        
        return childrens;
    }
    
}
