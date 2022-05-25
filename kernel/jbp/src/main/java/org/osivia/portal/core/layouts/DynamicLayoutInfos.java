package org.osivia.portal.core.layouts;

import java.util.Set;

public class DynamicLayoutInfos {
    
    String code;
    
    Set<String> regions;

    
    
    public Set<String> getRegions() {
        return regions;
    }


    public String getCode() {
        return code;
    }

    
    public void setCode(String code) {
        this.code = code;
    }

    


    public DynamicLayoutInfos(String code, Set<String> regions) {
        super();
        this.code = code;
        this.regions = regions;
    }
    


}
