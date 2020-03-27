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
    private void addDocument( String id , String parentId)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "doc." + id);   
        DocumentImpl doc = new DocumentImpl(id, parentId,new ArrayList<String>(), properties);
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
        properties.put("osivia.template", "/portalA/default/pageA");         
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
    private void addTemplatePage( String id, String parentId, String spaceId,  List<String> children)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);   
      
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef moduleA = new ModuleRef( "winA-"+id, "col-2", "0",  "SampleInstance");
        ModuleRef moduleB = new ModuleRef( "winB-"+id, "col-2", "1",  "SampleRemote");
        ModuleRef moduleC = new ModuleRef( "winC-"+id, "col-2", "2",  "SampleInstance");        
      
        modules.add(moduleA);
        modules.add(moduleB);
        modules.add(moduleC);
        
        PageImpl page = new PageImpl(id,  parentId , children, properties,modules);
        page.setSpaceId(spaceId);
        documents.put(id, page);
    }
    
    protected void createWorskpace() {
        addDocument( "doc1","space1");
        addWorkspace("space1");
    }
    
    
    protected void createTemplateSpace() {
        addTemplatePage("pageA", "portalA","portalA", new ArrayList<String>());
        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("pageA");
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
