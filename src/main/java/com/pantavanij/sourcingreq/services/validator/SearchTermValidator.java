package com.pantavanij.sourcingreq.services.validator;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SearchTermValidator implements ConstraintValidator<SearchTermConstraint, String> {

    @Override
    public void initialize(SearchTermConstraint searchTerm) { }

    @Override
    public boolean isValid(String searchTerm, ConstraintValidatorContext cxt) {
         if (searchTerm.equals("") || searchTerm.length() >= 3) {
             return true;
         } else {
             cxt.disableDefaultConstraintViolation();
             cxt.buildConstraintViolationWithTemplate(ApiMessage.E7034.name() + ":" + ApiMessage.E7034.description()).addConstraintViolation();
             return false;
         }
    }

}
