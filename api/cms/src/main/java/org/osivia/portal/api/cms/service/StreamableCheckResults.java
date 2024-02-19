package org.osivia.portal.api.cms.service;

import java.util.ArrayList;
import java.util.List;

public class StreamableCheckResults {
    
    List<StreamableCheckResult> items = new ArrayList<>();
   
    public List<StreamableCheckResult> getItems() {
        return items;
    }
    
    public void setItems(List<StreamableCheckResult> results) {
        this.items = results;
    }
    
}
