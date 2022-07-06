package org.osivia.portal.cms.portlets.edition.space.modify.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Modify style form validator.
 * 
 * @author Jean-Sébastien Steux
 * @see Validator
 */
@Component
public class ModifyStyleValidator implements Validator {

    /**
     * Constructor.
     */
    public ModifyStyleValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ModifyStyleForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
    }

}
