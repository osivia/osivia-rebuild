package org.osivia.portal.api.cms.repository.model.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

public class MemoryRepositorySpace extends MemoryRepositoryDocument implements Space {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;
    private List<Profile> profiles;

    public MemoryRepositorySpace(BaseUserRepository repository, String id, String name, UniversalID templateId, List<String> childrenId, Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name,null, id,childrenId, properties);
        this.templateId = templateId    ;
        this.moduleRefs = moduleRefs;
        supportedSubTypes = Arrays.asList(new String[]{ "page"});
        this.profiles = new ArrayList<>();
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


    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }
    
}
