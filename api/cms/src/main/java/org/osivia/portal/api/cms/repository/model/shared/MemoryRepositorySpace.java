package org.osivia.portal.api.cms.repository.model.shared;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

public class MemoryRepositorySpace extends MemoryRepositoryDocument implements Space {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;

    public MemoryRepositorySpace(BaseUserRepository repository, String id, String name, UniversalID templateId, List<String> childrenId, Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name,null, id,childrenId, properties);
        this.templateId = templateId    ;
        this.moduleRefs = moduleRefs;
        supportedSubTypes = Arrays.asList(new String[]{ "page"});
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
    
}
