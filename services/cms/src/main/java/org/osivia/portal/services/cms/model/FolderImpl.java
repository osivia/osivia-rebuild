package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.services.cms.repository.InMemoryUserRepository;

public class FolderImpl extends DocumentImpl  {



    public FolderImpl(InMemoryUserRepository repository, String id, String name, String parentId,String spaceId, List<String> childrenId,Map<String, Object> properties) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);


    }

    
    public boolean isNavigable()    {
        return true;
    }
    

}
