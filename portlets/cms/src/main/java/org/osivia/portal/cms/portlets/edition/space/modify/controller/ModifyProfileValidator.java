package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import java.util.regex.Pattern;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Modify profile form validator.
 * 
 * @author Jean-Sébastien Steux
 * @see Validator
 */
@Component
public class ModifyProfileValidator implements Validator {

    /**
     * Constructor.
     */
    public ModifyProfileValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ModifyProfileForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "profile.name", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "profile.role", "NotEmpty");
    }

}
