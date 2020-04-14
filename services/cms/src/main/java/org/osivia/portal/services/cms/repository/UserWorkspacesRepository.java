package org.osivia.portal.services.cms.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.services.cms.model.DocumentImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;

public class UserWorkspacesRepository extends InMemoryUserRepository {

    public UserWorkspacesRepository( String repositoryName) {
        super(repositoryName);
    }




    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addWorkspace(String id) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);
        properties.put("osivia.template", new UniversalID("templates","ID_PAGE_A"));
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef module = new ModuleRef("win-" + id, "col-1", "0", "SampleInstance");
        modules.add(module);
        SpaceImpl space = new SpaceImpl(this,id, null, new ArrayList<String>(), properties, modules);
        space.setSpaceInternalId(id);
        addDocument(id, space);
    }





    protected void createWorskpace() {
        addDocument("ID_DOC_1", "doc1", "space1");
        addWorkspace("space1");
    }




    protected void initDocuments() {
        createWorskpace();
    }


    
    
    /**
     * {@inheritDoc}
     */


  
}
