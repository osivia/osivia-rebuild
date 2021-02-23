package org.osivia.portal.services.cms.model.share;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.services.cms.repository.BaseUserRepository;

public class FolderImpl extends DocumentImpl  {


    private static final long serialVersionUID = -4961203732384812600L;


    public FolderImpl(BaseUserRepository repository, String id, String name, String parentId,String spaceId, List<String> childrenId,Map<String, Object> properties) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);
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