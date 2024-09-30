package org.osivia.portal.api.cms.service;

import java.util.List;

import org.osivia.portal.api.cms.model.Profile;

public class MergeParameters {
    
    boolean mergeStyles;
    List<String> mergeProfiles;
    
    public List<String> getMergeProfiles() {
        return mergeProfiles;
    }

    
    public void setMergeProfiles(List<String> mergeProfiles) {
        this.mergeProfiles = mergeProfiles;
    }

    List<String> pagesId;
    
    
    public List<String> getPagesId() {
        return pagesId;
    }
    
    public void setPagesId(List<String> pagesId) {
        this.pagesId = pagesId;
    }
    

}
