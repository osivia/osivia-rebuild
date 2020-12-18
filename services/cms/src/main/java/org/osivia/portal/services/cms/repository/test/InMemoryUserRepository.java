package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.services.cms.model.test.FolderImpl;
import org.osivia.portal.services.cms.model.test.NavigationItemImpl;
import org.osivia.portal.services.cms.model.test.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserRepository;

import fr.toutatice.portail.cms.producers.test.TestRepository;

/**
 * Minimal user repository
 * 
 * Stores data in memory
 * Doesn't support user rights.
 */


public abstract class InMemoryUserRepository implements UserRepository, RepositoryListener {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";

    protected SharedRepositoryKey repositoryKey;

    protected List<RepositoryListener> listeners;
    
    protected InMemoryUserRepository publishRepository;
    
    private static Map<SharedRepositoryKey, SharedRepository>  sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    
    private boolean previewRepository = false;;



    public InMemoryUserRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {
        super();
        this.repositoryKey = repositoryKey;
        this.listeners = new ArrayList<>();
        if( publishRepository != null)  {
            this.publishRepository = publishRepository;
            this.previewRepository = true;        
        }
        init(repositoryKey);
    }

    
    
    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    public void notifyChanges() {
        getSharedRepository().notifyChanges();
    }

    
    public boolean isPreviewRepository() {
        return previewRepository;
    }


    private void init(SharedRepositoryKey repositoryKey) {

        boolean initRepository = false;
        if( getSharedRepository() == null)    {
            sharedRepositories.put(repositoryKey, new SharedRepository(repositoryKey.getRepositoryName()));   
            initRepository = true;
        }
        
        sharedRepositories.get(repositoryKey).addListener(this);
        
        if(initRepository)  {
            initDocuments();
            updatePaths();     
        }
        
        
    }

    public void updatePaths() {
        getSharedRepository().updatePaths();
    }

    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryKey.getRepositoryName();
    }


    

    public SharedRepository getSharedRepository() {
         return sharedRepositories.get(repositoryKey);
    }


    
    /**
     * {@inheritDoc}
     */


    public NuxeoMockDocumentImpl getInternalDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(internalId);
    }

    
    public Document getDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(internalId);
    }

    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        NuxeoMockDocumentImpl document = (NuxeoMockDocumentImpl) getInternalDocument(internalId);
        if (!document.isNavigable()) {
            document = document.getNavigationParent();
        }
        return new NavigationItemImpl(document);
    }


    public NuxeoMockDocumentImpl getParent(Document document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        return (NuxeoMockDocumentImpl) getInternalDocument(docImpl.getParentInternalId());

    }

    public List<NuxeoMockDocumentImpl> getChildren(NuxeoMockDocumentImpl document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<NuxeoMockDocumentImpl> children = new ArrayList<>();
        for (String id : childrenId) {
            children.add(getInternalDocument(id));
        }

        return children;
    }
    
    @Override
    public void contentModified() {
        for (RepositoryListener listener : listeners) {
            listener.contentModified();
        }
    }



    @Override
    public List<Locale> getLocales() {
        return Arrays.asList(Locale.FRENCH);
    }

    
    
    
   

   



}
