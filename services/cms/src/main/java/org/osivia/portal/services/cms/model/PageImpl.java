package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.services.cms.repository.InMemoryUserRepository;

public class PageImpl extends NuxeoMockDocumentImpl implements Page {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;
    

    public PageImpl(InMemoryUserRepository repository, String id, String name, UniversalID templateId, String parentId, String spaceId, List<String> childrenId,Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);
        this.templateId = templateId;
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
