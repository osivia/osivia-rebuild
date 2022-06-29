package org.osivia.portal.api.cms.model;

import java.util.List;

public interface Space extends Document, Templateable, ModulesContainer {
    public List<ModuleRef> getModuleRefs();
    public List<Profile> getProfiles();

}
