package com.pantavanij.sourcingreq.services.validator;

import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ConditionSearchValidator implements ConstraintValidator<ConditionSearchConstraint, List<ConditionSearchRequest>> {

    @Override
    public void initialize(ConditionSearchConstraint conditionList) { }

    @Override
    public boolean isValid(List<ConditionSearchRequest> conditionSearchList, ConstraintValidatorContext cxt) {

        for (ConditionSearchRequest conditionSearchRequest: conditionSearchList) {
            if (StringUtils.isNotBlank(conditionSearchRequest.getSearchValue())
                    && conditionSearchRequest.getSearchValue().length() < 3) {
                cxt.disableDefaultConstraintViolation();
                cxt.buildConstraintViolationWithTemplate(ApiMessage.E7034.name() + ":" + ApiMessage.E7034.description()).addConstraintViolation();
                return false;
            }
        }

        return true;
    }

}
