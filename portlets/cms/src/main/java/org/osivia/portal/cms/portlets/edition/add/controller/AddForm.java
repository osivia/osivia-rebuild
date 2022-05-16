package org.osivia.portal.cms.portlets.edition.add.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.osivia.portal.api.apps.App;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddForm {
    
    private Locale locale;
    private List<App> apps;
   
    
    public List<App> getApps() {
        return apps;
    }

     public void setApps(List<App> apps) {
        this.apps = apps;
    }

    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }


}
