package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.CMSNotImplementedRequestException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.service.Documents;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.services.cms.model.share.FolderImpl;
import org.osivia.portal.services.cms.model.share.PageImpl;
import org.osivia.portal.services.cms.model.share.DocumentImpl;
import org.osivia.portal.services.cms.repository.BaseUserRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;


import fr.toutatice.portail.cms.producers.test.TestRepository;

/**
 * The Class NativeMemoryRepository contains additionnal test functions
 * (publishing, add content, ...)
 */

public abstract class UserRepositoryTestBase extends BaseUserRepository implements TestRepository

{
    public UserRepositoryTestBase(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName, new InMemoryUserStorage());
    }
    

    
    protected void addDocument(String internalID, DocumentImpl document) {
        getUserStorage().addDocument(internalID, document, batchMode);
    }
    
    protected void updateDocument(String internalID, DocumentImpl document) {
        getUserStorage().updateDocument(internalID, document, batchMode);
    }



    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
    }


    @Override  
    public void addWindow(String id, String name, String portletName, String region, int position, String pageId,  Map<String,String> properties) throws CMSException {
        PageImpl page = (PageImpl) getSharedDocument(pageId);
        ModuleRef module = new ModuleRef("winD-" + System.currentTimeMillis(), region,  portletName, properties);
        
        if( position == POSITION_END)
            page.getModuleRefs().add(module);
        else
            page.getModuleRefs().add(position, module);
        
        updateDocument(pageId, page);
    }
    
    
    @Override
    public void addFolder(String id, String name, String parentId) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);
        
        DocumentImpl parent = getSharedDocument(parentId);

        FolderImpl folder = new FolderImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);        
        
        addDocument(id, folder);
  
    }


    @Override
    public void addDocument(String id, String name, String parentId) throws CMSException {
        
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);
        
        DocumentImpl parent = getSharedDocument(parentId);
        DocumentImpl doc = new DocumentImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);
        
        addDocument(id, doc);
     }
    
    @Override
    public void renameDocument(String id,String title) throws CMSException {
        
        
        DocumentImpl doc = getSharedDocument(id);
        doc.setTitle(title);
        
        updateDocument(id, doc);
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
        DocumentImpl doc = (DocumentImpl) getSharedDocument(id);
        return doc.getACL();
        
     }

    @Override
    public void setACL(String id, List<String> acls) throws CMSException {
        DocumentImpl doc = (DocumentImpl) getSharedDocument(id);
        doc.setACL(acls);
        updateDocument(id, doc);
     }
    
}
