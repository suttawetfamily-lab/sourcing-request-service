package com.pantavanij.sourcingreq.services.constraint;

import com.pantavanij.sourcingreq.services.validator.SearchTermValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SearchTermValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchTermConstraint {
    String message() default "Input invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}