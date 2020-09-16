package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.services.cms.model.FolderImpl;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.user.InMemoryUserRepository;

import fr.toutatice.portail.cms.producers.test.TestRepository;

public abstract class NativeMemoryRepository extends InMemoryUserRepository implements TestRepository

{
    public NativeMemoryRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {
        super(repositoryKey, publishRepository);
    }

    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
    }


    @Override  
    public void addWindow(String id, String name, String portletName, String region, int position, String pageId,  Map<String,String> properties) throws CMSException {
        Page page = (Page) getDocument(pageId);
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
        
        NuxeoMockDocumentImpl parent = getDocument(parentId);
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
        
        NuxeoMockDocumentImpl parent = getDocument(parentId);
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
    
}
