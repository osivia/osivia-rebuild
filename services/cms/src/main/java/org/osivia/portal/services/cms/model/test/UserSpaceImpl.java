package org.osivia.portal.services.cms.model.test;

import java.util.List;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.services.cms.model.share.SpaceImpl;
import org.osivia.portal.services.cms.repository.spi.UserData;

public class UserSpaceImpl extends UserDocumentImpl implements Space, Templateable {
    
    public UserSpaceImpl(SpaceImpl shareDocument, UserData userDatas) {
        super(shareDocument, userDatas);
    }

    
    public List<ModuleRef> getModuleRefs() {
        return ((SpaceImpl) shareDocument).getModuleRefs();
    }
    
    public UniversalID getTemplateId() {
        return ((SpaceImpl) shareDocument).getTemplateId();
    }
}
