package org.osivia.portal.cms.portlets.edition.page.apps.add.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/**
 * The Class EditionStatus.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class AddModel {
    
    public boolean preview = false;
    public boolean pageEdition = false;
    public boolean supportPreview = false;
    public boolean havingPublication = false;
    public String remoteUser = null;
    public boolean manageable = false;
    public boolean modifiable = false;
    public List<String> acls = new ArrayList<>();   
    public String toolbar = null;
    






    private Map<String, String> locales;
    
    
    
    public Map<String, String> getLocales() {
        return locales;
    }





    
    public void setLocales(Map<String, String> locales) {
        this.locales = locales;
    }





    public boolean isManageable() {
        return manageable;
    }




    
    public void setManageable(boolean manageable) {
        this.manageable = manageable;
    }




    public String getRemoteUser() {
        return remoteUser;
    }



    
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }



    public AddModel() {
        super();
    }


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

    public boolean isModifiable() {
        return modifiable;
    }

    
    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public List<String> getAcls() {
        return acls;
    }
    
    public void setAcls(List<String> acls) {
        this.acls = acls;
    }

    
    public String getToolbar() {
        return toolbar;
    }



    
    public void setToolbar(String toolbar) {
        this.toolbar = toolbar;
    }

    
}
