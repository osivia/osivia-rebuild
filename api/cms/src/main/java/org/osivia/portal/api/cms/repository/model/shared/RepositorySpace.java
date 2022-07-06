package org.osivia.portal.api.cms.repository.model.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

public class RepositorySpace extends RepositoryDocument implements Space {

    /**
     * 
     */
    private static final long serialVersionUID = 5650412051545734668L;
    
    private UniversalID templateID;
    private List<Profile> profiles;
    private List<String> styles;
    
    public RepositorySpace(BaseUserRepository userRepository, EcmDocument nativeItem, String internalID, String name, String parentId, String spaceId,
            List<String> childrenId, Map<String, Object> properties, UniversalID templateID) {
        super(userRepository, nativeItem, internalID, name, parentId, spaceId, childrenId, properties);
        this.templateID = templateID;
        this.profiles = new ArrayList<>();
        this.styles = new ArrayList<>();
    }

    @Override
    public UniversalID getTemplateId() {
        return templateID;
    }

    @Override
    public List<ModuleRef> getModuleRefs() {
        return new ArrayList<ModuleRef>();
    }

    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }

    @Override
    public List<String> getStyles() {
        return styles;
    }

}
