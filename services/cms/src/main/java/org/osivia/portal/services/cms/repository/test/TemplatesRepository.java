package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.model.test.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.model.test.PageImpl;
import org.osivia.portal.services.cms.model.test.SpaceImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.producers.test.TestRepository;


public class TemplatesRepository extends MemoryRepository  {

      
      public TemplatesRepository(SharedRepositoryKey repositoryKey, String userName) {
          super(repositoryKey, null, userName);
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
        Map<String,String> editionProperties = new ConcurrentHashMap<>();
        editionProperties.put("osivia.hideTitle", "1");
        ModuleRef edition = new ModuleRef("edition" , "top", "EditionInstance", editionProperties); 
        modules.add(edition);  
        Map<String,String> navProperties = new ConcurrentHashMap<>(); 
        navProperties.put("osivia.hideTitle", "1");        
        ModuleRef nav = new ModuleRef("nav" , "nav",  "MenuInstance", navProperties); 
        
        modules.add(nav);     
        SpaceImpl space = new SpaceImpl(this,id, id,null,children, properties, modules);
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

        
        PageImpl page = new PageImpl(this,id, name, null, parentId, spaceId, children, properties, modules);
        page.setInheritedRegions(Arrays.asList("top","nav"));

        addDocument(id, page);
    }


    protected void pageA(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> aProperties = new ConcurrentHashMap<>();       
        ModuleRef moduleA = new ModuleRef("winA-" + id, "col-2",  "SampleInstance", aProperties);
        Map<String,String> bProperties = new ConcurrentHashMap<>();          
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2",  "ReactPortletInstance", bProperties);


        modules.add(moduleA);
        modules.add(moduleB);


        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }
    
    
    protected void pageA1(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> cProperties = new ConcurrentHashMap<>();    
        ModuleRef moduleC = new ModuleRef("winC-" + id, "col-2",  "ContentInstance", cProperties);

        modules.add(moduleC);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }


    protected void pageB(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> bProperties = new ConcurrentHashMap<>();    
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2", "SampleInstance", bProperties);
        modules.add(moduleB);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }


    protected void createTemplateSpace() {
        pageA("ID_PAGE_A", "pageA", "portalA", "portalA", new ArrayList<String>(Arrays.asList("ID_TEMPLATE_WORKSPACE")));
        pageA1("ID_TEMPLATE_WORKSPACE", "workspace", "ID_PAGE_A", "portalA", new ArrayList<String>());
        pageB("ID_TEMPLATE_SITE", "site", "portalA", "portalA", new ArrayList<String>());


        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("ID_PAGE_A");
        portalChildren.add("ID_TEMPLATE_SITE");
        addTemplateSpace("portalA", portalChildren);
    }

@Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
        NuxeoMockDocumentImpl parent = getSharedDocument(parentId);
        addTemplatePage(id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), new ArrayList<ModuleRef>());
    }
    


    protected void initDocuments() {
        createTemplateSpace();
    }


    @Override
    public boolean supportPageEdition() {
        return true;
    }


}
