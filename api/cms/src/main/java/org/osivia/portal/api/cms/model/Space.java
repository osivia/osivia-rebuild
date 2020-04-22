package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Space extends Document, Templateable {
    public List<ModuleRef> getModuleRefs();

}
