package org.osivia.portal.services.cms.repository.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;

import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.RepositoryFactory;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;




public class SiteRepository extends UserRepositoryMemoryBase  {

    public SiteRepository(RepositoryFactory repositoryFactory,SharedRepositoryKey repositoryKey,  BaseUserRepository publishRepository, String userName) {
        super(repositoryFactory, repositoryKey, publishRepository, userName);
    }


    
    /**
     * Adds the site space.
     *
     * @param id the id
     * @param properties the properties
     * @throws CMSException 
     */
    private void addSiteSpace(String id, List<String> children) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", generateTitle("Site "+ id));
        properties.put("templates.namespace", "templates");
        
        List<ModuleRef> modules = new ArrayList<>();
        MemoryRepositorySpace space = new MemoryRepositorySpace(this, id, id, new UniversalID("templates", "ID_TEMPLATE_SITE"), children, properties, modules);
        addDocument(id, space);
    }


    /**
     * Creates the template space.
     * @throws CMSException 
     */
    protected void createTemplateSpace() throws CMSException {
        List<String> portalChildren = new ArrayList<String>();

        addSiteSpace("ID_SITE_A", portalChildren);
    }





    protected void initDocuments() {
        try {
            createTemplateSpace();
        } catch (CMSException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean supportPreview() {
        return true;
    }


    @Override
    public void publish(String id) throws CMSException {
        super.publish(id);
        
    }

    
    @Override
    public boolean supportPageEdition() {
        return true;
    }
    
    @Override
    public List<Locale> getLocales() {
        return Arrays.asList(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN);
    }




}
