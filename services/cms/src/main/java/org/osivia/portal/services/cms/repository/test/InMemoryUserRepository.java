package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.DocumentException;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.services.cms.model.test.FolderImpl;
import org.osivia.portal.services.cms.model.test.NavigationItemImpl;
import org.osivia.portal.services.cms.model.test.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.service.CMSEventImpl;

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
    
    private boolean previewRepository = false;
    
    private String userName = null;
    


    public InMemoryUserRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository, String userName) {
        super();
        this.repositoryKey = repositoryKey;
        this.listeners = new ArrayList<>();
        if( publishRepository != null)  {
            this.publishRepository = publishRepository;
            this.previewRepository = true;        
        }
        this.userName = userName;
        init(repositoryKey);
    }

    

    
    public String getUserName() {
        return userName;
    }

    
    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    public void notifyChanges( Document src, List<Request> requests) {
        getSharedRepository().notifyChanges( new CMSEventImpl( src, requests));
    }
    
    public void notifyChanges() {
        getSharedRepository().notifyChanges( new CMSEventImpl());
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



    protected NuxeoMockDocumentImpl getSharedDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(internalId);
    }

    
    public Document getDocument(String internalId) throws CMSException {
        
        NuxeoMockDocumentImpl document = getSharedRepository().getDocument(internalId);
        if( checkACL(document))
            return document;
        else
            throw new DocumentForbiddenException();
    }

    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        NuxeoMockDocumentImpl document = (NuxeoMockDocumentImpl) getSharedDocument(internalId);
        if (!document.isNavigable()) {
            document = getNavigationParent( document);
        }
        return new NavigationItemImpl(this, document);
    }


    
    
    private boolean checkACL(NuxeoMockDocumentImpl doc)   {
        List<String> acls = doc.getACL();
        if( acls.size() == 0)
            return true;
        if( userName != null && acls.contains("group:members"))
            return true;
        return false;
        
    }

    public NuxeoMockDocumentImpl getNavigationParent(Document document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        NuxeoMockDocumentImpl parent = null;
        do  {
            NuxeoMockDocumentImpl parentTmp = (NuxeoMockDocumentImpl) getSharedDocument(docImpl.getParentInternalId());
            if( checkACL(parentTmp))
                parent = parentTmp;
            else
                docImpl = parentTmp;
        } while(parent == null);
        
        return parent;

    }

    public List<NuxeoMockDocumentImpl> getNavigationChildren(NuxeoMockDocumentImpl document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<NuxeoMockDocumentImpl> children = new ArrayList<>();
        for (String id : childrenId) {
            NuxeoMockDocumentImpl sharedDocument = getSharedDocument(id);
            if( sharedDocument.isNavigable())   {
                if( checkACL(sharedDocument))
                    children.add(sharedDocument);
            }
        }

        return children;
    }
    
    @Override
    public void contentModified( CMSEvent e) {
        for (RepositoryListener listener : listeners) {
            listener.contentModified( e);
        }
    }



    @Override
    public List<Locale> getLocales() {
        return Arrays.asList(Locale.FRENCH);
    }

    
    
    
   

   



}
