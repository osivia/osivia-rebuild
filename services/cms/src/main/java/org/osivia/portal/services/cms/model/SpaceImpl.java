package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.services.cms.repository.user.InMemoryUserRepository;

public class SpaceImpl extends NuxeoMockDocumentImpl implements Space {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;

    public SpaceImpl(InMemoryUserRepository repository, String id, String name, UniversalID templateId, List<String> childrenId, Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name,null, id,childrenId, properties);
        this.templateId = templateId    ;
        this.moduleRefs = moduleRefs;

    }

    @Override
    public List<ModuleRef> getModuleRefs() {
        return moduleRefs;
    }
    
    @Override
    public UniversalID getTemplateId() {
        return templateId;
    }

    public boolean isNavigable()    {
        return true;
    }
    
}
