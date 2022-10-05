package org.osivia.portal.cms.portlets.edition.web.page.properties.controller;

import java.util.regex.Pattern;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Properties form validator.
 * 
 * @author Jean sébastien steux
 * @see Validator
 */
@Component
public class WebPagePropertiesFormValidator implements Validator {

    /**
     * Constructor.
     */
    public WebPagePropertiesFormValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return WebPagePropertiesForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "NotEmpty");
        
        // illegal characters : /\:*?<>!
        String id = errors.getFieldValue("id").toString();
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_-])*");
        if(!pattern.matcher(id).matches())   {
            errors.rejectValue("id", "MODIFY_PAGE_PROPERTIES_ID_INVALID");
        }
        
    }

}
