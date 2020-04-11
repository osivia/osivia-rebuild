package org.osivia.portal.services.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.model.DocumentImpl;
import org.osivia.portal.services.cms.model.PageImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService {
    
    
    private Map<String, Document> documents;

    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
    }
    
    /**
     * Adds the document.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addDocument( String id , String name, String parentId)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "doc." + name + "#"+id);   
        DocumentImpl doc = new DocumentImpl(id, name, parentId,new ArrayList<String>(), properties);
        doc.setSpaceId(parentId);
        documents.put(id, doc);
    }
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addWorkspace( String id)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);   
        properties.put("osivia.template", "/portalA/pageA");         
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef module = new ModuleRef( "win-"+id, "col-1", "0",  "SampleInstance");
        modules.add(module);
        SpaceImpl space = new SpaceImpl(id,  null,new ArrayList<String>(),properties,modules);
        space.setSpaceId(id);
        documents.put(id, space);
    }
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplateSpace( String id, List<String> children)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);   
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        SpaceImpl space = new SpaceImpl(id, null, children, properties,modules);
        space.setSpaceId(id);
        documents.put(id, space);
    }
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplatePage( String id, String name, String parentId, String spaceId,  List<String> children, List<ModuleRef> modules)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);   
      
        PageImpl page = new PageImpl(id, name, parentId , children, properties,modules);
        page.setSpaceId(spaceId);
        documents.put(id, page);
    }
    
    
    protected void pageA(String id, String name, String parentId, String spaceId, List<String> children) {
        
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef moduleA = new ModuleRef( "winA-"+id, "col-2", "0",  "SampleInstance");
        ModuleRef moduleB = new ModuleRef( "winB-"+id, "col-2", "1",  "SampleRemote");
        ModuleRef moduleC = new ModuleRef( "winC-"+id, "col-2", "2",  "SampleInstance");        
      
        modules.add(moduleA);
        modules.add(moduleB);
        modules.add(moduleC);
        
        addTemplatePage(  id, name, parentId,  spaceId,  children, modules) ;
    }
    
 
   protected void pageB(String id, String name, String parentId, String spaceId, List<String> children) {
        
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef moduleB = new ModuleRef( "winB-"+id, "col-2", "0",  "SampleInstance");
        modules.add(moduleB);
        
        addTemplatePage(  id, name, parentId,  spaceId,  children, modules) ;
    }

    
    
    protected void createWorskpace() {
        addDocument( "ID_DOC_1", "doc1", "space1");
        addWorkspace("space1");
    }
    
    
    protected void createTemplateSpace() {
        pageA("ID_PAGE_A", "pageA", "portalA","portalA", new ArrayList<String>());
        pageB("ID_PAGE_B", "pageB", "portalA","portalA", new ArrayList<String>());
       
        
        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("ID_PAGE_A");
        portalChildren.add("ID_PAGE_B");
        addTemplateSpace("portalA", portalChildren);
    } 
    
    @PostConstruct
    private void init()  {
        documents = new ConcurrentHashMap<>();
        createWorskpace();
        createTemplateSpace();
    }


    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, String id) throws CMSException {
       
        return documents.get(id);

    }

}
