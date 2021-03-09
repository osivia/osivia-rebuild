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
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.RepositorySpace;

public class UserWorkspacesRepository extends UserRepositoryTestBase {

    
    public UserWorkspacesRepository(SharedRepositoryKey repositoryKey, String userName) {
        super(repositoryKey, null, userName);
    }

    
    
    private void addFolder(String id, String name, String parentId, String spaceId, List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);

        RepositoryFolder folder = new RepositoryFolder(this, id, name, parentId, spaceId, children, properties);
        addDocument(id, folder);
    }

    
    private void addDocument(String id, String name, String parentId, String spaceId ) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);

        RepositoryDocument doc = new RepositoryDocument(this, id, name, parentId, spaceId, new ArrayList<String>(), properties);
        addDocument(id, doc);
    }

    
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addWorkspace(String id,  List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> moduleProperties = new ConcurrentHashMap<>();         
        ModuleRef module = new ModuleRef("win-" + id, "col-1",  "SampleInstance", moduleProperties);
        modules.add(module);
        RepositorySpace space = new RepositorySpace(this,id, id,new UniversalID("templates", "ID_TEMPLATE_WORKSPACE" ),children, properties, modules);
        addDocument(id, space);
    }


    protected void createWorskpace() {

        addFolder("ID_FOLD_1", "folder1", "space1", "space1", new ArrayList<String>(Arrays.asList("ID_FOLD_11")));
        addFolder("ID_FOLD_11", "folder11", "ID_FOLD_1", "space1", new ArrayList<String>());
        addDocument("ID_DOC_1", "doc1", "ID_FOLD_11", "space1");
        addWorkspace("space1",  new ArrayList<String>(Arrays.asList("ID_FOLD_1")));
    }



    protected void initDocuments() {
        createWorskpace();
    }


    
    
    /**
     * {@inheritDoc}
     */


  
}
