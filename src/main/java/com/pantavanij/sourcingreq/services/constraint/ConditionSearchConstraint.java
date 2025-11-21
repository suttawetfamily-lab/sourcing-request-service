package com.pantavanij.sourcingreq.services.constraint;

import com.pantavanij.sourcingreq.services.validator.ConditionSearchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConditionSearchValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionSearchConstraint {
    String message() default "Input invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}