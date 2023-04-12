package org.osivia.portal.api.cms.service;

import java.util.List;

public class MergeParameters {
    
    boolean mergeStyles;
    boolean mergeProfiles;
    List<String> pagesId;
    
    public boolean isMergeStyles() {
        return mergeStyles;
    }
    
    public void setMergeStyles(boolean mergeStyles) {
        this.mergeStyles = mergeStyles;
    }
    
    public boolean isMergeProfiles() {
        return mergeProfiles;
    }
    
    public void setMergeProfiles(boolean mergeProfiles) {
        this.mergeProfiles = mergeProfiles;
    }
    
    public List<String> getPagesId() {
        return pagesId;
    }
    
    public void setPagesId(List<String> pagesId) {
        this.pagesId = pagesId;
    }
    

}
