package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Space extends Document {
    public List<ModuleRef> getModuleRefs();

}
