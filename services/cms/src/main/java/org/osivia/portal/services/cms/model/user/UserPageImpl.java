package org.osivia.portal.services.cms.model.user;

import java.util.List;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.services.cms.model.share.PageImpl;
import org.osivia.portal.services.cms.model.share.SpaceImpl;
import org.osivia.portal.services.cms.repository.spi.UserData;

public class UserPageImpl extends UserDocumentImpl implements Page, Templateable{
    
    public UserPageImpl(PageImpl shareDocument, UserData userDatas) {
        super(shareDocument, userDatas);
    }

    
    public List<ModuleRef> getModuleRefs() {
        return ((PageImpl) shareDocument).getModuleRefs();
    }
    
    public UniversalID getTemplateId() {
        return ((PageImpl) shareDocument).getTemplateId();
    }
    
    public List<String> getInheritedRegions() {
        return ((PageImpl) shareDocument).getInheritedRegions();
   }


}
