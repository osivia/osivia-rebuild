package org.osivia.portal.core.container.persistent;

import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSContentEvent;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.CMSRepositoryEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;


public class CMSListener implements RepositoryListener{
    
    private final String nameSpace;
    
    

    private long version = 0;
    
    
    
    public long getVersion() {
        return version;
    }


    public CMSListener(String nameSpace) {
        super();
        this.nameSpace = nameSpace;
        
        
    }


    public String getNameSpace() {
        return nameSpace;
    }
    
    @Override
    public void contentModified(CMSEvent e) {
        boolean makeDirtyNode = false;
        if (e instanceof CMSContentEvent) {
            Document sourceDocument = ((CMSContentEvent) e).getSourceDocument();
            if (sourceDocument instanceof Space) {

                String templated = (String) sourceDocument.getProperties().get("osivia.connect.templated");
                if (!"false".equals(templated)) {
                    makeDirtyNode = true;
                }

            }
        }
        if (e instanceof CMSRepositoryEvent) {
            makeDirtyNode = true;
        }

        if (makeDirtyNode) {
            version++;
            DynamicPortalObjectContainer.clearCache();
        }
    }


}
