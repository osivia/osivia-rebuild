package org.osivia.portal.cms.portlets.edition.page.apps.modify.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyForm {

    private String title;
    private boolean displayTitle;
    private boolean displayPanel;
    private boolean hideIfEmpty;
    private boolean supportTabSelection;
    
    
   
    private List<String> styles;
    private List<String> unimplementedStyles;
    



    /**
     * Linked layout item identifier.
     */
    private String linkedLayoutItemId;
    private List<String> profiles;

    



    /**
     * Constructor.
     */
    public ModifyForm() {
        super();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(boolean displayTitle) {
        this.displayTitle = displayTitle;
    }

    public boolean isDisplayPanel() {
        return displayPanel;
    }

    public void setDisplayPanel(boolean displayPanel) {
        this.displayPanel = displayPanel;
    }

    public boolean isHideIfEmpty() {
        return hideIfEmpty;
    }

    public void setHideIfEmpty(boolean hideIfEmpty) {
        this.hideIfEmpty = hideIfEmpty;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    public String getLinkedLayoutItemId() {
        return linkedLayoutItemId;
    }

    public void setLinkedLayoutItemId(String linkedLayoutItemId) {
        this.linkedLayoutItemId = linkedLayoutItemId;
    }
    
    public boolean isSupportTabSelection() {
        return supportTabSelection;
    }
    
    public void setSupportTabSelection(boolean supportTabSelection) {
        this.supportTabSelection = supportTabSelection;
    }

   
    public List<String> getProfiles() {
        return profiles;
    }

    
    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }
    
    public List<String> getUnimplementedStyles() {
        return unimplementedStyles;
    }
    
    public void setUnimplementedStyles(List<String> unimplementedStyles) {
        this.unimplementedStyles = unimplementedStyles;
    }

}
