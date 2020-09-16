package org.osivia.portal.services.cms.model.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.services.cms.repository.test.InMemoryUserRepository;

public class FolderImpl extends NuxeoMockDocumentImpl  {


    private static final long serialVersionUID = -4961203732384812600L;


    public FolderImpl(InMemoryUserRepository repository, String id, String name, String parentId,String spaceId, List<String> childrenId,Map<String, Object> properties) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);
        subTypes = Arrays.asList(new String[]{"folder,document"});
    }

    
    public boolean isNavigable()    {
        return true;
    }


    @Override
    public String getType() {
        return "folder";
    }
    
    
    

}
