package org.osivia.portal.cms.portlets.edition.tree.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Move form java-bean.
 * 
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MoveForm {


    public MoveForm() {
        super();
    }

    private String target;

    
    public String getTarget() {
        return target;
    }

    
    public void setTarget(String target) {
        this.target = target;
    }

}
