package org.osivia.portal.core.layouts;

import java.util.Set;

public class DynamicLayoutInfos {
    
    Set<String> regions;
    
    boolean maximized;

    
    
    public boolean isMaximized() {
		return maximized;
	}


	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}


	public Set<String> getRegions() {
        return regions;
    }



    


    public DynamicLayoutInfos( Set<String> regions, boolean maximized) {
        super();
        this.regions = regions;
        this.maximized = maximized;
    }
    


}
