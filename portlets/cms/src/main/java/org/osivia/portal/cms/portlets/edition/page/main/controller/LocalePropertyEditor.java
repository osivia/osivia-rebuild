package org.osivia.portal.cms.portlets.edition.page.main.controller;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * Person property editor.
 * 
 * @author Jean-Sébastien SSteux
 * @see PropertyEditorSupport
 */
@Component
public class LocalePropertyEditor extends PropertyEditorSupport {

    /**
     * Constructor.
     */
    public LocalePropertyEditor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsText() {
        Object value = this.getValue();

        String text;

        if ((value != null) && (value instanceof Locale)) {
            text = ((Locale) value).getLanguage();
        } else {
            text = null;
        }

        return text;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // Local group
        Locale locale ;

            locale = new Locale(text);


        this.setValue(locale);
    }

}
