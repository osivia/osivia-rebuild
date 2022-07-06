package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.util.List;

import org.osivia.portal.api.cms.model.Profile;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyForm {
    
    private List<Profile> profiles;

    private List<String> styles;


    
    public List<String> getStyles() {
        return styles;
    }


    
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }


    public List<Profile> getProfiles() {
        return profiles;
    }

    
    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
    
    

}
