package org.osivia.portal.cms.portlets.edition.web.page.properties.controller;

import org.osivia.portal.api.cms.UniversalID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebPagePropertiesForm {
    
    private String id;
    

    private String templateId;

    private UniversalID templateSpaceId; 


    
    public String getTemplateId() {
        return templateId;
    }


    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    
    public UniversalID getTemplateSpaceId() {
        return templateSpaceId;
    }

    
    public void setTemplateSpaceId(UniversalID templateSpaceId) {
        this.templateSpaceId = templateSpaceId;
    }


    public String getId() {
        return id;
    }


    
    public void setId(String id) {
        this.id = id;
    }
}
