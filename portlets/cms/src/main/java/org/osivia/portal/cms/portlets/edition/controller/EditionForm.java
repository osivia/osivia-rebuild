package org.osivia.portal.cms.portlets.edition.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditionForm {
    
    private Locale locale;
    
    private Map<String, String> locales;
    
    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Map<String, String> getLocales() {
        return locales;
    }
    
    public void setLocales(Map<String, String> locales) {
        this.locales = locales;
    }  

}
