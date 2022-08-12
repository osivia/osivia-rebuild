package org.osivia.portal.api.cms.repository.model.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

public class MemoryRepositoryPage extends MemoryRepositoryDocument implements Page {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;
    List<String> inheritedRegions = new ArrayList<>();
    

    
    public void setInheritedRegions(List<String> inheritedRegions) {
        this.inheritedRegions = inheritedRegions;
    }

    public MemoryRepositoryPage(BaseUserRepository repository, String id, String name, UniversalID templateId, String parentId, String spaceId, List<String> childrenId,Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, "page", id, name, parentId, spaceId,childrenId, properties);
        this.templateId = templateId;
        this.moduleRefs = moduleRefs;
        supportedSubTypes = Arrays.asList(new String[]{"document", "page"});

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
