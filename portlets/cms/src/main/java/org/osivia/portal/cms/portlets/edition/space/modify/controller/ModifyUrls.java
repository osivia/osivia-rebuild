package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.util.List;

import org.osivia.portal.api.cms.model.Profile;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class ModifyUrls {
    
    private List<String> modifyProfileUrls;
    private String addProfileUrl;
    
    public List<String> getModifyProfileUrls() {
        return modifyProfileUrls;
    }
    
    public void setModifyProfileUrls(List<String> modifyProfileUrls) {
        this.modifyProfileUrls = modifyProfileUrls;
    }
    
    public String getAddProfileUrl() {
        return addProfileUrl;
    }
    
    public void setAddProfileUrl(String addProfileUrl) {
        this.addProfileUrl = addProfileUrl;
    }

    


}
