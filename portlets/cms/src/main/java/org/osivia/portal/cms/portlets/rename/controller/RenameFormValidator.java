package org.osivia.portal.cms.portlets.rename.controller;

import java.util.regex.Pattern;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Rename form validator.
 * 
 * @author Cédric Krommenhoek
 * @see Validator
 */
@Component
public class RenameFormValidator implements Validator {

    /**
     * Constructor.
     */
    public RenameFormValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return RenameForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "NotEmpty");
        
        // illegal characters : /\:*?<>!
        String title = errors.getFieldValue("title").toString();
        Pattern pattern = Pattern.compile("([^/\\\\:*?\\\"<>|])*");
        if(!pattern.matcher(title).matches())   {
            errors.rejectValue("title", "InvalidCharacter");
        }
        
    }

}
