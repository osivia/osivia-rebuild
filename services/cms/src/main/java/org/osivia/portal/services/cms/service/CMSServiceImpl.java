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
    private void addDocument( String id , String spaceId)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "doc." + id);   
        DocumentImpl doc = new DocumentImpl(id, properties);
        doc.setSpaceId(spaceId);
        documents.put(id, doc);
        
    }
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addSpace( String id)   {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);   
        properties.put("osivia.template", "/portalA/pageA");         
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        ModuleRef module = new ModuleRef( "win-"+id, "col-1", "0",  "SampleInstance");
        modules.add(module);
       SpaceImpl space = new SpaceImpl(id, properties,modules);
        space.setSpaceId(id);
        documents.put(id, space);
    }
    
    
    @PostConstruct
    private void init()  {
        documents = new ConcurrentHashMap<>();
        addDocument( "doc1","space1");
        addSpace("space1");
    }

    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, String id) throws CMSException {
       
        return documents.get(id);

    }

}
