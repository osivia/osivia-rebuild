package org.osivia.portal.api.cms.repository;

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
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.user.NavigationItemImpl;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.context.PortalControllerContext;


/**
 * Base user repository
 * 
 * Associated to a context (user, repository, state, language)
 * 
 * Manage cache, request, hierarchy, navigation, notification, transaction cooredination ...
 * 
 * Stores data in memory
 * Doesn't support user rights.
 */


public abstract class BaseUserRepository implements UserRepository, RepositoryListener {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";
    
    public static String SUPERUSER_NAME = "superuser";

    protected SharedRepositoryKey repositoryKey;

 

    protected List<RepositoryListener> listeners;
    
    protected BaseUserRepository publishRepository;
    
    private static Map<SharedRepositoryKey, SharedRepository>  sharedRepositories = new Hashtable<SharedRepositoryKey, SharedRepository>();
    
    private Map<String, Personnalization>  personnalizationMap = new Hashtable<String, Personnalization>();
    
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
            getSharedRepository().endBatch(userStorage);    
        }
        
        
    }


    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryKey.getRepositoryName();
    }

   

    public SharedRepository getSharedRepository() {
         return sharedRepositories.get(repositoryKey);
    }



    public RepositoryDocument getSharedDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(getUserStorage(),internalId);
    }

    
    public Document getDocument(String internalId) throws CMSException {
        
        RepositoryDocument sharedDocument = getSharedRepository().getDocument(getUserStorage(),internalId);
        if( checkSecurity(sharedDocument)) {
            // default
            return sharedDocument;
        }
        else
            throw new DocumentForbiddenException();
    }
    
   public Personnalization getPersonnalization(String internalId) throws CMSException {

        Personnalization personnalization = personnalizationMap.get(internalId);

        if (personnalization == null) {
            personnalizationMap.put(internalId, userStorage.getUserData(internalId));
        }

        return personnalizationMap.get(internalId);
    }
 

    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        RepositoryDocument document = (RepositoryDocument) getSharedDocument(internalId);
        if (!document.isNavigable()) {
            document = getNavigationParent( document);
        }
        return new NavigationItemImpl(this, document);
    }

    
    private boolean checkSecurity(RepositoryDocument document)  {
        try {
            getPersonnalization(document.getInternalID());
        } catch(Exception e) {
            return false;
        }
        return true;
    }
    

    public RepositoryDocument getNavigationParent(RepositoryDocument document) throws CMSException {
        RepositoryDocument docImpl = (RepositoryDocument) document;
        RepositoryDocument parent = null;
        do  {
            RepositoryDocument parentTmp = (RepositoryDocument) getSharedDocument(docImpl.getParentInternalId());
            if( checkSecurity(parentTmp))
                parent = parentTmp;
            else
                docImpl = parentTmp;
        } while(parent == null);
        
        return parent;

    }

    public List<RepositoryDocument> getNavigationChildren(RepositoryDocument document) throws CMSException {
        RepositoryDocument docImpl = (RepositoryDocument) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<RepositoryDocument> children = new ArrayList<>();
        for (String id : childrenId) {
            RepositoryDocument sharedDocument = getSharedDocument(id);
            if( sharedDocument.isNavigable())   {
                if( checkSecurity(sharedDocument))
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
        
        RepositoryDocument doc = getSharedDocument(id);
        RepositoryDocument existingPublishedDoc = null;
        try {
            existingPublishedDoc = publishRepository.getSharedDocument(id);
        } catch(CMSException e)    {
            // not found
        }
        
        // Search for first published parent
        RepositoryDocument publishedParent = null;
        String parentId = doc.getParentInternalId();
        
        while(  publishedParent == null && parentId != null)   {
            try {
                publishedParent = publishRepository.getSharedDocument(parentId);
            } catch(CMSException e)    {
                // not found
                RepositoryDocument parent = getSharedDocument(parentId);
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
                RepositoryDocument publishedChild = null;
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
                    
                    publishRepository.getUserStorage().updateDocument(publishedChild.getInternalID(), publishedChild, true);
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
            
            publishRepository.getUserStorage().updateDocument(publishedParent.getInternalID(), publishedParent, true);
        }     
        
        
        // create published object
        RepositoryDocument publishedDoc = (RepositoryDocument) doc.duplicateForPublication(publishedParentId, childrenId, publishRepository);
        publishRepository.getUserStorage().addDocument(publishedDoc.getInternalID(), publishedDoc, false);
        
        
        } catch( Exception e)   {
            throw new CMSException( e);
        }
     }
    
    
    public UserStorage getUserStorage()  {
        return userStorage;
    }




}
