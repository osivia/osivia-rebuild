package org.osivia.portal.api.cms.repository;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.osivia.portal.api.cms.CMSPortalControllerContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.UpdateScope;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.RepositoryContentEvent;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.user.NavigationItemImpl;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.service.GroupService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.imports.IPortalImportManager;


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


public abstract class BaseUserRepository implements UserRepository {

 
	private static final String OSIVIA_REPOSITORY_ADMINISTRATOR = "osivia.repository.administrator.";
	
    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";
    
    public static String SUPERUSER_NAME = "superuser";

    protected SharedRepositoryKey repositoryKey;

    protected List<RepositoryListener> listeners;
    
    protected BaseUserRepository publishRepository;
    


    private boolean previewRepository = false;
    
    private String userName = null;
    
    protected boolean batchMode = false;
    
    UserStorage userStorage;
    
    public ThreadLocal<CMSPortalControllerContext> portalCtx = new ThreadLocal<CMSPortalControllerContext>();
    
    private RepositoryFactory repositoryFactory;
   

    /** The group service */
    private GroupService groupService;
    
    public GroupService getGroupService() {
        if( groupService == null)
            groupService = DirServiceFactory.getService(GroupService.class);
        return groupService;
    }
    
    

    public BaseUserRepository( RepositoryFactory repositoryFactory, SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName, UserStorage userStorage) {
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
        this.repositoryFactory=repositoryFactory;
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
        PortalControllerContext ctx = portalCtx.get().getPortalControllerContext();
        if( ctx.getHttpServletRequest() != null)    {
             principal = ctx.getHttpServletRequest().getUserPrincipal();
        }
        return principal;
    }
    

    
	public boolean isManager() {

		if (isAdministrator())
			return true;

		Boolean repositoryAdministrator = Boolean.FALSE;
		
		PortalControllerContext ctx = portalCtx.get().getPortalControllerContext();

		if (ctx.getHttpServletRequest() != null) {

			repositoryAdministrator = (Boolean) ctx.getHttpServletRequest().getSession()
					.getAttribute(getRepositoryAdministratorKey());

			if (repositoryAdministrator == null) {

				HttpServletRequest request = ctx.getHttpServletRequest();
				repositoryAdministrator = Boolean.FALSE;

				if (StringUtils.isNotEmpty(getRepositoryName())) {
					String profile = System
							.getProperty("osivia.cms.repository." + getRepositoryName() + ".administrators.profile");
					if (StringUtils.isNotEmpty(profile) && request.isUserInRole(profile)) {
						repositoryAdministrator = Boolean.TRUE;
					}

				}
				
				// Store in session
				ctx.getHttpServletRequest().getSession().setAttribute(getRepositoryAdministratorKey(),
						repositoryAdministrator);
			}
		}

		return repositoryAdministrator.booleanValue();
	}



	private String getRepositoryAdministratorKey() {
		return OSIVIA_REPOSITORY_ADMINISTRATOR+ getUserName()+"."+getRepositoryName();
	}

    
    
    public boolean isAdministrator()    {
        PortalControllerContext ctx = portalCtx.get().getPortalControllerContext();
        if( ctx.getHttpServletRequest() != null &&  ctx.getHttpServletRequest().getUserPrincipal() != null)    {
            Boolean isAdministrator = (Boolean) ctx.getHttpServletRequest().getSession().getAttribute("osivia.isAdmin");
            if (isAdministrator == null) {
                isAdministrator = ctx.getHttpServletRequest().isUserInRole("Administrators");
                ctx.getHttpServletRequest().getSession().setAttribute("osivia.isAdmin", isAdministrator);
            }
            return isAdministrator;

        }
        return false;
    }
    
    
    @Override
    public void setPortalContext(CMSPortalControllerContext portalContext) {
        portalCtx.set(portalContext);
     }
    

    public PortalControllerContext getPortalContext() {
        return portalCtx.get().getPortalControllerContext();
     }
    
    public boolean isUserRefreshingPage() {
        return portalCtx.get().isUserRefreshingPage();
     }
    
    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        getSharedRepository().addListener(listener);
    }

    
    
    public void removeListener(RepositoryListener listener) {
        getSharedRepository().removeListener(listener);
    }
    
    public boolean isPreviewRepository() {
        return previewRepository;
    }


    private void init(SharedRepositoryKey repositoryKey) {
        

        boolean initRepository = false;
        if( getSharedRepository() == null)    {
            repositoryFactory.getSharedRepositories().put(repositoryKey, new SharedRepository(repositoryKey.getRepositoryName(), userStorage));   
            initRepository = true;
        }
        
        
       
        
        if(initRepository)  {
            startInitBatch();    
        }
        
        
    }


    public void startInitBatch() {
        
        IPortalImportManager portalImportManager = Locator.getService(IPortalImportManager.class);
   
        portalImportManager.stopRequests();
        

        
        clearCaches();
        
        batchMode = true;
        getSharedRepository().beginBatch(userStorage);
        boolean error = false;

        try {
            initDocuments();
            
        }   catch(Exception e)  {
            userStorage.handleError();
            error = true;
            throw e;
        }   finally    {
            getSharedRepository().endBatch(userStorage);
            
            if( error == false)  {
                UpdateInformations infos = new UpdateInformations(getRepositoryName());
                try {
                    getSharedRepository().notifyUpdate( getUserStorage(), infos);
                } catch (CMSException e) {
                    throw new RuntimeException(e);
                }
            }
            
            batchMode = false;
            
            portalImportManager.restartRequests();            
        }
        
        
       
    }


    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryKey.getRepositoryName();
    }

   

    public SharedRepository getSharedRepository() {
         return repositoryFactory.getSharedRepositories().get(repositoryKey);
    }



    public RepositoryDocument getSharedDocument(String internalId) throws CMSException {
        return getSharedRepository().getDocument(getUserStorage(),internalId);
    }

    

    
    public Document getDocument(String internalId) throws CMSException {
        
    	if( isUserRefreshingPage())
    		reload(internalId);
        
        RepositoryDocument sharedDocument = getSharedRepository().getDocument(getUserStorage(),internalId);
        
        if( checkSecurity(sharedDocument)) {
            // default
            return sharedDocument;
        }
        else
            throw new DocumentForbiddenException();
    }
    
    
    public void reload(String internalId) throws CMSException {
        getSharedRepository().reload(getUserStorage(),internalId);
    }

    
   public Personnalization getPersonnalization(String internalId) throws CMSException {

        return userStorage.getUserData(internalId);

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
    public List<Locale> getLocales() {
        return Arrays.asList(Locale.FRENCH);
    }

    
    
    public UserStorage getUserStorage()  {
        return userStorage;
    }


    @Override
    public void notifyUpdate(UpdateInformations infos) throws CMSException {
        getSharedRepository().notifyUpdate(getUserStorage(), infos);
    }
    
    
    @Override
    public void handleUpdate(UpdateInformations infos) throws CMSException {
        getSharedRepository().handleUpdate(this, getUserStorage(), infos);
    }
    
    /**
     * Clear local and shared caches.
     */
    public void clearCaches()    {
        getSharedRepository().clear();
        if( publishRepository != null)
            publishRepository.clearCaches();
    }

    

    @Override
    public SpaceCacheBean getSpaceCacheInformations(String id) throws CMSException {
        return getSharedRepository().getSpaceCacheInformations(id);
    }
    
}
