package org.osivia.portal.cms.portlets.rename.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Rename form java-bean.
 * 
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RenameForm {

    /** Document title. */
    private String title;


    /**
     * Constructor.
     */
    public RenameForm() {
        super();
    }


    /**
     * Getter for title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for title.
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
