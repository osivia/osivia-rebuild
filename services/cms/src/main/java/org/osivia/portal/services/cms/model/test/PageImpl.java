package org.osivia.portal.services.cms.model.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.services.cms.repository.test.InMemoryUserRepository;

public class PageImpl extends NuxeoMockDocumentImpl implements Page {

    private List<ModuleRef> moduleRefs;
    private UniversalID templateId;
    List<String> inheritedRegions = new ArrayList<>();
    

    
    public void setInheritedRegions(List<String> inheritedRegions) {
        this.inheritedRegions = inheritedRegions;
    }

    public PageImpl(InMemoryUserRepository repository, String id, String name, UniversalID templateId, String parentId, String spaceId, List<String> childrenId,Map<String, Object> properties, List<ModuleRef> moduleRefs) {
        super(repository, id, name, parentId, spaceId,childrenId, properties);
        this.templateId = templateId;
        this.moduleRefs = moduleRefs;
        subTypes = Arrays.asList(new String[]{"document"});

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

    @Override
    public List<String> getInheritedRegions() {
         return inheritedRegions;
    }


}
