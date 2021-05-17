package org.osivia.portal.api.cms.repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.UserData;
import org.osivia.portal.api.cms.repository.UserStorage;
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.user.UserDatasImpl;

/**
 * In memory sample storage
 * 
 */
public abstract class BaseUserStorage implements UserStorage  {
    

    
    private BaseUserRepository userRepository;
    
    public void setUserRepository(BaseUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    protected SharedRepository getSharedRepository()    {
        return userRepository.getSharedRepository();
    }
    
    protected BaseUserRepository getUserRepository() {
        return userRepository;
    }
    

  

    @Override
    public abstract UserData getUserData(String internalID) throws CMSException ;


  


    

    
}
