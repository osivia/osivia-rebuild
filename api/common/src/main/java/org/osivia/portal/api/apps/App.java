package org.osivia.portal.api.apps;

import java.util.Locale;
import java.util.Map;

/**
 * Application descriptor
 * 
 * @author jsste
 */
public class App { 
    
    /** display name */
    protected String displayName;
    /** id */    
    private String id;
    /** icon location */        
    public String iconLocation;
    
    
    
    public String getIconLocation() {
        return iconLocation;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getId() {
        return id;
    }

    public App( String id, String displayName, String iconLocation) {
        super();
        this.displayName = displayName;
        this.id = id;
        this.iconLocation = iconLocation;
    }
    
    
}
