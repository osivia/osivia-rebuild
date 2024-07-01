package org.osivia.portal.cms.portlets.edition.page.acls.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AclsForm {
    
 
    private List<String> profiles;
    private List<String> unreferencedLdapRoles = new ArrayList<>();
    
    
    public List<String> getUnreferencedLdapRoles() {
        return unreferencedLdapRoles;
    }

    
    public void setUnreferencedLdapRoles(List<String> unreferencedLdapRoles) {
        this.unreferencedLdapRoles = unreferencedLdapRoles;
    }


    public List<String> getProfiles() {
        return profiles;
    }

    
    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }
  
}
