package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import org.osivia.portal.api.cms.model.Profile;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyProfileForm  {

    Profile profile;
    String callBackUrl;

    
    
    public String getCallBackUrl() {
        return callBackUrl;
    }


    
    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }


    public Profile getProfile() {
        return profile;
    }

    
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    
 
}
