package org.osivia.portal.cms.portlets.edition.page.apps.modify.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyForm {
    
    private String title;
    private boolean displayTitle;
    private boolean displayPanel;
    private boolean hideIfEmpty;   

   
    
    
    public boolean isHideIfEmpty() {
        return hideIfEmpty;
    }




    
    public void setHideIfEmpty(boolean hideIfEmpty) {
        this.hideIfEmpty = hideIfEmpty;
    }




    public boolean isDisplayPanel() {
        return displayPanel;
    }



    
    public void setDisplayPanel(boolean displayPanel) {
        this.displayPanel = displayPanel;
    }



    public boolean isDisplayTitle() {
        return displayTitle;
    }


    
    public void setDisplayTitle(boolean displayTitle) {
        this.displayTitle = displayTitle;
    }


    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }



}
