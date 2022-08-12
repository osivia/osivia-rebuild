package org.osivia.portal.api.cms.repository.model.shared;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.repository.BaseUserRepository;

public class MemoryRepositoryFolder extends MemoryRepositoryDocument  {


    private static final long serialVersionUID = -4961203732384812600L;


    public MemoryRepositoryFolder(BaseUserRepository repository, String id, String name, String parentId,String spaceId, List<String> childrenId,Map<String, Object> properties) {
        super(repository, "folder", id, name, parentId, spaceId,childrenId, properties);
        supportedSubTypes = Arrays.asList(new String[]{"folder,document"});
    }

    
    public boolean isNavigable()    {
        return true;
    }


    @Override
    public String getType() {
        return "folder";
    }
    
    
    

}
