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
    
    /** show in add menu */     
    private boolean showInAdministrationMenu = false;
    
    /** show tab selection */     
    private boolean supportTabSelection = false;
    
    public String getIconLocation() {
        return iconLocation;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getId() {
        return id;
    }
    
    
    public boolean isShowInAdministrationMenu() {
        return showInAdministrationMenu;
    }

    
    public void setShowInAdministrationMenu(boolean showInAdministrationMenu) {
        this.showInAdministrationMenu = showInAdministrationMenu;
    }

    
    public boolean isSupportTabSelection() {
        return supportTabSelection;
    }

    
    public void setSupportTabSelection(boolean supportTabSelection) {
        this.supportTabSelection = supportTabSelection;
    }

    public App( String id, String displayName, String iconLocation) {
        super();
        this.displayName = displayName;
        this.id = id;
        this.iconLocation = iconLocation;
    }
    
    
}
