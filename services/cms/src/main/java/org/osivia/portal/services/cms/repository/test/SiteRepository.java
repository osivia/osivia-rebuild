package org.osivia.portal.services.cms.repository.test;

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
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;




public class SiteRepository extends UserRepositoryTestBase  {

    public SiteRepository(SharedRepositoryKey repositoryKey,  BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName);
    }

    
    private String generateTitle( String originalTitle) {
        return "["+repositoryKey.getLocale().getLanguage().toString()+"]" + originalTitle;
    }
    
    /**
     * Adds the site space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addSiteSpace(String id, List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", generateTitle("Site "+ id));

        List<ModuleRef> modules = new ArrayList<>();
        MemoryRepositorySpace space = new MemoryRepositorySpace(this, id, id, new UniversalID("templates", "ID_TEMPLATE_SITE"), children, properties, modules);
        addDocument(id, space);
    }


    protected void createTemplateSpace() {
        List<String> portalChildren = new ArrayList<String>();

        addSiteSpace("ID_SITE_A", portalChildren);
    }


    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addSitePage(String id, String name, String parentId, String spaceId, List<String> children, List<ModuleRef> modules) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", generateTitle("Page "+ id));
        MemoryRepositoryPage page = new MemoryRepositoryPage(this, id, name, null, parentId, spaceId, children, properties, modules);
        addDocument(id, page);
    }


    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
        RepositoryDocument parent = (RepositoryDocument) getSharedDocument(parentId);
        addSitePage(id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), new ArrayList<ModuleRef>());
    }


    protected void initDocuments() {
        createTemplateSpace();
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
