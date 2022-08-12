package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.portal.core.model.portal.control.portal.PortalControlContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;

public class UserWorkspacesRepository extends UserRepositoryTestBase {

    
    public UserWorkspacesRepository(SharedRepositoryKey repositoryKey, String userName) {
        super(repositoryKey, null, userName);
    }

    
    
    private void addFolder(String id, String name, String parentId, String spaceId, List<String> children) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);

        MemoryRepositoryFolder folder = new MemoryRepositoryFolder(this, id, name, parentId, spaceId, children, properties);
        addDocument(id, folder);
    }

    
    private void addDocument(String id, String name, String parentId, String spaceId ) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);

        RepositoryDocument doc = new MemoryRepositoryDocument(this, null, id, name, parentId, spaceId, new ArrayList<String>(), properties);
        addDocument(id, doc);
    }

    
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     * @throws CMSException 
     */
    private void addWorkspace(String id,  List<String> children) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> moduleProperties = new ConcurrentHashMap<>();         
        ModuleRef module = new ModuleRef("win-" + id, "col-1",  "SampleInstance", moduleProperties);
        modules.add(module);
        MemoryRepositorySpace space = new MemoryRepositorySpace(this,id, id,new UniversalID("templates", "ID_TEMPLATE_WORKSPACE" ),children, properties, modules);
        addDocument(id, space);
    }


    protected void createWorskpace() {

        try {
        addFolder("ID_FOLD_1", "folder1", "space1", "space1", new ArrayList<String>(Arrays.asList("ID_FOLD_11")));
        addFolder("ID_FOLD_11", "folder11", "ID_FOLD_1", "space1", new ArrayList<String>());
        addDocument("ID_DOC_1", "doc1", "ID_FOLD_11", "space1");
        addWorkspace("space1",  new ArrayList<String>(Arrays.asList("ID_FOLD_1")));
        } catch(Exception e)    {
            throw new RuntimeException(e);
        }
    }



    protected void initDocuments() {
        createWorskpace();
    }


    
    
    /**
     * {@inheritDoc}
     */


  
}
