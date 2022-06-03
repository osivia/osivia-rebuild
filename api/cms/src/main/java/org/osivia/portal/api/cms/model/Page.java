package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Page extends Document, Templateable,ModulesContainer {
    public List<ModuleRef> getModuleRefs();
    public List<String> getInheritedRegions();
}
