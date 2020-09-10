package org.osivia.portal.cms.portlets.edition.controller;

import java.util.ArrayList;
import java.util.List;

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
    public boolean pageEdition = false;
    public boolean supportPreview = false;
    public boolean havingPublication = false;
    


    public List<String> subtypes = new ArrayList<>();
    
     
    public List<String> getSubtypes() {
        return subtypes;
    }



    public void setSubtypes(List<String> subtypes) {
        this.subtypes = subtypes;
    }



    public boolean isPageEdition() {
        return pageEdition;
    }


    
    public void setPageEdition(boolean pageEdition) {
        this.pageEdition = pageEdition;
    }


    public boolean isPreview() {
        return preview;
    }

    
    public void setPreview(boolean preview) {
        this.preview = preview;
    }
    

    
    public boolean isSupportPreview() {
        return supportPreview;
    }

    
    public void setSupportPreview(boolean supportPreview) {
        this.supportPreview = supportPreview;
    } 
    
    
    public boolean isHavingPublication() {
        return havingPublication;
    }

   
    public void setHavingPublication(boolean havingPublication) {
        this.havingPublication = havingPublication;
    }

    
}
