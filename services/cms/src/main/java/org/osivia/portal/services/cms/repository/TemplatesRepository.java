package org.osivia.portal.services.cms.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.model.DocumentImpl;
import org.osivia.portal.services.cms.model.PageImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;

public class TemplatesRepository extends InMemoryUserRepository {

      public TemplatesRepository( String repositoryName) {
        super(repositoryName);
    }

    /**
     * {@inheritDoc}
     */


    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplateSpace(String id, List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        SpaceImpl space = new SpaceImpl(this,id, null, children, properties, modules);
        space.setSpaceInternalId(id);
        addDocument(id, space);
    }

    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplatePage(String id, String name, String parentId, String spaceId, List<String> children, List<ModuleRef> modules) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);

        PageImpl page = new PageImpl(this,id, name, parentId, children, properties, modules);
        page.setSpaceInternalId(spaceId);
        addDocument(id, page);
    }


    protected void pageA(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef moduleA = new ModuleRef("winA-" + id, "col-2", "0", "SampleInstance");
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2", "1", "SampleRemote");
        ModuleRef moduleC = new ModuleRef("winC-" + id, "col-2", "2", "SampleInstance");

        modules.add(moduleA);
        modules.add(moduleB);
        modules.add(moduleC);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }


    protected void pageB(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2", "0", "SampleInstance");
        modules.add(moduleB);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }


    protected void createTemplateSpace() {
        pageA("ID_PAGE_A", "pageA", "portalA", "portalA", new ArrayList<String>());
        pageB("ID_PAGE_B", "pageB", "portalA", "portalA", new ArrayList<String>());


        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("ID_PAGE_A");
        portalChildren.add("ID_PAGE_B");
        addTemplateSpace("portalA", portalChildren);
    }


    protected void initDocuments() {
        createTemplateSpace();
    }



    
    


}
