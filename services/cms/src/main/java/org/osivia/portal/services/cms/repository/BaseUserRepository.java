package org.osivia.portal.services.cms.repository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.services.cms.model.share.DocumentImpl;
import org.osivia.portal.services.cms.model.share.PageImpl;
import org.osivia.portal.services.cms.model.share.SpaceImpl;
import org.osivia.portal.services.cms.model.user.NavigationItemImpl;

import org.osivia.portal.services.cms.repository.cache.SharedRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.services.cms.repository.spi.UserRepository;
import org.osivia.portal.services.cms.repository.spi.UserStorage;


/**
 * Minimal user repository
 * 
 * Stores data in memory
 * Doesn't support user rights.
 */


public abstract class BaseUserRepository implements UserRepository, RepositoryListener {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";

    protected SharedRepositoryKey repositoryKey;

 

    protected List<RepositoryListener> listeners;
    
    protected BaseUserRepository publishRepository;
    
    private static Map<SharedRepositoryKey, SharedRepository>  sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    
    private boolean previewRepository = false;
    
    private String userName = null;
    
    protected boolean batchMode = false;
    
    UserStorage userStorage;
    
    public ThreadLocal<PortalControllerContext> portalCtx = new ThreadLocal<PortalControllerContext>();

    public BaseUserRepository(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName, UserStorage userStorage) {
        super();
        this.repositoryKey = repositoryKey;
        this.listeners = new ArrayList<>();
        if( publishRepository != null)  {
            this.publishRepository = publishRepository;
            this.previewRepository = true;        
        }
        this.userName = userName;
        this.userStorage = userStorage;
        userStorage.setUserRepository(this);
        init(repositoryKey);
    }

    
    public SharedRepositoryKey getRepositoryKey() {
        return repositoryKey;
    }


    
    public String getUserName() {
        return userName;
    }

    public Principal getPrincipal()   {
        Principal principal = null;
        PortalControllerContext ctx = portalCtx.get();
        if( ctx.getHttpServletRequest() != null)    {
             principal = ctx.getHttpServletRequest().getUserPrincipal();
        }
        return principal;
    }
    
    public boolean isAdministrator()    {
        Principal principal = getPrincipal();
        if( principal != null && principal.toString().contains("Administrators")) {
            return true;
        }
        return false;
    }
    
    
    @Override
    public void setPortalContext(PortalControllerContext portalContext) {
        portalCtx.set(portalContext);
     }
    
    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    
    public boolean isPreviewRepository() {
        return previewRepository;
    }


    private void init(SharedRepositoryKey repositoryKey) {
        

        boolean initRepository = false;
        if( getSharedRepository() == null)    {
            sharedRepositories.put(repositoryKey, new SharedRepository(repositoryKey.getRepositoryName(), userStorage));   
            initRepository = true;
        }
        
        
        sharedRepositories.get(repositoryKey).addListener(this);
        
        if(initRepository)  {
            batchMode = true;
            try {
            initDocuments();
            } finally    {
                batchMode = false;
            }
            getSharedRepository().endBatch();    
        }
        
        
    }


    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryKey.getRepositoryName();
    }

   

    public SharedRepository getSharedRepository() {
         return sharedRepositories.get(repositoryKey);
    }



    public DocumentImpl getSharedDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(internalId);
    }

    
    public Document getDocument(String internalId) throws CMSException {
        
        DocumentImpl sharedDocument = getSharedRepository().getDocument(internalId);
        if( checkACL(sharedDocument)) {
            // default
            return sharedDocument;
        }
        else
            throw new DocumentForbiddenException();
    }
    
   public Personnalization getPersonnalization(String internalId) throws CMSException {
        
        DocumentImpl sharedDocument = getSharedRepository().getDocument(internalId);
        if( checkACL(sharedDocument)) {
            return userStorage.getUserData(internalId);
        }
        else
            throw new DocumentForbiddenException();
    }
 

    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        DocumentImpl document = (DocumentImpl) getSharedDocument(internalId);
        if (!document.isNavigable()) {
            document = getNavigationParent( document);
        }
        return new NavigationItemImpl(this, document);
    }


    
    
    private boolean checkACL(DocumentImpl doc)   {
        List<String> acls = doc.getACL();
        if( acls.size() == 0)
            return true;
        if( userName != null && acls.contains("group:members"))
            return true;
        return false;
        
    }

    public DocumentImpl getNavigationParent(DocumentImpl document) throws CMSException {
        DocumentImpl docImpl = (DocumentImpl) document;
        DocumentImpl parent = null;
        do  {
            DocumentImpl parentTmp = (DocumentImpl) getSharedDocument(docImpl.getParentInternalId());
            if( checkACL(parentTmp))
                parent = parentTmp;
            else
                docImpl = parentTmp;
        } while(parent == null);
        
        return parent;

    }

    public List<DocumentImpl> getNavigationChildren(DocumentImpl document) throws CMSException {
        DocumentImpl docImpl = (DocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<DocumentImpl> children = new ArrayList<>();
        for (String id : childrenId) {
            DocumentImpl sharedDocument = getSharedDocument(id);
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

    
    @Override
    public void publish( String id) throws CMSException {
        try {
        
        DocumentImpl doc = getSharedDocument(id);
        DocumentImpl existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getSharedDocument(id);
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        DocumentImpl publishedParent = null;
        String parentId = doc.getParentInternalId();
        
        while(  publishedParent == null && parentId != null)   {
            try {
                publishedParent = publishRepository.getSharedDocument(parentId);
            } catch(CMSException e)    {
                // not found
                DocumentImpl parent = getSharedDocument(parentId);
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
                DocumentImpl publishedChild = null;
                try {
                    publishedChild = publishRepository.getSharedDocument(childId);
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
        DocumentImpl publishedDoc = (DocumentImpl) doc.duplicateForPublication(publishedParentId, childrenId, publishRepository);
        publishRepository.getUserStorage().addDocument(publishedDoc.getInternalID(), publishedDoc, false);
        
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
     }
    
    
    public UserStorage getUserStorage()  {
        return userStorage;
    }




}
