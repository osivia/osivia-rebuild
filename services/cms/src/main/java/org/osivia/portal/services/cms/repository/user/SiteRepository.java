package org.osivia.portal.services.cms.repository.user;

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
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.model.PageImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.producers.sample.inmemory.IPageMemoryRepository;


public class SiteRepository extends InMemoryUserRepository implements IPageMemoryRepository {

    public SiteRepository(SharedRepositoryKey repositoryKey) {
        super(repositoryKey);
    }


    /**
     * Adds the site space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addSiteSpace(String id, List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Site." + id);

        List<ModuleRef> modules = new ArrayList<>();
        SpaceImpl space = new SpaceImpl(this, id, id, new UniversalID("templates", "ID_TEMPLATE_SITE"), children, properties, modules);
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
        properties.put("dc:title", "Site." + id);


        PageImpl page = new PageImpl(this, id, name, null, parentId, spaceId, children, properties, modules);

        addDocument(id, page);
    }


    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
        NuxeoMockDocumentImpl parent = getDocument(parentId);
        parent.getChildrenId().add(id);
        addSitePage(id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), new ArrayList<ModuleRef>());
        updatePaths();

        notifyChanges();
    }

    @Override
    public void addWindow(String id, String name, String portletName, String region, String pageId) throws CMSException {
        Page page = (Page) getDocument(pageId);
        ModuleRef module = new ModuleRef("winD-" + System.currentTimeMillis(), region, "0", portletName);
        page.getModuleRefs().add(module);


        notifyChanges();
    }


    protected void initDocuments() {
        createTemplateSpace();
    }


    @Override
    public boolean supportPreview() {
        return true;
    }


}
