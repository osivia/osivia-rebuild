package org.osivia.portal.services.cms.model.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.services.cms.repository.BaseUserRepository;

public class PageImpl extends DocumentImpl  {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;
    List<String> inheritedRegions = new ArrayList<>();
    

    
    public void setInheritedRegions(List<String> inheritedRegions) {
        this.inheritedRegions = inheritedRegions;
    }

    public PageImpl(BaseUserRepository repository, String id, String name, UniversalID templateId, String parentId, String spaceId, List<String> childrenId,Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);
        this.templateId = templateId;
        this.moduleRefs = moduleRefs;
        supportedSubTypes = Arrays.asList(new String[]{"document"});

    }
    
    


    public List<ModuleRef> getModuleRefs() {
        return moduleRefs;
    }


    public UniversalID getTemplateId() {
        return templateId;
    }

    
    public boolean isNavigable()    {
        return true;
    }


    public List<String> getInheritedRegions() {
         return inheritedRegions;
    }


}
