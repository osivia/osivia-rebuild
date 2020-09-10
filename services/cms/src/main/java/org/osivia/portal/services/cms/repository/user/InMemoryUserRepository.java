package org.osivia.portal.services.cms.repository.user;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.services.cms.model.FolderImpl;
import org.osivia.portal.services.cms.model.NavigationItemImpl;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;

import fr.toutatice.portail.cms.producers.sample.inmemory.IRepositoryUpdate;

public abstract class InMemoryUserRepository implements RepositoryListener, IRepositoryUpdate {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";

    protected SharedRepositoryKey repositoryKey;

    protected List<RepositoryListener> listeners;
    
    protected InMemoryUserRepository publishRepository;
    
    private static Map<SharedRepositoryKey, SharedRepository>  sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    



    public InMemoryUserRepository(SharedRepositoryKey repositoryKey) {
        super();
        this.repositoryKey = repositoryKey;
        this.listeners = new ArrayList<>();
        init(repositoryKey);
    }

    
    public InMemoryUserRepository(SharedRepositoryKey repositoryKey, InMemoryUserRepository publishRepository) {
        this( repositoryKey);
        this.publishRepository = publishRepository;
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

    public void publish( String id) throws CMSException {
        try {
        
        NuxeoMockDocumentImpl doc = getDocument(id);
        NuxeoMockDocumentImpl existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getDocument(id);
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        NuxeoMockDocumentImpl publishedParent = null;
        String parentId = doc.getParentInternalId();
        
        while(  publishedParent == null && parentId != null)   {
            try {
                publishedParent = publishRepository.getDocument(parentId);
            } catch(CMSException e)    {
                // not found
                NuxeoMockDocumentImpl parent = getDocument(parentId);
                parentId = parent.getParentInternalId();                
            }
        }
        
        
        List<String> childToRemoveFromParent = new ArrayList<>();
        
        // Get parent
        String publishedParentId;
        if( publishedParent != null) {
            publishedParentId = publishedParent.getInternalID();
        }   else
            publishedParentId = null;
        
        
        
        // Update hierarchy
        List<String> childrenId;
        if( existingPublishedDoc != null)   {
            // replacement
            childrenId = existingPublishedDoc.getChildrenId();
        }
        else    {
            // new object : look for published children
            childrenId = new ArrayList<>();
            for(String childId: doc.getChildrenId())    {
                NuxeoMockDocumentImpl publishedChild = null;
                try {
                    publishedChild = publishRepository.getDocument(childId);
                } catch(CMSException e)    {
                    // not found
                }    
                if( publishedChild != null) {
                    // Add to current node
                    childrenId.add(childId);
                    // Remove child from parent
                    childToRemoveFromParent.add(childId);
                    // reset child parent
                    publishedChild.setParentInternalId( id);
                }
            }            
        }
        
        
        // Set parent children
        if( publishedParent != null) {
            if( ! publishedParent.getChildrenId().contains(id))
                publishedParent.getChildrenId().add( id);
            
            for (String childId:childToRemoveFromParent)    {
                publishedParent.getChildrenId().remove(childId);
            }
        }     
        
        
        // create published object
        NuxeoMockDocumentImpl publishedDoc = (NuxeoMockDocumentImpl) doc.duplicate(publishedParentId, childrenId, publishRepository);
        publishRepository.getSharedRepository().addDocument(publishedDoc.getInternalID(), publishedDoc);
        
        
        
        publishRepository.updatePaths();
        publishRepository.notifyChanges(); 
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
        
        
        
    }
    

    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
    }


    @Override
    public void addWindow(String id, String name, String portletName, String region, String pageId) throws CMSException {
     }

    
    @Override
    public void addFolder(String id, String name, String parentId) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Folder." + id);
        
        NuxeoMockDocumentImpl parent = getDocument(parentId);
        parent.getChildrenId().add(id);        
 
        FolderImpl folder = new FolderImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);        
        
        addDocument(id, folder);
        updatePaths();
        notifyChanges();         
    }


    @Override
    public void addDocument(String id, String name, String parentId) throws CMSException {
        
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Document." + id);
        
        NuxeoMockDocumentImpl parent = getDocument(parentId);
        parent.getChildrenId().add(id);        

        NuxeoMockDocumentImpl doc = new NuxeoMockDocumentImpl(this, id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), properties);
        
        addDocument(id, doc);
        updatePaths();
        notifyChanges();           
    }


    @Override
    public boolean supportPreview() {
        return false;
     }


    @Override
    public boolean supportPageEdition() {
        return false;
    }



}
