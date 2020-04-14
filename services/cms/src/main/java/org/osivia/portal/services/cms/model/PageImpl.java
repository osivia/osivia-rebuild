package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.services.cms.repository.CMSUserRepository;

public class PageImpl extends DocumentImpl implements Page {

    private List<ModuleRef> moduleRefs;

    public PageImpl(CMSUserRepository repository, String id, String name, String parentId, List<String> childrenId,Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name, parentId, childrenId, properties);
        this.moduleRefs = moduleRefs;

    }

    @Override
    public List<ModuleRef> getModuleRefs() {
        return moduleRefs;
    }

}
