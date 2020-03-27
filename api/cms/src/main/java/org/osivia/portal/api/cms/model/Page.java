package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Page extends Document {
    public List<ModuleRef> getModuleRefs();
}
