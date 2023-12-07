package org.osivia.portal.cms.portlets.edition.page.creation.controller;

import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreationForm {
    
    private String id;
    private String displayName;
    private boolean noModel = false;
    private String model;
    private String target;    



    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isNoModel() {
        return noModel;
    }

    
    public void setNoModel(boolean noModel) {
        this.noModel = noModel;
    }


    public String getModel() {
        return model;
    }


    
    public void setModel(String model) {
        this.model = model;
    }

    
    public String getTarget() {
        return target;
    }

    
    public void setTarget(String target) {
        this.target = target;
    }

}

