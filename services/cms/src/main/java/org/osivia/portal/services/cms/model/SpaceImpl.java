package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Space;

public class SpaceImpl extends DocumentImpl implements Space {

    private List<ModuleRef> moduleRefs;

    public SpaceImpl(String id, Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(id, properties);
        this.moduleRefs = moduleRefs;

    }

    @Override
    public List<ModuleRef> getModuleRefs() {
        return moduleRefs;
    }

}
