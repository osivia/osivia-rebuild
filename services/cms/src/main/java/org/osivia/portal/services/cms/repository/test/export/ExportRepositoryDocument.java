package org.osivia.portal.services.cms.repository.test.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;

public class ExportRepositoryDocument {
    
    public String id;
    public String type;    
    public Map<String,Object> properties;
    public List<String > acls;
    public List<ModuleRef> moduleRefs;
    public UniversalID templateId;
    public List<Profile> profiles;
    public List<String> styles;
    public List<String> inheritedRegions;    
    public EcmDocument nativeItem;
    public List<ExportRepositoryDocument> children;

}
