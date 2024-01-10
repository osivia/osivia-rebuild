package org.osivia.portal.cms.portlets.edition.page.creation.controller;

import java.util.regex.Pattern;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Creation form validator.
 * 
 * @author Jean sébastien steux
 * @see Validator
 */
@Component
public class CreationFormValidator implements Validator {

    /**
     * Constructor.
     */
    public CreationFormValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return CreationForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "NotEmpty");
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "displayName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "target", "MODIFY_PAGE_CREATION_TARGET_MANDATORY");
        
        // illegal characters : /\:*?<>!
        String id = errors.getFieldValue("id").toString();
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_-])*");
        if(!pattern.matcher(id).matches())   {
            errors.rejectValue("id", "MODIFY_PAGE_CREATION_ID_INVALID");
        }
        
        if( id.startsWith("_ID_"))  {
            errors.rejectValue("id", "MODIFY_PAGE_CREATION_ID_INVALID_START");
        }
            
        
        
    }

}
