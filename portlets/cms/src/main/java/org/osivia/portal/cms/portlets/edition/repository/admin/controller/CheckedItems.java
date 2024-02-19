package org.osivia.portal.cms.portlets.edition.repository.admin.controller;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.service.StreamableCheckResults;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class CheckedItems
{
    StreamableCheckResults results;
    
    String confirmationMessage;
    
    boolean ok;


    
    
    public boolean isOk() {
        return ok;
    }


    
    public void setOk(boolean ok) {
        this.ok = ok;
    }


    public StreamableCheckResults getResults() {
        return results;
    }

    
    public void setResults(StreamableCheckResults results) {
        this.results = results;
    }


    
    
    public String getConfirmationMessage() {
        return confirmationMessage;
    }
    
    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }




}
