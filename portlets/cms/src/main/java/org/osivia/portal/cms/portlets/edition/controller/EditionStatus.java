package org.osivia.portal.cms.portlets.edition.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
/**
 * The Class EditionStatus.
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)

public class EditionStatus {
    
    public boolean preview = false;

    
    public boolean isPreview() {
        return preview;
    }

    
    public void setPreview(boolean preview) {
        this.preview = preview;
    }
    
    public boolean supportPreview = false;


    
    public boolean isSupportPreview() {
        return supportPreview;
    }


    
    public void setSupportPreview(boolean supportPreview) {
        this.supportPreview = supportPreview;
    } 
    
    
}
