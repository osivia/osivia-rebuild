package org.osivia.portal.services.cms.repository.user;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.services.cms.model.NavigationItemImpl;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;

public abstract class InMemoryUserRepository implements RepositoryListener {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";

    protected SharedRepositoryKey repositoryKey;

    protected List<RepositoryListener> listeners;
    
    
    private static Map<SharedRepositoryKey, SharedRepository>  sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    



    public InMemoryUserRepository(SharedRepositoryKey repositoryKey) {
        super();
        this.repositoryKey = repositoryKey;
        this.listeners = new ArrayList<>();
        init(repositoryKey);
    }

    
    
    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    protected void notifyChanges() {
        getSharedRepository().notifyChanges();
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

    protected void updatePaths() {
        getSharedRepository().updatePaths();
    }

    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryKey.getRepositoryName();
    }



    protected SharedRepository getSharedRepository() {
         return sharedRepositories.get(repositoryKey);
    }

    protected void addDocument(String internalID, NuxeoMockDocumentImpl document) {
        getSharedRepository().addDocument(internalID, document);
    }
    
    /**
     * {@inheritDoc}
     */


    public NuxeoMockDocumentImpl getDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(internalId);
    }


    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        NuxeoMockDocumentImpl document = getDocument(internalId);
        if (!document.isNavigable()) {
            document = document.getNavigationParent();
        }
        return new NavigationItemImpl(document);
    }


    public NuxeoMockDocumentImpl getParent(Document document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        return getDocument(docImpl.getParentInternalId());

    }

    public List<NuxeoMockDocumentImpl> getChildren(NuxeoMockDocumentImpl document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<NuxeoMockDocumentImpl> children = new ArrayList<>();
        for (String id : childrenId) {
            children.add(getDocument(id));
        }

        return children;
    }
    
    @Override
    public void contentModified() {
        for (RepositoryListener listener : listeners) {
            listener.contentModified();
        }
    }


}
